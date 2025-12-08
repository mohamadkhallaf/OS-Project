package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class RR {

    private final int quantum;

    public RR(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public String toString() {
        return "Round Robin (q=" + quantum + ")";
    }

    public SimulationResult run(List<Process> inputProcesses) {

        // Create a safe copy so original list isn't modified
        List<Process> processes = new ArrayList<>();
        for (Process p : inputProcesses) {
            processes.add(p.copy());
        }

        // Sort by arrival time so we load them in correct order
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();
        List<String> ganttChart = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int n = processes.size();
        int nextArrival = 0;

        while (completed < n) {

            // Add newly arrived processes to the ready queue
            while (nextArrival < n && processes.get(nextArrival).getArrivalTime() <= time) {
                readyQueue.offer(processes.get(nextArrival));
                nextArrival++;
            }

            // If no process is ready, CPU stays idle for this time unit
            if (readyQueue.isEmpty()) {
                ganttChart.add("IDLE");
                time++;
                continue;
            }

            // Fetch next process from queue
            Process current = readyQueue.poll();

            // Mark first time the process gets CPU
            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            // Determine how long to run this process
            int execTime = Math.min(quantum, current.getRemainingTime());

            // Run the process for each time unit
            for (int i = 0; i < execTime; i++) {
                ganttChart.add(current.getPid());
                time++;

                // Check if new processes arrive during execution
                while (nextArrival < n && processes.get(nextArrival).getArrivalTime() <= time) {
                    readyQueue.offer(processes.get(nextArrival));
                    nextArrival++;
                }
            }

            // Update remaining burst time
            current.setRemainingTime(current.getRemainingTime() - execTime);

            // If finished, compute stats
            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                completed++;
            }
            // Otherwise, requeue it
            else {
                readyQueue.offer(current);
            }
        }

        // Build and return the final result
        return new SimulationResult(ganttChart, processes, time);
    }
}
