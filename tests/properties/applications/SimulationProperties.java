package applications;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import dataStructures.LinkedQueue;

@RunWith(JUnitQuickcheck.class)
public class SimulationProperties {
    @Property
    public void lastJobCompletesAtOverallFinishTime(
            @From(SimulationSpecificationGenerator.class)
                    SimulationSpecification specification)
    {
        final SimulationResults results = MachineShopSimulator.runSimulation(specification);
        final int finishTime = results.getFinishTime();
        final Job[] jobs = results.getJobs();
        final int lastJobCompletionTime = jobs[jobs.length-1].completionTime; //Not sure if this should be jobs.length - 2
        assertEquals(finishTime, lastJobCompletionTime);
    }

    @Property
    public void waitTimesShouldMatch(
            @From(SimulationSpecificationGenerator.class)
                    SimulationSpecification specification)
    {
        final SimulationResults results = MachineShopSimulator.runSimulation(specification);

        int totalMachineWaitTime = 0;
        for (int waitTime : results.getTotalWaitTimePerMachine()) {
            assertThat(waitTime, greaterThanOrEqualTo(0));
            totalMachineWaitTime += waitTime;
        }

        int totalJobWaitTime = 0;
        for (Job jobs : results.getJobs()) {
            final int jobWaitTime = jobs.totalWaitTime;
            assertThat(jobWaitTime, greaterThanOrEqualTo(0));
            totalJobWaitTime += jobWaitTime;
        }

        assertEquals(totalJobWaitTime, totalMachineWaitTime);
    }

    @Property
    public void jobsOutputInTimeOrder(
            @From(SimulationSpecificationGenerator.class)
                    SimulationSpecification specification)
    {
        final SimulationResults results = MachineShopSimulator.runSimulation(specification);

        Job[] jobCompletionData = results.getJobs();
        for (int i=1; i<jobCompletionData.length-1; ++i) {
            assertThat(jobCompletionData[i].completionTime,
                    lessThanOrEqualTo(jobCompletionData[i+1].completionTime));
        }
    }

    @Property
    public void machinesCompletedCorrectNumberOfTasks(
            @From(SimulationSpecificationGenerator.class)
                    SimulationSpecification specification)
    {
        final SimulationResults results = MachineShopSimulator.runSimulation(specification);

        int numMachines = specification.getNumMachines();
        int numJobs = specification.getNumJobs();
        int[] expectedMachineTaskCounts = new int[numMachines+1];

        for (int i = 0; i < numJobs; i++) {
            Job job = specification.jobs[i];
            //System.err.println("job: " + job.getId() + " | taskQ: " + job.getTaskQ().toString());
            int numTasks = job.numTasks;
            LinkedQueue taskQ = job.getTaskQ();
            for (int j = 0; j < numTasks; j++) {
                int machine = ((Task)taskQ.getFrontElement()).getMachine();
                expectedMachineTaskCounts[machine]++;
                taskQ.put(taskQ.remove());
            }
        }

        int[] actualMachineTasksCounts = results.getNumTasksPerMachine();
        for (int i=1; i<=numMachines; ++i) {
            assertEquals(expectedMachineTaskCounts[i - 1], actualMachineTasksCounts[i]);
        }
    }

}
