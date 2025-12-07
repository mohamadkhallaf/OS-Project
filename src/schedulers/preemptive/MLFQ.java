package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLFQ {

    // Lecture values: Q0 = RR8, Q1 = RR16, Q2 = FCFS
    private static final int Q0_QUANTUM = 8;
    private static final int Q1_QUANTUM = 16;

    public SimulationResult run(List<Process> originalProcesses) {

        // Make fresh copies
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        // Sort by arrival for predictable ordering
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Three levels of ready queues
        Queue<Process> Q0 = new LinkedList<>(); // Highest priority
        Queue<Process> Q1 = new LinkedList<>(); // Medium priority
        Queue<Process> Q2 = new LinkedList<>(); // Lowest priority (FCFS)

        List<String> gantt = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int n = processes.size();
        int arrivalIndex = 0;

        // Continue until all processes finish
        while (completed < n) {

            // Bring new arrivals into Q0
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Q0.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            Process current = null;
            int currentLevel = -1; // 0 = Q0, 1 = Q1, 2 = Q2

            // ---------------------------
            // 1. Choose queue by priority
            // ---------------------------
            if (!Q0.isEmpty()) {
                current = Q0.poll();
                currentLevel = 0;
            } else if (!Q1.isEmpty()) {
                current = Q1.poll();
                currentLevel = 1;
            } else if (!Q2.isEmpty()) {
                current = Q2.poll();
                currentLevel = 2;
            }

            // No process ready → idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // First time running? → response time
            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            // --------------------------------------
            // 2. Determine quantum based on the queue
            // --------------------------------------
            int quantum;
            if (currentLevel == 0) quantum = Q0_QUANTUM;
            else if (currentLevel == 1) quantum = Q1_QUANTUM;
            else quantum = Integer.MAX_VALUE; // FCFS for Q2 (run until completion)

            // RUN the process for up to quantum time (preemptible per time unit)
            int runTime = Math.min(quantum, current.getRemainingTime());

            for (int i = 0; i < runTime; i++) {
                gantt.add(current.getPid());
                time++;

                // Add arrivals during execution (always to Q0)
                while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                    Q0.add(processes.get(arrivalIndex));
                    arrivalIndex++;
                }

                // Optional optimization: if a newly arrived process is in Q0 and we are running from Q1/Q2,
                // we might preempt immediately. We already re-check queues next loop iteration.
            }

            // Deduct CPU used
            current.setRemainingTime(current.getRemainingTime() - runTime);

            // --------------------------------------------------
            // 3. If finished → compute metrics + do NOT enqueue
            // --------------------------------------------------
            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
                current.setTurnaroundTime(time - current.getArrivalTime());
                current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                completed++;
                continue;
            }

            // --------------------------------------------------
            // 4. Not finished → DEMOTE to lower queue based on currentLevel
            // --------------------------------------------------
            if (currentLevel == 0) {
                // used full quantum? demote to Q1. If it used less because it finished earlier, we already handled above.
                Q1.add(current);
            } else if (currentLevel == 1) {
                Q2.add(current);
            } else {
                // Q2 (FCFS) → stay in Q2 until finished
                Q2.add(current);
            }
        }

        return new SimulationResult(gantt, processes, time);
    }
}
