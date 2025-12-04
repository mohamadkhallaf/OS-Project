package schedulers.non_preemptive;

import process.Process;
import simulation.SimulationResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SJF {

    public SimulationResult run(List<Process> processes) {

        // Gantt chart timeline
        List<String> gantt = new ArrayList<>();

        // Sort first by arrival time
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int time = 0;
        int completed = 0;
        int n = processes.size();
        boolean[] done = new boolean[n];

        while (completed < n) {

            Process current = null;
            int currentIndex = -1;
            int shortestBurst = Integer.MAX_VALUE;

            // Find the shortest job among arrived and not completed processes
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

            // No process has arrived yet → idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // Set start and response time
            current.setStartTime(time);
            current.setResponseTime(current.getStartTime() - current.getArrivalTime());

            // Run full burst (NON-preemptive)
            for (int i = 0; i < current.getBurstTime(); i++) {
                gantt.add(current.getPid());
                time++;
            }

            // Set completion + metrics
            current.setCompletionTime(time);
            current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

            done[currentIndex] = true;
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}
