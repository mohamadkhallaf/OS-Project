package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class PriorityPreemptive {

    public SimulationResult run(List<Process> originalProcesses) {
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<String> gantt = new ArrayList<>();

        int time = 0;
        int finished = 0;
        int n = processes.size();
        int arrivalIndex = 0;

        Process current = null;

        while (finished < n) {
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                arrivalIndex++;
            }

            Process best = null;
            for (Process p : processes) {
                if (p.getArrivalTime() <= time && p.getRemainingTime() > 0) {
                    if (best == null || p.getPriority() < best.getPriority()) {
                        best = p;
                    }
                }
            }

            if (best == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            if (current != best) {
                current = best;

                if (current.getStartTime() == null) {
                    current.setStartTime(time);
                    current.setResponseTime(time - current.getArrivalTime());
                }
            }

            gantt.add(current.getPid());
            current.setRemainingTime(current.getRemainingTime() - 1);
            time++;

            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(time - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                finished++;

                current = null;
            }
        }

        return new SimulationResult(gantt, processes, time);
    }
}