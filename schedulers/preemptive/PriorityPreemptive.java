package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class PriorityPreemptive {

    /**
     * PREEMPTIVE PRIORITY SCHEDULING
     *
     * Rules:
     * - Lower priority number = higher actual priority.
     * - At EVERY time unit, choose the highest-priority process
     *   among all processes that have arrived.
     * - If a new higher-priority process arrives, we immediately
     *   preempt the current one.
     * - A process runs only for ONE time unit before re-checking.
     */
    public SimulationResult run(List<Process> originalProcesses) {

        // Make independent copies so original list stays untouched
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) {
            processes.add(p.copy());
        }

        // Sort once by arrival time so iteration order is predictable
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<String> gantt = new ArrayList<>();   // Timeline output
        int time = 0;
        int finishedCount = 0;
        int total = processes.size();
        boolean[] done = new boolean[total];

        Process current = null;
        int currentIndex = -1;

        // Keep looping until all processes finish
        while (finishedCount < total) {

            // ---------------------------------------------------------
            // 1. Find the BEST available process at this moment
            // ---------------------------------------------------------
            Process bestProcess = null;
            int bestProcessIndex = -1;
            int bestPriority = Integer.MAX_VALUE;  // Lowest number wins

            for (int i = 0; i < total; i++) {
                Process p = processes.get(i);

                // Skip processes that have not arrived or are already done
                if (done[i] || p.getArrivalTime() > time) continue;

                // Find highest priority (lowest priority number)
                if (p.getPriority() < bestPriority) {
                    bestPriority = p.getPriority();
                    bestProcess = p;
                    bestProcessIndex = i;
                }
            }

            // ---------------------------------------------------------
            // 2. No process available → CPU idle for this time unit
            // ---------------------------------------------------------
            if (bestProcess == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // ---------------------------------------------------------
            // 3. Preemption check — switch CPU to new best process
            // ---------------------------------------------------------
            if (current != bestProcess) {
                current = bestProcess;
                currentIndex = bestProcessIndex;

                // If this is the first time it runs → record its response time
                if (current.getStartTime() == null) {
                    current.setStartTime(time);
                    current.setResponseTime(time - current.getArrivalTime());
                }
            }

            // ---------------------------------------------------------
            // 4. Run the process for ONE time unit (because it's preemptive)
            // ---------------------------------------------------------
            gantt.add(current.getPid());
            time++;

            current.setRemainingTime(current.getRemainingTime() - 1);

            // ---------------------------------------------------------
            // 5. If finished → compute performance metrics & clear CPU
            // ---------------------------------------------------------
            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(time - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

                done[currentIndex] = true;
                finishedCount++;

                current = null;  // CPU becomes free
            }
        }

        // Return final output including Gantt chart & updated processes
        return new SimulationResult(gantt, processes, time);
    }
}
