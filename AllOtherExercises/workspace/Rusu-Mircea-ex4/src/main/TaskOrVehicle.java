package main;

import logist.simulation.Vehicle;
import logist.task.Task;

public class TaskOrVehicle {
    private TaskEncap task;
    private Vehicle vehicle ;
    boolean isTask;

    public TaskOrVehicle(TaskEncap task, Vehicle vehicle, boolean isTask) {
        this.task = task;
        this.vehicle = vehicle;
        this.isTask = isTask;
    }

    public TaskOrVehicle(TaskEncap task) {
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
