package applications;

import dataStructures.LinkedQueue;

class Job {
    // data members
    private LinkedQueue taskQ; // this job's tasks
    private int timeRunning; // measures how long the job has been running (excluded downtime)
    public int machineArrivalTime; // The global time when the job arrives at the machine of the current task.
    private int id;
    public int numTasks;
    private int[] specificationsForTasks;
    public int completionTime;
    public int totalWaitTime;

    public int[] getSpecificationsForTasks() {
        return specificationsForTasks;
    }

    Job(int theId) {
        id = theId;
        taskQ = new LinkedQueue();
    }

    public void addTask(int machineID, int time) {
        getTaskQ().put(new Task(machineID, time));
    }

    public LinkedQueue getTaskQ() {
        return taskQ;
    }

    public int removeNextTask() {
        int taskTime = ((Task) getTaskQ().remove()).getTime();
        timeRunning += taskTime;
        return taskTime;
    }

    public void setSpecificationsForTasks(int[] specificationsForTasks) {
        this.specificationsForTasks = specificationsForTasks;
    }

    public int getTimeRunning() {
        return timeRunning;
    }

    public int getId() {
        return id;
    }
}
