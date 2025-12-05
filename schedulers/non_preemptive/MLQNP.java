package schedulers.non_preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLQNP {

    private static final int Q0_QUANTUM = 4;  // RR quantum for highest queue

    /**
     * NON-PREEMPTIVE Multi-Level Queue Scheduling
     *
     * Rules:
     * - Multiple queues with fixed priority order.
     * - NO PREEMPTION BETWEEN QUEUES.
     *   If CPU is running a Q1 process, and a Q0 process arrives, Q0 waits.
     *
     * - Q0 uses RR(q=4)
     * - Q1 uses FCFS
     * - Q2 uses FCFS
     *
     * - Processes NEVER move between queues (unlike MLFQ).
     */
    public SimulationResult run(List<Process> originalProcesses) {

        // Make clean copies so we don't overwrite original data
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        // Sort arrivals for predictable loading
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Three fixed queues (levels)
        Queue<Process> Q0 = new LinkedList<>(); // Highest priority (RR)
        Queue<Process> Q1 = new LinkedList<>(); // Medium (FCFS)
        Queue<Process> Q2 = new LinkedList<>(); // Lowest (FCFS)

        List<String> gantt = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int arrivalIndex = 0;
        int n = processes.size();

        // By default: assign all arriving processes to Q0
        while (completed < n) {

            // Add newly arrived processes to Q0
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Q0.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            Process current = null;

            // Choose queue by FIXED PRIORITY (Non-Preemptive)
            if (!Q0.isEmpty()) current = Q0.poll();
            else if (!Q1.isEmpty()) current = Q1.poll();
            else if (!Q2.isEmpty()) current = Q2.poll();

            // If all queues empty → CPU idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // First-time execution tracking
            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            // -------------------------------------
            // RUNNING THE PROCESS (NON-PREEMPTIVE)
            // -------------------------------------

            if (Q1.contains(current) || Q2.contains(current)) {
                // FCFS → run until process finishes
                while (current.getRemainingTime() > 0) {
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    // Capture new arrivals and assign them to Q0 (but DO NOT preempt)
                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }
                }
            } 
            else {
                // CURRENT PROCESS CAME FROM Q0 → Round Robin(q=4)
                int runTime = Math.min(Q0_QUANTUM, current.getRemainingTime());

                for (int i = 0; i < runTime; i++) {
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    // Add new arrivals (still does NOT cause preemption)
                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }
                }

                // If process not finished, it STAYS in Q0 (no demotion in MLQ)
                if (current.getRemainingTime() > 0) {
                    Q0.add(current);
                    continue;
                }
            }

            // -------------------------------------
            // PROCESS COMPLETED → COMPUTE METRICS
            // -------------------------------------
            current.setCompletionTime(time);
            current.setTurnaroundTime(time - current.getArrivalTime());
            current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
            completed++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}
