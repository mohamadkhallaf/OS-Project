package schedulers.non_preemptive;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import process.Process;
import simulation.SimulationResult;

public class PriorityNP {

    public SimulationResult run(List<Process> processes) {
        List<String> gantt = new ArrayList<>();
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int time = 0;
        int completed = 0;
        int n = processes.size();
        boolean[] done = new boolean[n];

        while (completed < n) {
            Process current = null;
            int bestPriority = Integer.MAX_VALUE;
            int currentIndex = -1;

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

            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            current.setStartTime(time);
            current.setResponseTime(time - current.getArrivalTime());

            for (int i = 0; i < current.getBurstTime(); i++) {
                gantt.add(current.getPid());
                time++;
            }

            current.setCompletionTime(time);
            current.setTurnaroundTime(time - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());

            done[currentIndex] = true;
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}