package schedulers.non_preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLQNP {

    private static final int Q0_QUANTUM = 4;

    public SimulationResult run(List<Process> originalProcesses) {
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> Q0 = new LinkedList<>();
        Queue<Process> Q1 = new LinkedList<>();
        Queue<Process> Q2 = new LinkedList<>();

        List<String> gantt = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int arrivalIndex = 0;
        int n = processes.size();

        while (completed < n) {
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Q0.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            Process current = null;

            if (!Q0.isEmpty()) current = Q0.poll();
            else if (!Q1.isEmpty()) current = Q1.poll();
            else if (!Q2.isEmpty()) current = Q2.poll();

            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            if (Q1.contains(current) || Q2.contains(current)) {
                while (current.getRemainingTime() > 0) {
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }
                }
            } else {
                int runTime = Math.min(Q0_QUANTUM, current.getRemainingTime());

                for (int i = 0; i < runTime; i++) {
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }
                }

                if (current.getRemainingTime() > 0) {
                    Q0.add(current);
                    continue;
                }
            }

            current.setCompletionTime(time);
            current.setTurnaroundTime(time - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}