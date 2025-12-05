package schedulers.non_preemptive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import process.Process;
import simulation.SimulationResult;

/**
 * First Come First Served (FCFS) Scheduler
 * Non-preemptive: once a process starts running, it continues until completion.
 */
public class FCFS {

    public SimulationResult run(List<Process> processes) {

        // Sort by arrival time so the earliest process is first
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime));

        List<String> gantt = new ArrayList<>();
        int time = 0;

        for (Process p : processes) {

            // If CPU is idle before the process arrives, record the idle period
            while (time < p.getArrivalTime()) {
                gantt.add("idle");
                time++;
            }

            // Process starts execution
            p.setStartTime(time);
            p.setResponseTime(time - p.getArrivalTime());

            // Run the entire burst (non-preemptive)
            for (int i = 0; i < p.getBurstTime(); i++) {
                gantt.add(p.getPid());
                time++;
            }

            // Process completes
            p.setCompletionTime(time);
            p.setTurnaroundTime(time - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        // Build final result
        return new SimulationResult(gantt, processes, time);
    }
}
