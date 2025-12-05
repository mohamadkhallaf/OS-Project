package schedulers.preemptive;

import process.Process;
import simulation.SimulationResult;

import java.util.*;

public class RR {

    private int quantum;

    public RR(int quantum) {
        this.quantum = quantum;
    }

    public SimulationResult run(List<Process> originalProcesses) {

        // Deep copy the processes (so RR does not modify the original list)
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) {
            processes.add(p.copy());
        }

        // Sort by arrival time initially
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();
        List<String> gantt = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int n = processes.size();

        // Track index for new arrivals
        int arrivalIndex = 0;

        while (completed < n) {

            // Bring newly arrived processes into the ready queue
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                readyQueue.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            // If no process is ready → idle
            if (readyQueue.isEmpty()) {
                gantt.add("idle");
                time++;
                continue;
            }

            // Get next process from queue
            Process current = readyQueue.poll();

            // If first time executing, set start + response time
            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            // Execute for at most 'quantum' or remaining time
            int runTime = Math.min(quantum, current.getRemainingTime());

            for (int i = 0; i < runTime; i++) {
                gantt.add(current.getPid());
                time++;

                // Before each time step, check if new processes arrived
                while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                    readyQueue.add(processes.get(arrivalIndex));
                    arrivalIndex++;
                }
            }

            // Update remaining time
            current.setRemainingTime(current.getRemainingTime() - runTime);

            // If process is finished
            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                completed++;
            } 
            else {
                // Not finished → put back in queue
                readyQueue.add(current);
            }
        }

        return new SimulationResult(gantt, processes, time);
    }
}