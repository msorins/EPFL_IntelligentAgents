package main;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;

import java.util.ArrayList;
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
        return true;
    }

    List<CSP> getNeighbours() {
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
