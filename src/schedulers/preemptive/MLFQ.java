package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLFQ {

    private final int quantum;  

    public MLFQ(int quantum) {
        this.quantum = quantum;
    }
    @Override
    public String toString() {
        return String.format(
            "MLFQ\n" +
            "Q0 : RR (q = %d)\n" +
            "Q1 : RR (q = %d)\n" +
            "Q2 : FCFS",
            quantum, quantum
        );
    }
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
        int n = processes.size();
        int arrivalIndex = 0;

        while (completed < n) {

            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Q0.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            Process current = null;
            int level = -1;

            if (!Q0.isEmpty()) {
                current = Q0.poll();
                level = 0;
            } else if (!Q1.isEmpty()) {
                current = Q1.poll();
                level = 1;
            } else if (!Q2.isEmpty()) {
                current = Q2.poll();
                level = 2;
            }

            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            // ============================
            // Q0 and Q1 --> Round Robin
            // ============================
            if (level == 0 || level == 1) {

                int used = 0;

                while (used < quantum && current.getRemainingTime() > 0) {

                    gantt.add(current.getPid());
                    time++;
                    used++;
                    current.setRemainingTime(current.getRemainingTime() - 1);

                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }

                    if (current.getRemainingTime() == 0) {
                        current.setCompletionTime(time);
                        current.setTurnaroundTime(time - current.getArrivalTime());
                        current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                        completed++;
                        break;
                    }
                }

                if (current.getRemainingTime() > 0) {
                    if (used == quantum) {
                        if (level == 0) Q1.add(current);
                        else Q2.add(current);
                    } else {
                        if (level == 0) Q0.add(current);
                        else Q1.add(current);
                    }
                }

                continue;
            }

            // ============================
            // Q2 --> FCFS (preempted only by Q0)
            // ============================
            if (level == 2) {

                while (current.getRemainingTime() > 0) {

                    if (!Q0.isEmpty()) {
                        Q2.add(current);
                        current = null;
                        break;
                    }

                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }

                    if (current != null && current.getRemainingTime() == 0) {
                        current.setCompletionTime(time);
                        current.setTurnaroundTime(time - current.getArrivalTime());
                        current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                        completed++;
                        break;
                    }
                }

                continue;
            }
        }


        return new SimulationResult(gantt, processes, time);
    }
}