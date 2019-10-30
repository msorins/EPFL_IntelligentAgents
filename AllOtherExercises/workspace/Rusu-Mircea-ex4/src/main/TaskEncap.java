package main;

import logist.task.Task;

public class TaskEncap {
    public Task task;
    public Boolean pickUp;

    public TaskEncap(Task task, Boolean pickUp) {
        this.task = task;
        this.pickUp = pickUp;
    }
}
