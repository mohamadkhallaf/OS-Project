package schedulers.non_preemptive;

import process.Process;
import simulation.SimulationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FCFS {

    public SimulationResult run(List<Process> processes) {

        // Sort processes by arrival time (FCFS rule)
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime));

        List<String> gantt = new ArrayList<>(); // Gantt chart timeline
        int time = 0; // simulation time

        for (Process p : processes) {

            // If CPU is idle until process arrives
            while (time < p.getArrivalTime()) {
                gantt.add("idle");
                time++;
            }

            // Process starts now
            p.setStartTime(time);
            p.setResponseTime(p.getStartTime() - p.getArrivalTime());

            // Run the entire burst time (non-preemptive)
            for (int i = 0; i < p.getBurstTime(); i++) {
                gantt.add(p.getPid());
                time++;
            }

            // Process finishes
            p.setCompletionTime(time);
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        // Return results
        return new SimulationResult(gantt, processes, time);
    }
}
