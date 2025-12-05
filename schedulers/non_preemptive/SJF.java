package schedulers.non_preemptive;

import process.Process;
import simulation.SimulationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Shortest Job First (Non-Preemptive)
 * Chooses the process with the smallest burst time among ready processes.
 */
public class SJF {

    public SimulationResult run(List<Process> processes) {

        List<String> gantt = new ArrayList<>();

        // Sort processes by arrival time for predictable behavior
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int time = 0;
        int completed = 0;
        int n = processes.size();
        boolean[] done = new boolean[n];

        while (completed < n) {

            Process current = null;
            int currentIndex = -1;
            int shortestBurst = Integer.MAX_VALUE;

            // Pick the shortest job that has already arrived
            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);

                if (!done[i] && p.getArrivalTime() <= time) {
                    if (p.getBurstTime() < shortestBurst) {
                        shortestBurst = p.getBurstTime();
                        current = p;
                        currentIndex = i;
                    }
                }
            }

            // No available process → CPU idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // First time this process runs
            current.setStartTime(time);
            current.setResponseTime(time - current.getArrivalTime());

            // Run entire burst (non-preemptive)
            for (int i = 0; i < current.getBurstTime(); i++) {
                gantt.add(current.getPid());
                time++;
            }

            // Compute metrics
            current.setCompletionTime(time);
            current.setTurnaroundTime(time - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

            done[currentIndex] = true;
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}
