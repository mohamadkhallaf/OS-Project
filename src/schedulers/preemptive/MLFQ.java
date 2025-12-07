package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLFQ {

    private static final int Q0_QUANTUM = 8;
    private static final int Q1_QUANTUM = 16;

  
    public SimulationResult run(List<Process> originalProcesses) {

        // Deep copy to avoid modifying original
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        // Sort by arrival
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Queues
        Queue<Process> Q0 = new LinkedList<>();
        Queue<Process> Q1 = new LinkedList<>();
        Queue<Process> Q2 = new LinkedList<>();

        List<String> gantt = new ArrayList<>();

        int time = 0;
        int completed = 0;
        int n = processes.size();
        int arrivalIndex = 0;

        while (completed < n) {

            // Load new arrivals → ALWAYS into Q0
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Q0.add(processes.get(arrivalIndex));
                arrivalIndex++;
            }

            Process current = null;
            int level = -1;

            // Choose queue (highest priority non-empty queue)
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

            // If no process is ready → idle
            if (current == null) {
                gantt.add("idle");
                time++;
                continue;
            }

            // First time running?
            if (current.getStartTime() == null) {
                current.setStartTime(time);
                current.setResponseTime(time - current.getArrivalTime());
            }

            // ------------------------
            // LEVEL 0 → RR (quantum 8)
            // LEVEL 1 → RR (quantum 16)
            // LEVEL 2 → FCFS
            // ------------------------

            if (level == 0 || level == 1) {

                int quantum = (level == 0) ? Q0_QUANTUM : Q1_QUANTUM;
                int used = 0;

                while (used < quantum && current.getRemainingTime() > 0) {

                    // Execute 1 time unit
                    gantt.add(current.getPid());
                    time++;
                    used++;
                    current.setRemainingTime(current.getRemainingTime() - 1);

                    // Add new arrivals (ALWAYS to Q0)
                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Q0.add(processes.get(arrivalIndex));
                        arrivalIndex++;
                    }

                    // Finished inside quantum?
                    if (current.getRemainingTime() == 0) {
                        current.setCompletionTime(time);
                        current.setTurnaroundTime(time - current.getArrivalTime());
                        current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                        completed++;
                        break;
                    }
                }

                // If not finished
                if (current.getRemainingTime() > 0) {
                    // FULL quantum used → DEMOTE
                    if (used == quantum) {
                        if (level == 0) Q1.add(current);
                        else Q2.add(current);
                    }
                    // Didn't use full quantum → put back at SAME level
                    else {
                        if (level == 0) Q0.add(current);
                        else Q1.add(current);
                    }
                }

                continue;
            }

            // ------------------------
            // LEVEL 2 → FCFS
            // ------------------------
            if (level == 2) {

                while (current.getRemainingTime() > 0) {

                    // Preempt if Q0 has new arrivals
                    if (!Q0.isEmpty()) {
                        Q2.add(current);
                        current = null;
                        break;
                    }

                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    // Add new arrivals
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

                continue;
            }
        }

        return new SimulationResult(gantt, processes, time);
    }
}
