package applications;

import exceptions.MyInputException;
import utilities.MyInputStream;

public class SpecificationReader {

    private final SimulationSpecification specification;
    private final MyInputStream keyboard;

    public SpecificationReader() {
        specification = new SimulationSpecification();
        keyboard = new MyInputStream();
    }

    private void readChangeOverTimes() {
        // input the change-over times
        int changeOverTimes[] = new int[specification.getNumMachines()+1];

        System.out.println("Enter change-over times for machines");
        for (int j = 1; j <= specification.getNumMachines(); j++) {
            int ct = keyboard.readInteger();
            if (ct < 0)
                throw new MyInputException(MachineShopSimulator.CHANGE_OVER_TIME_MUST_BE_AT_LEAST_0);
            changeOverTimes[j] = ct;
        }

        specification.setChangeOverTimes(changeOverTimes);
    }

    private void createJobTasks (Job job, int numTasks) {
        // create user specified tasks for a given job
        Task[] tasks = new Task[numTasks];
        System.out.println("Enter the tasks (machine, time) in process order");
        for (int i = 0; i < numTasks; i++) { // get tasks for job i
            int machine = keyboard.readInteger();
            int taskTime = keyboard.readInteger();
            if (machine < 1 || machine > specification.getNumMachines() || taskTime < 1) {
                throw new MyInputException(MachineShopSimulator.BAD_MACHINE_NUMBER_OR_TASK_TIME);
            }
            job.addTask(machine, taskTime);
        }
//        return tasks;
    }

    private void readJobSpecifications() {
        // input the jobs
        // take in user specified number of tasks per job
        Job[] jobs = new Job[specification.getNumJobs()]; //Refactor with javaCompRefactor
        for (int i = 0; i < specification.getNumJobs(); i++) {
            System.out.println("Enter number of tasks for job " + (i + 1));
            int numTasks = keyboard.readInteger();
            if (numTasks < 1) {
                throw new MyInputException(MachineShopSimulator.EACH_JOB_MUST_HAVE_AT_LEAST_1_TASK);
            }
            jobs[i] = new Job(i);
            jobs[i].numTasks = numTasks;
            createJobTasks(jobs[i], numTasks);
        }
        specification.jobs = jobs;
    }

    private void readNumberMachinesAndJobs() {
        System.out.println("Enter number of machines and jobs");
        int numMachines = keyboard.readInteger();
        int numJobs = keyboard.readInteger();
        if (numMachines < 1 || numJobs < 1) {
            throw new MyInputException(MachineShopSimulator.NUMBER_OF_MACHINES_AND_JOBS_MUST_BE_AT_LEAST_1);
        } else {
            specification.setNumMachines(numMachines);
            specification.setNumJobs(numJobs);
        }
    }

    /** input machine shop data */
    public SimulationSpecification readSpecification() {
        readNumberMachinesAndJobs();
        readChangeOverTimes();
        readJobSpecifications();
        return specification;
    }
}
