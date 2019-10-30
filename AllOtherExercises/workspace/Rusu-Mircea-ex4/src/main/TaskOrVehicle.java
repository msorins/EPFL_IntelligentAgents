package main;

import logist.simulation.Vehicle;
import logist.task.Task;

public class TaskOrVehicle {
    private Task task;
    private Vehicle vehicle ;
    boolean isTask;

    public TaskOrVehicle(Task task, Vehicle vehicle, boolean isTask) {
        this.task = task;
        this.vehicle = vehicle;
        this.isTask = isTask;
    }

    public TaskOrVehicle(Task task) {
        this.task = task;
        isTask = true;
    }

    public TaskOrVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        isTask = false;
    }

    public Object get() {
        if(isTask) {
            return this.task;
        } else {
            return this.vehicle;
        }
    }
}
