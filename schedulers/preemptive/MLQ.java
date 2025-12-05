package schedulers.preemptive;

import process.Process;
import simulation.SimulationResult;

import java.util.*;

public class MLQ {

    private int quantum; // Foreground queue uses RR

    public MLQ(int quantum) {
        this.quantum = quantum;
    }

    public SimulationResult run(List<Process> originalProcesses) {

        // Deep copy
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) {
            processes.add(p.copy());
        }

        // Foreground (HIGH priority) queue → RR
        Queue<Process> foreground = new LinkedList<>();

        // Background (LOW priority) queue → FCFS
        Queue<Process> background = new LinkedList<>();

        // Assign processes based on priority (you can change the rule)
        for (Process p : processes) {
            if (p.getPriority() <= 2)
                foreground.add(p);
            else
                background.add(p);
        }

        List<String> gantt = new ArrayList<>();
        int time = 0;
        int completed = 0;
        int n = processes.size();

        // Sort by arrival time (important for preemption)
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int arrivalIndex = 0;

        // The currently running background process (if any)
        Process runningBackground = null;

        while (completed < n) {

            // Bring new arrivals
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {

                Process arriving = processes.get(arrivalIndex);

                // Foreground arrivals PREEMPT background
                if (arriving.getPriority() <= 2) {
                    foreground.add(arriving);

                    // PREEMPT background if running
                    if (runningBackground != null) {
                        background.add(runningBackground); // back into queue
                        runningBackground = null; // stop running it
                    }
                } else {
                    background.add(arriving);
                }

                arrivalIndex++;
            }

            // ---------------------------------------------------
            // 1️⃣ Foreground (HIGH priority) → ALWAYS runs first
            // ---------------------------------------------------
            if (!foreground.isEmpty()) {

                Process current = foreground.poll();

                if (current.getStartTime() == null) {
                    current.setStartTime(time);
                    current.setResponseTime(time - current.getArrivalTime());
                }

                int run = Math.min(quantum, current.getRemainingTime());

                for (int i = 0; i < run; i++) {
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    // Check for new foreground arrivals DURING execution
                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Process arriving = processes.get(arrivalIndex);

                        if (arriving.getPriority() <= 2)
                            foreground.add(arriving);
                        else
                            background.add(arriving);

                        arrivalIndex++;
                    }
                }

                // Finished?
                if (current.getRemainingTime() == 0) {
                    current.setCompletionTime(time);
                    current.setTurnaroundTime(time - current.getArrivalTime());
                    current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                    completed++;
                } else {
                    // Put back (RR behavior)
                    foreground.add(current);
                }

                continue; // Foreground always takes precedence
            }

            // ---------------------------------------------------
            // 2️⃣ Background (LOW priority) — FCFS
            // ---------------------------------------------------
            if (runningBackground == null && !background.isEmpty()) {
                runningBackground = background.poll();
                if (runningBackground.getStartTime() == null) {
                    runningBackground.setStartTime(time);
                    runningBackground.setResponseTime(time - runningBackground.getArrivalTime());
                }
            }

            if (runningBackground != null) {

                gantt.add(runningBackground.getPid());
                runningBackground.setRemainingTime(runningBackground.getRemainingTime() - 1);
                time++;

                // Finished?
                if (runningBackground.getRemainingTime() == 0) {
                    runningBackground.setCompletionTime(time);
                    runningBackground.setTurnaroundTime(time - runningBackground.getArrivalTime());
                    runningBackground
                            .setWaitingTime(runningBackground.getTurnaroundTime() - runningBackground.getBurstTime());
                    completed++;

                    runningBackground = null;
                }

                continue;
            }

            // ---------------------------------------------------
            // 3️⃣ CPU Idle
            // ---------------------------------------------------
            gantt.add("idle");
            time++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}
