package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class RR {

    private int quantum;

    public RR(int quantum) {
        this.quantum = quantum;
    }

    public SimulationResult run(List<Process> originalProcesses) {
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) {
            processes.add(p.copy());
        }

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();
        List<String> gantt = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int n = processes.size();
        int arrivalIndex = 0;

        while (completed < n) {
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                readyQueue.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            if (readyQueue.isEmpty()) {
                gantt.add("idle");
                time++;
                continue;
            }

            Process current = readyQueue.poll();

            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            int runTime = Math.min(quantum, current.getRemainingTime());

            for (int i = 0; i < runTime; i++) {
                gantt.add(current.getPid());
                time++;

                while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                    readyQueue.add(processes.get(arrivalIndex));
                    arrivalIndex++;
                }
            }

            current.setRemainingTime(current.getRemainingTime() - runTime);

            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                completed++;
            } else {
                readyQueue.add(current);
            }
        }

        return new SimulationResult(gantt, processes, time);
    }
}