package main;

/* import table */

import logist.agent.Agent;
import logist.behavior.DeliberativeBehavior;
import logist.plan.Action;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.util.*;

/**
 * An optimal planner for one vehicle.
 */

class State {
	// State
	private City currentCity;
	private HashSet<Task> delivering;
	private HashSet<Task> delivered;
	private Integer totalAgentCapacity;

	public State(City currentCity, HashSet<Task> delivering, HashSet<Task> delivered, Integer totalAgentCapacity) {
		this.currentCity = currentCity;
		this.delivering = delivering;
		this.delivered = delivered;
		this.totalAgentCapacity = totalAgentCapacity;
	}

	public void setCurrentCity(City currentCity) {
		this.currentCity = currentCity;
	}

	public City getCurrentCity() {
		return currentCity;
	}

	public Set<Task> getDelivering() {
		return delivering;
	}

	public Set<Task> getDelivered() {
		return delivered;
	}

	public Integer getTotalAgentCapacity() {
		return totalAgentCapacity;
	}

	public Integer getCurrentAgentCapacity() {
		int agentLeftCapacity = totalAgentCapacity;
		for(Task task: delivering) {
			agentLeftCapacity -= task.weight;
		}

		return agentLeftCapacity;
	}

//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		State other = (State) o;
//
//		if(!currentCity.toString().equals(other.currentCity.toString())) {
//			return false;
//		}
//
//		if(!delivering.toString().equals(other.delivering.toString())) {
//			return false;
//		}
//
//		if(!delivered.toString().equals(other.delivered.toString())) {
//			return false;
//		}
//
//		/*
//		return true;
//	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		State state = (State) o;
		return Objects.equals(currentCity, state.currentCity) &&
				Objects.equals(delivering, state.delivering) &&
				Objects.equals(delivered, state.delivered) &&
				Objects.equals(totalAgentCapacity, state.totalAgentCapacity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(currentCity, delivering, delivered, totalAgentCapacity);
	}

	@Override
	protected State clone() {
		return new State(this.currentCity, (HashSet<Task>) this.delivering.clone(), (HashSet<Task>) this.delivered.clone(), this.totalAgentCapacity);
	}
}

class QueueParams {
	private State state;
	private Plan plan;
	private TaskSet tasksLeft;

	public QueueParams(State state, Plan plan, TaskSet tasksLeft) {
		this.state = state;
		this.plan = plan;
		this.tasksLeft = tasksLeft;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public TaskSet getTasksLeft() {
		return tasksLeft;
	}

	public void setTasksLeft(TaskSet tasksLeft) {
		this.tasksLeft = tasksLeft;
	}
}

public class DeliberativeAgent implements DeliberativeBehavior {

	enum Algorithm { BFS, ASTAR }
	
	/* Environment */
	Topology topology;
	TaskDistribution td;
	
	/* the properties of the agent */
	Agent agent;
	int capacity;

	/* the planning class */
	Algorithm algorithm;
	
	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {
		this.topology = topology;
		this.td = td;
		this.agent = agent;
		
		// initialize the planner
		int capacity = agent.vehicles().get(0).capacity();
		String algorithmName = agent.readProperty("algorithm", String.class, "ASTAR");
		
		// Throws IllegalArgumentException if algorithm is unknown
		algorithm = Algorithm.valueOf(algorithmName.toUpperCase());
	}
	
	@Override
	public Plan plan(Vehicle vehicle, TaskSet tasks) {
		Plan plan;

		// Compute the plan with the selected algorithm.
		switch (algorithm) {
		case ASTAR:
			plan = aStarPlan(vehicle, tasks);
			break;
		case BFS:
			plan = bfsPlan(vehicle, tasks);
			break;
		default:
			throw new AssertionError("Should not happen.");
		}		
		return plan;
	}
	
	private Plan naivePlan(Vehicle vehicle, TaskSet tasks) {
		City current = vehicle.getCurrentCity();
		Plan plan = new Plan(current);

		// SOME LOGS FOR DEBUGGING
		tasks.iterator().forEachRemaining(act -> {
			System.out.println(act.id + ": (" + act.weight + "-" + act.reward +") (" + act.pickupCity + "->" + act.deliveryCity + ")");
		});
		System.out.println("######");

		for (Task task : tasks) {
			// move: current city => pickup location
			for (City city : current.pathTo(task.pickupCity))
				plan.appendMove(city);

			plan.appendPickup(task);

			// move: pickup location => delivery location
			for (City city : task.path())
				plan.appendMove(city);

			plan.appendDelivery(task);

			// set current city
			current = task.deliveryCity;
		}
		return plan;
	}

	private Plan bfsPlan(Vehicle vehicle, TaskSet tasks) {
		// The BFS is going to iterate through all the possible states and return the most rewarding plan
		HashSet<Task> doingTasks = new HashSet<>();
		vehicle.getCurrentTasks().iterator().forEachRemaining(doingTasks::add);
		State startingState = new State(vehicle.getCurrentCity(), doingTasks, new HashSet<Task>(), vehicle.capacity());

		Plan bestPlan = doBFS(
				startingState,
				vehicle.costPerKm(),
				tasks
		);
		System.out.println("Best plan has been computed( " + bestPlan.totalDistanceUnits() + " ): " + bestPlan.toString());
		return bestPlan;
	}

	private Plan aStarPlan(Vehicle vehicle, TaskSet tasks) {
		HashSet<Task> doingTasks = new HashSet<>();
		vehicle.getCurrentTasks().iterator().forEachRemaining(doingTasks::add);
		State startingState = new State(vehicle.getCurrentCity(), doingTasks, new HashSet<Task>(), vehicle.capacity());

		Plan bestPlan = doAStar(
				startingState,
				vehicle.costPerKm(),
				tasks
		);
		System.out.println("Best plan has been computed( " + bestPlan.totalDistanceUnits() + " ): " + bestPlan.toString());
		return bestPlan;
	}

	private Plan copyPlan(City fromCity, Plan otherPlan) {
		List<Action> actions = new ArrayList<Action>();
		otherPlan.seal();
		otherPlan.iterator().forEachRemaining(actions::add);
		return new Plan(fromCity, actions);
	}

	private Plan doBFS(State initialState, Integer costPerKm, TaskSet initialTasksLeft) {
		HashMap<State, Long> statesMap = new HashMap<>();
		Queue<QueueParams> q = new LinkedList<>();
		Plan initialPlan = new Plan(initialState.getCurrentCity());
		Plan bestPlan = null;
		q.add(new QueueParams(initialState, initialPlan, initialTasksLeft));

		// Do BFS
		while(!q.isEmpty()) {
			QueueParams frontQueue = q.element(); q.remove();
			State state = frontQueue.getState();
			Plan plan = frontQueue.getPlan();
			TaskSet tasksLeft = frontQueue.getTasksLeft();

			// 1. Deliver tasks that can be delivered
			// Cannot modify the getDelivering array while iterating (will return a concurrency error)
			List<Task> toDeliver = new ArrayList<>();
			state.getDelivering().iterator().forEachRemaining(task -> {
				if(task.deliveryCity == state.getCurrentCity()) {
					toDeliver.add(task);
				}
			});
			toDeliver.forEach(task -> {
				plan.appendDelivery(task);
				state.getDelivering().remove(task);
				state.getDelivered().add(task);
			});

			// Also add the new state (with available tasks delivered)
			statesMap.put(state, plan.totalDistanceUnits() * costPerKm);

			// 2. Check if we are in a final state (of success)
			if(tasksLeft.size() == 0 && state.getDelivering().size() == 0) {
				plan.seal();
				System.out.println("Plan: " + plan.totalDistanceUnits());

				if(bestPlan == null || plan.totalDistanceUnits() < bestPlan.totalDistanceUnits() ) {
					bestPlan = plan;
				}

				continue;
			}

			// 3. Pick up tasks
			for(Task task: tasksLeft) {
				// Check if agent has capacity to pick up
				if(state.getCurrentAgentCapacity() >= task.weight && task.pickupCity == state.getCurrentCity()) {
					// Deep copy params for new recursion (so that we avoid java.util.ConcurrentModificationException)
					State newState = state.clone();
					newState.getDelivering().add(task);

					Plan newPlan = copyPlan(state.getCurrentCity(), plan);
					newPlan.appendPickup(task);

					TaskSet newTasksLeft = tasksLeft.clone();
					newTasksLeft.remove(task);

					// Add new state in queue
					if(statesMap.containsKey(newState) && !statesMap.containsKey(state)) {
						System.out.println(state.toString());
					}
					if(!statesMap.containsKey(newState) || (newPlan.totalDistanceUnits() * costPerKm < statesMap.get(state))) {
						statesMap.put(newState, newPlan.totalDistanceUnits() * costPerKm);
						q.add(new QueueParams(newState, newPlan, newTasksLeft));
					}
				}
			}

			// 4. Move to other city
			for(City toCity: state.getCurrentCity().neighbors()) {
				// Deep copy params for new recursion (so that we avoid java.util.ConcurrentModificationException)
				State newState = state.clone();
				newState.setCurrentCity(toCity);

				Plan newPlan = copyPlan(state.getCurrentCity(), plan);
				newPlan.appendMove(toCity);

				TaskSet newTasksLeft = tasksLeft.clone();

				// Add new state in queue
				if(!statesMap.containsKey(newState) || (newPlan.totalDistanceUnits() * costPerKm < statesMap.get(state))) {
					statesMap.put(newState, newPlan.totalDistanceUnits() * costPerKm);
					q.add(new QueueParams(newState, newPlan, newTasksLeft));
				}
			}
		}

		return bestPlan;
	}

	private Long computeHeuristic(State state, TaskSet tasksLeft) {
		long cost = 0;

		for(Task task: state.getDelivering()) {
			cost += task.pickupCity.distanceTo(task.deliveryCity);
		}

		for(Task task: tasksLeft) {
			cost += task.pickupCity.distanceTo(task.deliveryCity);
		}

		return cost;
	}

	private Plan doAStar(State initialState, Integer costPerKm, TaskSet initialTasksLeft) {
		HashMap<State, Long> statesMap = new HashMap<>();
		statesMap.put(initialState, computeHeuristic(initialState, initialTasksLeft) * costPerKm);

		Comparator<QueueParams> qpComparator = (p1, p2) -> (int) (
				(p1.getPlan().totalDistanceUnits() + computeHeuristic(p1.getState(), p1.getTasksLeft())) * costPerKm -
				(p2.getPlan().totalDistanceUnits() + computeHeuristic(p2.getState(), p2.getTasksLeft())) * costPerKm
		);

		PriorityQueue<QueueParams> pq = new PriorityQueue<>(qpComparator);
		Plan initialPlan = new Plan(initialState.getCurrentCity());
		pq.add(new QueueParams(initialState, initialPlan, initialTasksLeft));

		Plan bestPlan = null;

		while(!pq.isEmpty()) {
			QueueParams topQueue = pq.remove();
			State state = topQueue.getState();
			Plan plan = topQueue.getPlan();
			TaskSet tasksLeft = topQueue.getTasksLeft();

			// 1. Deliver tasks that can be delivered
			// Cannot modify the getDelivering array while iterating (will return a concurrency error)
			List<Task> toDeliver = new ArrayList<>();
			state.getDelivering().iterator().forEachRemaining(task -> {
				if(task.deliveryCity == state.getCurrentCity()) {
					toDeliver.add(task);
				}
			});
			toDeliver.forEach(task -> {
				plan.appendDelivery(task);
				state.getDelivering().remove(task);
				state.getDelivered().add(task);
			});

			// Also add the new state (with available tasks delivered)
			statesMap.put(state, (plan.totalDistanceUnits() + computeHeuristic(state, tasksLeft) ) * costPerKm);

			// 2. Check if we are in a final state (of success)
			if(tasksLeft.size() == 0 && state.getDelivering().size() == 0) {
				plan.seal();
				System.out.println("Plan: " + plan.totalDistanceUnits());
				if(bestPlan == null || plan.totalDistanceUnits() < bestPlan.totalDistanceUnits() ) {
					bestPlan = plan;
				}

				continue;
			}

			// 3. Pick up tasks
			for(Task task: tasksLeft) {
				// Check if agent has capacity to pick up
				if(state.getCurrentAgentCapacity() >= task.weight && task.pickupCity == state.getCurrentCity()) {
					// Deep copy params for new recursion (so that we avoid java.util.ConcurrentModificationException)
					State newState = state.clone();
					newState.getDelivering().add(task);

					Plan newPlan = copyPlan(state.getCurrentCity(), plan);
					newPlan.appendPickup(task);

					TaskSet newTasksLeft = tasksLeft.clone();
					newTasksLeft.remove(task);

					// Add new state in queue
					if(statesMap.containsKey(newState) && !statesMap.containsKey(state)) {
						System.out.println(state.toString());
					}

					Long prospectiveEarn = (newPlan.totalDistanceUnits() + computeHeuristic(newState, newTasksLeft) ) * costPerKm;
					if(!statesMap.containsKey(newState) || ( prospectiveEarn < statesMap.get(state))) {
						statesMap.put(newState, newPlan.totalDistanceUnits() * costPerKm);
						pq.add(new QueueParams(newState, newPlan, newTasksLeft));
					}
				}
			}

			// 4. Move to other city
			for(City toCity: state.getCurrentCity().neighbors()) {
				// Deep copy params for new recursion (so that we avoid java.util.ConcurrentModificationException)
				State newState = state.clone();
				newState.setCurrentCity(toCity);

				Plan newPlan = copyPlan(state.getCurrentCity(), plan);
				newPlan.appendMove(toCity);

				TaskSet newTasksLeft = tasksLeft.clone();

				// Add new state in queue
				Long prospectiveEarn = (newPlan.totalDistanceUnits() + computeHeuristic(newState, newTasksLeft) ) * costPerKm;
				if(!statesMap.containsKey(newState) || ( prospectiveEarn < statesMap.get(state))) {
					statesMap.put(newState, newPlan.totalDistanceUnits() * costPerKm);
					pq.add(new QueueParams(newState, newPlan, newTasksLeft));
				}
			}
		}

		return bestPlan;
	}

	@Override
	public void planCancelled(TaskSet carriedTasks) {
		
		if (!carriedTasks.isEmpty()) {
			// This cannot happen for this simple agent, but typically
			// you will need to consider the carriedTasks when the next
			// plan is computed.

			// For BFS there is no need to be done anything, init steps takes cares of it no matter what
		}
	}
}
