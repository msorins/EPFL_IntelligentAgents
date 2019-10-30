package main;

import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSP {
    // These two do not change
    private List<Vehicle> vehiclesList;
    private List<Task> tasks;

    // 2*Nt + Nv  (1*Nt for pick up tasks, 1*Nt for deliver tasks)
    private HashMap<TaskOrVehicle, TaskEncap> nextTask;
    private ArrayList<Integer> time;
    private ArrayList<Integer> vehicle;
    private ArrayList<Integer> capacity;

    public CSP() {
    }

    public CSP(List<Vehicle> vehiclesList, List<Task> tasks, HashMap<TaskOrVehicle, TaskEncap> nextTask, ArrayList<Integer> time, ArrayList<Integer> vehicle, ArrayList<Integer> capacity) {
        this.vehiclesList = vehiclesList;
        this.tasks = tasks;
        this.nextTask = nextTask;
        this.time = time;
        this.vehicle = vehicle;
        this.capacity = capacity;
    }

    static CSP generate(List<Vehicle> vehiclesList, List<Task> tasks) {
        // Can be used to randomly generate a new CSP
        CSP csp = new CSP(
                vehiclesList,
                tasks,
                new HashMap<TaskOrVehicle, TaskEncap>(),
                new ArrayList<Integer>(2 * tasks.size()),
                new ArrayList<Integer>(2 * tasks.size()),
                new ArrayList<Integer>(2 * tasks.size())
        );

        int crtVehicleIndex = 0;
        for(Task task: tasks) {
            Vehicle vehicle = csp.getVehiclesList().get(crtVehicleIndex);
            // Attribute to "vehicle" the current task

            if(csp.getNextTask().containsKey(vehicle)) {
                // Vehicle already has a starting task
            } else {
                // Vehicle doesn't have a starting task
                csp.getNextTask().put(new TaskOrVehicle(vehicle), new TaskEncap(task, true));
                csp.getNextTask()

            }


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

    Plan toPlan() {
        // Will convert the current CSP to a plan
        return null;
    }

    Long cost() {
        return 0L;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CSP(
                this.vehiclesList,
                this.tasks,
                (HashMap<TaskOrVehicle, TaskEncap>)  this.nextTask.clone(),
                (ArrayList<Integer>)   this.time.clone(),
                (ArrayList<Integer>)   this.vehicle.clone(),
                (ArrayList<Integer>)   this.capacity.clone()
        );
    }

    public List<Vehicle> getVehiclesList() {
        return vehiclesList;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public HashMap<TaskOrVehicle, TaskEncap> getNextTask() {
        return nextTask;
    }

    public ArrayList<Integer> getTime() {
        return time;
    }

    public ArrayList<Integer> getVehicle() {
        return vehicle;
    }

    public ArrayList<Integer> getCapacity() {
        return capacity;
    }
}
