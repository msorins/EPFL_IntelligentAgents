package main;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CSP {
    // These two do not change
    private List<Vehicle> vehiclesList;
    private TaskSet tasksToDo;

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

        int crtVehicleIndex = 0;
        for(Task task: tasks) {
            // Attribute to "crtVehicleIndex" the current task
            csp.getTasks().get(crtVehicleIndex).add( new TaskEncap(task, true) );
            csp.getTasks().get(crtVehicleIndex).add( new TaskEncap(task, false) );

            crtVehicleIndex += 1;
            crtVehicleIndex %= csp.vehiclesList.size();
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
            return true;
        }

        // Return false if there are duplicate tasks in different cars
        if(areDuplicateTasks()) {
            return true;
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
                if(tasks.get(i).get(j).pickUp) {
                    // If pick up task => just add it to memo

                    // Return true if vehicle has more tasks than its capacity
                    crtCapacity -= 1;
                    if(crtCapacity < 0) {
                        return true;
                    }

                    // Return true if task was taken twice
                    if(memo.containsKey(tasks.get(i).get(j).task)) {
                        return true;
                    }

                    // Add it to tasks
                    memo.put(tasks.get(i).get(j).task, j);
                } else {
                    crtCapacity += 1;

                    // Return true if task was not picked up
                    if(!memo.containsKey(tasks.get(i).get(j).task)) {
                        return true;
                    }

                    // Return true if task was already delivered
                    if(memo.get(tasks.get(i).get(j).task) == -1) {
                        return true;
                    }

                    memo.put(tasks.get(i).get(j).task, -1);
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

        return csps;
    }

    CSP chooseBestNeighbour() {
        // Will call getNeighbours and return the best neighbour
        CSP csp = new CSP();
        return csp;
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
    protected Object clone() throws CloneNotSupportedException {
        return new CSP(
                this.vehiclesList,
                this.tasksToDo,
                (ArrayList<ArrayList<TaskEncap>>) this.tasks.clone()
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
