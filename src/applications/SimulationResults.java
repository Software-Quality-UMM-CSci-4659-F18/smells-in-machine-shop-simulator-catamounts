package applications;

import java.util.Arrays;

public class SimulationResults {
    private int finishTime;
    private int numMachines;
    private int[] numTasksPerMachine;
    private int[] totalWaitTimePerMachine;
    private Job[] jobs;
    private int nextJob = 0;

    public SimulationResults(int numJobs) {
        jobs = new Job[numJobs];
    }

    public void print() {
        for (Job job : jobs) {
            System.out.println("Job " + job.getId() + " has completed at "
                    + job.completionTime + " Total wait was " + job.totalWaitTime);
        }

        System.out.println("Finish time = " + finishTime);
        for (int p = 1; p <= numMachines; p++) {
            System.out.println("Machine " + p + " completed "
                    + numTasksPerMachine[p] + " tasks");
            System.out.println("The total wait time was "
                    + totalWaitTimePerMachine[p]);
            System.out.println();
        }
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setNumMachines(int numMachines) {
        this.numMachines = numMachines;
    }

    public int[] getNumTasksPerMachine() {
        return Arrays.copyOf(numTasksPerMachine, numTasksPerMachine.length);
    }

    public void setNumTasksPerMachine(int[] numTasksPerMachine) {
        this.numTasksPerMachine = numTasksPerMachine;
    }

    public int[] getTotalWaitTimePerMachine() {
        return Arrays.copyOf(totalWaitTimePerMachine, totalWaitTimePerMachine.length);
    }

    public void setTotalWaitTimePerMachine(int[] totalWaitTimePerMachine) {
        this.totalWaitTimePerMachine = totalWaitTimePerMachine;
    }

    public void addJobToResults(Job job){
        jobs[nextJob] = job;
        nextJob++;
    }

    public Job[] getJobs() {
        return jobs;
    }
//
//    public void setJobCompletionData(int jobID, int completionTime, int totalWaitTime) {
//        Job jobCompletionData = new Job(jobID, completionTime, totalWaitTime);
//        jobs[nextJob] = jobCompletionData;
//        nextJob++;
//    }
}
