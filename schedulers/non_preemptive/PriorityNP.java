package schedulers.non_preemptive;

import process.Process;
import simulation.SimulationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityNP {

    public SimulationResult run(List<Process> processes) {

        // Timeline list (Gantt chart)
        List<String> gantt = new ArrayList<>();

        // Sort processes by arrival (first rule)
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int time = 0;
        int completed = 0;
        int n = processes.size();

        // We need to mark which processes finished
        boolean[] done = new boolean[n];

        while (completed < n) {

            // Find the highest priority (smallest priority number) among arrived processes
            Process current = null;
            int currentIndex = -1;
            int bestPriority = Integer.MAX_VALUE;

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

            // If no process is ready → CPU idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // Set start time if first time running
            current.setStartTime(time);
            current.setResponseTime(current.getStartTime() - current.getArrivalTime());

            // Run the process for full burst (non-preemptive)
            for (int i = 0; i < current.getBurstTime(); i++) {
                gantt.add(current.getPid());
                time++;
            }

            // Mark process as completed
            current.setCompletionTime(time);
            current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

            done[currentIndex] = true;
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}

