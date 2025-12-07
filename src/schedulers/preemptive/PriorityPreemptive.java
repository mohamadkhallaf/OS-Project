package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class PriorityPreemptive {

    /**
     * Preemptive Priority Scheduling
     * Lower priority number = higher priority.
     * We check priority at EVERY time unit.
     */
    public SimulationResult run(List<Process> originalProcesses) {

        // Deep copy
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        // Sort by arrival
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<String> gantt = new ArrayList<>();

        int time = 0;
        int finished = 0;
        int n = processes.size();
        int arrivalIndex = 0;

        Process current = null;

        while (finished < n) {

            // 1. Add new arrivals
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                arrivalIndex++;
            }

            // 2. Select highest-priority ready process
            Process best = null;
            for (Process p : processes) {
                if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                    if (best == null || p.getPriority() < best.getPriority()) {
                        best = p;
                    }
                }
            }

            // No process ready → idle
            if (best == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // 3. Preempt immediately if needed
            if (current != best) {
                current = best;

                // First time response
                if (current.getStartTime() == null) {
                    current.setStartTime(time);
                    current.setResponseTime(time - current.getArrivalTime());
                }
            }

            // 4. Execute for ONE time unit
            gantt.add(current.getPid());
            current.setRemainingTime(current.getRemainingTime() - 1);
            time++;

            // 5. If finished → compute metrics
            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(time - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                finished++;

                current = null; // CPU free
            }
        }

        return new SimulationResult(gantt, processes, time);
    }
}
