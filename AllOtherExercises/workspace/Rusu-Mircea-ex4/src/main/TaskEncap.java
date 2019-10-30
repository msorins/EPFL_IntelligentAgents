package main;

import logist.task.Task;

import java.util.Objects;

public class TaskEncap {
    public Task task;
    public Boolean pickUp;

    public TaskEncap(Task task, Boolean pickUp) {
        this.task = task;
        this.pickUp = pickUp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEncap taskEncap = (TaskEncap) o;
        return Objects.equals(task, taskEncap.task) &&
                Objects.equals(pickUp, taskEncap.pickUp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, pickUp);
    }
}
