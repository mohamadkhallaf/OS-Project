package schedulers.non_preemptive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import process.Process;
import simulation.SimulationResult;

/**
 * NON-PREEMPTIVE PRIORITY SCHEDULER
 * Lower priority number = higher priority.
 * Once a process is chosen, it runs until completion.
 */
public class PriorityNP {

    public SimulationResult run(List<Process> processes) {

        List<String> gantt = new ArrayList<>();

        // Sort by arrival so we always check earlier arrivals first
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int time = 0;
        int completed = 0;
        int n = processes.size();
        boolean[] done = new boolean[n];

        while (completed < n) {

            Process current = null;
            int bestPriority = Integer.MAX_VALUE;
            int currentIndex = -1;

            // Find the highest priority process that has arrived
            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);

                if (!done[i] && p.getArrivalTime() <= time) {
                    if (p.getPriority() < bestPriority) {
                        bestPriority = p.getPriority();
                        current = p;
                        currentIndex = i;
                    }
                }
            }

            // If no process is available yet, CPU is idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // First time running → record response time
            current.setStartTime(time);
            current.setResponseTime(time - current.getArrivalTime());

            // Run the full burst
            for (int i = 0; i < current.getBurstTime(); i++) {
                gantt.add(current.getPid());
                time++;
            }

            // Process finishes
            current.setCompletionTime(time);
            current.setTurnaroundTime(time - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

            done[currentIndex] = true;
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}
