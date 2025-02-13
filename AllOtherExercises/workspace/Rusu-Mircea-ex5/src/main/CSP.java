package main;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;

import java.util.*;

public class CSP {
    // These two do not change
    private List<Vehicle> vehiclesList;
    private TaskSet tasksToDo;

    static public final int RANDOM_SEED = 123456;

    static private Random generator = new Random(RANDOM_SEED);

    static public Random getRandom() {
      return generator;
    }

    // NumberVehicles list of tasks
    private ArrayList< ArrayList<TaskEncap> > tasks;

    public CSP() {
    }

    public CSP(List<Vehicle> vehiclesList, TaskSet tasksToDo, ArrayList<ArrayList<TaskEncap>> tasks) {
        this.vehiclesList = vehiclesList;
        this.tasksToDo = tasksToDo;
        this.tasks = tasks;
    }

    static CSP generate(List<Vehicle> vehiclesList, TaskSet tasks) {
        // Can be used to randomly generate a new CSP
        CSP csp = new CSP(
                vehiclesList,
                tasks,
                new ArrayList<>()
        );
        for(int i = 0; i < vehiclesList.size(); i++) {
            csp.getTasks().add(new ArrayList<>());
        }

        // Idea 1: Loop through tasks and assign each one to a vehicle (in order) that can handle the workload
        // Idea 2: Send all the load to one vehicle

        // Idea 3 (current): Randomly assign each of the task to a vehicle that can support the weight
        Random r = getRandom();
        for(Task task: tasks) {
            // Attribute to "crtVehicleIndex" the current task
            int crtVehicleIndex = r.nextInt(vehiclesList.size());

            int checks = 0;
            // While current vehicle cannot get the current task
            while (vehiclesList.get(crtVehicleIndex).capacity() < task.weight && checks <= vehiclesList.size()) {
              crtVehicleIndex += 1;
              crtVehicleIndex %= csp.vehiclesList.size();
              checks += 1;
            }
            if (checks > vehiclesList.size()) {
              throw new RuntimeException("A task cannot be delivered by any " +
                  "vehicle (its weight exceeds the capacity" +
                  "of all the vehicles)");
            }

            // The initial plan will be to always deliver sequentially, so
            // we know the current capacity is >= the task weight by the while loop above
            csp.getTasks().get(crtVehicleIndex).add( new TaskEncap(task, true) );
            csp.getTasks().get(crtVehicleIndex).add( new TaskEncap(task, false) );

        }

        return csp;
    }

    boolean isValid() {
        // Return if this.CSP is valid, aka is final state that respects all the constraints

        // Return false if not all tasks are delivered => IF IT IS NOT A FINAL STATE
        if(tasksDelivered() != this.tasksToDo.size() * 2) {
            return false;
        }

        // Return false if agent hasn't got a valid list of tasks
        if(areInvalidTasksFormations()) {
            return false;
        }

        // Return false if there are duplicate tasks in different cars
        if(areDuplicateTasks()) {
            return false;
        }

        return true;
    }

    int tasksDelivered() {
        int nr = 0;
        for(int i = 0; i < tasks.size(); i++) {
            nr += tasks.get(i).size();
        }

        return nr;
    }

    boolean areInvalidTasksFormations() {
        for(int i = 0; i < tasks.size(); i++) {
            HashMap<Task, Integer> memo = new HashMap<>();
            Integer crtCapacity = vehiclesList.get(i).capacity();

            for(int j = 0; j < tasks.get(i).size(); j++) {
                TaskEncap current = tasks.get(i).get(j);
                if(current.pickUp) {
                    // If pick up task => just add it to memo

                    // Return true if vehicle capacity has been exceeded
                    crtCapacity -= current.task.weight;
                    if(crtCapacity < 0) {
                        return true;
                    }

                    // Return true if task was taken twice
                    if(memo.containsKey(current.task)) {
                        return true;
                    }

                    // Add it to tasks
                    memo.put(current.task, j);
                } else {
                    // Deliver task
                    crtCapacity += current.task.weight;

                    // Return true if task was not picked up
                    if(!memo.containsKey(current.task)) {
                        return true;
                    }

                    // Return true if task was already delivered
                    if(memo.get(current.task) == -1) {
                        return true;
                    }

                    memo.put(current.task, -1);
                }
            }
        }

        return false;
    }

    boolean areDuplicateTasks() {
        HashSet<Task> memo = new HashSet<>();
        for(int i = 0; i < tasks.size(); i++) {
            for(int j = 0; j < tasks.get(i).size(); j++) {
                if(tasks.get(i).get(j).pickUp && memo.contains(tasks.get(i).get(j).task)) {
                    return true;
                }

                memo.add(tasks.get(i).get(j).task);
            }
        }
        return false;
    }

    List<CSP> getNeighbours(int nrNeighbours) {
        ArrayList<CSP> csps = new ArrayList<>();
        Random ran = getRandom();

        while(csps.size() < nrNeighbours) {
            if(ran.nextBoolean() || vehiclesList.size() == 1) {
                // Swap two tasks from a vehicle
                CSP newCsp = (CSP) this.clone();
                int vehicleIndex = ran.nextInt(newCsp.getVehiclesList().size());
                while (newCsp.getTasks().get(vehicleIndex).size() == 0) {
                  vehicleIndex = ran.nextInt(newCsp.getVehiclesList().size());
                }
                int indexA = -1, indexB = -1;
                while(indexA == indexB) {
                    indexA = ran.nextInt(newCsp.getTasks().get(vehicleIndex).size());
                    indexB = ran.nextInt(newCsp.getTasks().get(vehicleIndex).size());
                }
                Collections.swap( newCsp.getTasks().get(vehicleIndex), indexA, indexB);

                if(newCsp.isValid()) {
                    csps.add(newCsp);
                }
            } else {
                // Move one task from one vehicle to another
                CSP newCsp = (CSP) this.clone();

                // Indexes of targeted vehicles
                int vehicleIndexA = ran.nextInt(newCsp.getVehiclesList().size());
                while (newCsp.getTasks().get(vehicleIndexA).size() == 0) {
                  vehicleIndexA = ran.nextInt(newCsp.getVehiclesList().size());
                }
                int vehicleIndexB = ran.nextInt(newCsp.getVehiclesList().size());
                while(vehicleIndexA == vehicleIndexB) {
                    vehicleIndexB = ran.nextInt(newCsp.getVehiclesList().size());
                }

                // For each vehicle, target a specific task
                int posA = ran.nextInt(newCsp.tasks.get(vehicleIndexA).size());
                Task taskA = newCsp.tasks.get(vehicleIndexA).get(posA).task;

                // Delete the chosen task from vehicleA
                newCsp.tasks.get(vehicleIndexA).remove(new TaskEncap(taskA, true));
                newCsp.tasks.get(vehicleIndexA).remove(new TaskEncap(taskA, false));

                // Move the chosen task to vehicleB
                newCsp.tasks.get(vehicleIndexB).add(new TaskEncap(taskA, true));
                newCsp.tasks.get(vehicleIndexB).add(new TaskEncap(taskA, false));

                if(newCsp.isValid()) {
                    csps.add(newCsp);
                } else {
                    System.out.println("Possibly a problem, this should always be valid");
                }
            }
        }

        return csps;
    }

    CSP chooseBestNeighbour(int nrNeighbours) {
        // Will call getNeighbours and return the best neighbour
        List<CSP> csps = getNeighbours(nrNeighbours);
        CSP bestCSP = csps.get(0);

        for(int i = 1; i < csps.size(); i++) {
            if(csps.get(i).cost() < bestCSP.cost()) {
                bestCSP = csps.get(i);
            }
        }

        return bestCSP;
    }

    ArrayList<Plan> toPlans() {
        // Will convert the current CSP to a plan
        ArrayList<Plan> plans = new ArrayList<>();

        for(int i = 0; i < this.vehiclesList.size(); i++) {
            Topology.City currentCity = this.vehiclesList.get(i).getCurrentCity();
            Plan plan = new Plan(currentCity);

            for(TaskEncap task: tasks.get(i)) {
                if(task.pickUp) {
                    // Pick up the task
                    for(Topology.City city: currentCity.pathTo(task.task.pickupCity)) {
                        plan.appendMove(city);
                    }
                    currentCity = task.task.pickupCity;
                    plan.appendPickup(task.task);
                } else {
                    // Deliver the task
                    for(Topology.City city: currentCity.pathTo(task.task.deliveryCity)) {
                        plan.appendMove(city);
                    }
                    currentCity = task.task.deliveryCity;
                    plan.appendDelivery(task.task);
                }
            }
            plans.add(plan);
        }

        return plans;
    }

    Long cost() {
        ArrayList<Plan> plans = this.toPlans();
        long cost = 0;

        return CSP.plansCost(plans, this.vehiclesList);
    }

    static Long plansCost(ArrayList<Plan> plans, List<Vehicle> vehiclesList) {
        long cost = 0;

        for(int i = 0; i < plans.size(); i++) {
            cost += plans.get(i).totalDistance() * vehiclesList.get(i).costPerKm();
        }

        return cost;
    }


    @Override
    protected Object clone() {
        ArrayList<ArrayList<TaskEncap>> newTasks = new ArrayList<>();
        for(ArrayList<TaskEncap> tasksForVeh: this.tasks) {
            newTasks.add( (ArrayList<TaskEncap>) tasksForVeh.clone() );
        }

        return new CSP(
                this.vehiclesList,
                this.tasksToDo,
                newTasks
        );
    }

    public List<Vehicle> getVehiclesList() {
        return vehiclesList;
    }

    public TaskSet getTasksToDo() {
        return tasksToDo;
    }

    public ArrayList<ArrayList<TaskEncap>> getTasks() {
        return tasks;
    }
}
