package applications;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import dataStructures.LinkedQueue;

public class SimulationSpecificationGenerator extends Generator<SimulationSpecification> {
    /* I initially thought of having larger numbers here, like 100 or 1,000, but
       in playing with properties for `LinkedQueue` it became clear that if your
       sizes were too big you never hit edge cases like an empty queue.
    */
    public static final int MAX_MACHINES = 10;
    public static final int MAX_JOBS = 10;
    public static final int MAX_TASKS = 10;
    public static final int MAX_CHANGEOVER_TIME = 10;
    private static final int MAX_TASK_TIME = 10;

    public SimulationSpecificationGenerator() {
        super(SimulationSpecification.class);
    }

    @Override
    public SimulationSpecification generate(SourceOfRandomness r, GenerationStatus status) {
        SimulationSpecification result = new SimulationSpecification();
        // Add one so we don't get 0 Machines or Jobs (0 of either breaks test).
        int numMachines = r.nextInt(MAX_MACHINES) + 1;
        int numJobs = r.nextInt(MAX_JOBS) + 1;

        result.setNumMachines(numMachines);
        result.setNumJobs(numJobs);

        /* changeOverTimes is a 1 indexed array so we need to allocate an extra space */
        int[] changeOverTimes = new int[numMachines + 1];
        for (int i = 1; i <= numMachines; i++) {
            // Changeover times can be 0 so I don't need to add 1 here.
            changeOverTimes[i] = r.nextInt(MAX_CHANGEOVER_TIME);
        }
        result.setChangeOverTimes(changeOverTimes);

        //CREATING JOBS
        Job[] jobs = new Job[numJobs];
        for (int i = 0; i < numJobs; i++) {
            int numTasks = r.nextInt(MAX_TASKS) + 1; //Because the number of tasks per job cannot be zero
            jobs[i] = new Job(i);
            jobs[i].numTasks = numTasks;

            for (int j = 0; j < numTasks; j++) {
                int machine = r.nextInt(numMachines) + 1;
                int taskTime = r.nextInt(MAX_TASK_TIME) + 1;
                jobs[i].addTask(machine, taskTime);
            }
        }
        result.jobs = jobs;
        //System.err.println("result.jobs: " + result.jobs[0].getTaskQ().isEmpty());
        return result;
    }

    /**
     * We'll try each of the following ways of shrinking things:
     * * Removing a random machine (& all references to it in the task lists)
     * * Removing a random job & its task list
     * These two moves seem to get us down to one machine and one job
     * pretty consistently when there are fundamental errors in the code.
     *
     * @param r source of randomness
     * @param spec the simulation specification to shrink
     * @return a list of smaller specifications
     */
    @Override
    public List<SimulationSpecification> doShrink(
            SourceOfRandomness r, SimulationSpecification spec) {

        return Stream.of(
                removeRandomMachine(r, spec),
                removeRandomJob(r, spec)
        )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private SimulationSpecification removeRandomMachine(SourceOfRandomness r, SimulationSpecification spec) {
        SimulationSpecification smallerSpec = new SimulationSpecification();
        int originalNumMachines = spec.getNumMachines();
        if (originalNumMachines == 1) {
            return null;
        }
        int numJobs = spec.getNumJobs();
        smallerSpec.setNumMachines(originalNumMachines - 1);
        smallerSpec.setNumJobs(numJobs);

        int machineToRemove = r.nextInt(originalNumMachines) + 1;

        int[] newChangeOvers = new int[originalNumMachines];
        for (int i = 1, j = 1; i <= originalNumMachines; i++) {
            if (i != machineToRemove) {
                newChangeOvers[j] = spec.getChangeOverTimes(i);
                j++;
            }
        }
        smallerSpec.setChangeOverTimes(newChangeOvers);

        Job[] newJobs = new Job[numJobs];
        for (int i = 0; i < numJobs; i++) {
            Job job = spec.jobs[i];
            int machineTaskCount = job.numTasks;
            LinkedQueue jobTasks = job.getTaskQ();
            int numTasksOnThisMachine = 0;
            for (int j = 0; j < machineTaskCount; j++) {
                Task task = (Task)jobTasks.getFrontElement();
                if (task.getMachine() == machineToRemove) {
                    numTasksOnThisMachine++;
                }
                jobTasks.put(task);
                jobTasks.remove();
            }

            /* If the only tasks in the taskQ use the machine we want to get rid of,
               then removing this machine would break things.
               Instead, we will not remove this machine.*/
            if (numTasksOnThisMachine == machineTaskCount) {
                return null;
            }

            final int newNumTasks = machineTaskCount - numTasksOnThisMachine;
            for(int j = 0; j < machineTaskCount; j++){
                Task curTask = (Task)jobTasks.getFrontElement();
                if(curTask.getMachine() != machineToRemove){
                    int newMachineId = curTask.getMachine();
                    if(newMachineId > machineToRemove){
                        newMachineId--;
                    }
                    newJobs[i].addTask(newMachineId, curTask.getTime());
                }
                jobTasks.put(jobTasks.remove());
            }
            newJobs[i].numTasks = newNumTasks;
        }
        smallerSpec.jobs = newJobs;

        return smallerSpec;
    }

    private SimulationSpecification removeRandomJob(SourceOfRandomness r, SimulationSpecification spec) {
        SimulationSpecification smallerSpec = new SimulationSpecification();
        int originalNumJobs = spec.getNumJobs();
        if (originalNumJobs == 1) {
            return null;
        }
        int numMachines = spec.getNumMachines();
        smallerSpec.setNumMachines(numMachines);
        smallerSpec.setNumJobs(originalNumJobs-1);
        //CREATE NEW CHANGEOVER TIMES
        int[] changeOverTimes = new int[numMachines + 1];
        for (int i=1; i<=numMachines; ++i) {
            changeOverTimes[i] = spec.getChangeOverTimes(i);
        }
        smallerSpec.setChangeOverTimes(changeOverTimes);
        //CREATE NEW JOBS
        int jobToRemove = r.nextInt(originalNumJobs);

        Job[] newJobs = new Job[originalNumJobs - 1];
        for (int i = 0, j = 0; i < originalNumJobs; i++) {
            if (i != jobToRemove) {
                newJobs[j] = spec.jobs[i];
                j++;
            }
        }
        smallerSpec.jobs = newJobs;

        return smallerSpec;
    }

    @Override public BigDecimal magnitude(Object value) {
        SimulationSpecification simulationSpecification = (SimulationSpecification) value;
        int size = simulationSpecification.getNumMachines();
        size += simulationSpecification.getNumJobs();
        for (int i = 0; i < simulationSpecification.getNumJobs(); i++) {
            size += simulationSpecification.jobs[i].numTasks;
        }

        return BigDecimal.valueOf(size);
    }
}
