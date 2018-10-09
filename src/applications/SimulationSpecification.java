package applications;

public class SimulationSpecification {
    private int numMachines; //Need's to be refactor such that it is public
    private int numJobs; //Need's to be refactor so that it is public
    private int[] changeOverTimes; //1 indexed: to be refactored
    public Job[] jobs; //0 indexed

    public void setNumMachines(int numMachines) {
        this.numMachines = numMachines;
    }

    public void setNumJobs(int numJobs) {
        this.numJobs = numJobs;
    }

    public int getNumMachines() {
        return numMachines;
    }

    public int getNumJobs() {
        return numJobs;
    }

    public void setChangeOverTimes(int[] changeOverTimes) {
        this.changeOverTimes = changeOverTimes;
    }

    public int getChangeOverTimes(int machineNumber) {
        return changeOverTimes[machineNumber];
    }
}
