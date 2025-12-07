package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLQPreemptive {

    private final int quantum; // Foreground RR quantum

    public MLQPreemptive(int quantum) {
        this.quantum = quantum;
    }

    public SimulationResult run(List<Process> originalProcesses) {

        // Deep copy input processes so we don't mutate caller data
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        // Sort by arrival time
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        int n = processes.size();
        int arrivalIndex = 0;

        // Ready queues (only contain processes that have arrived)
        Queue<Process> foreground = new LinkedList<>(); // RR
        Deque<Process> background = new LinkedList<>(); // FCFS (we use Deque to reinsert at front)

        List<String> gantt = new ArrayList<>();
        int time = 0;
        int completed = 0;

        // Currently running background process (so we can preempt and reinsert at front)
        Process runningBackground = null;

        while (completed < n) {

            // 1) Add arrivals at current time (and before running anything)
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Process arriving = processes.get(arrivalIndex);
                if (arriving.getPriority() <= 2) foreground.add(arriving);
                else background.add(arriving);
                arrivalIndex++;
            }

            // 2) If any foreground ready -> run foreground RR (always preempts background)
            if (!foreground.isEmpty()) {

                // Poll the next foreground process (RR)
                Process current = foreground.poll();

                // First time on CPU?
                if (current.getStartTime() == null) {
                    current.setStartTime(time);
                    current.setResponseTime(time - current.getArrivalTime());
                }

                // Run for up to 'quantum' time units (but still check for arrivals each time unit and add them to correct queues).
                int runTime = Math.min(quantum, current.getRemainingTime());
                for (int i = 0; i < runTime; i++) {
                    // Execute one time unit
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    // During execution, new processes may arrive — add them to their queues
                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Process arriving = processes.get(arrivalIndex);
                        if (arriving.getPriority() <= 2) foreground.add(arriving);
                        else background.add(arriving);
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
                    // Not finished -> requeue at tail of foreground (RR)
                    foreground.add(current);
                }

                // ensure any running background (if was running) is still stopped — foreground always preempts
                if (runningBackground != null) {
                    // it was already paused earlier when foreground arrived; keep it paused
                }

                continue; // scheduler loop: re-evaluate queues
            }

            // 3) No foreground ready → run background (FCFS)
            if (runningBackground == null) {
                // pick next background job if available
                if (!background.isEmpty()) {
                    runningBackground = background.poll();
                    if (runningBackground.getStartTime() == null) {
                        runningBackground.setStartTime(time);
                        runningBackground.setResponseTime(time - runningBackground.getArrivalTime());
                    }
                }
            }

            if (runningBackground != null) {
                // Run background one time unit at a time so we can preempt immediately if a foreground arrival appears
                // Before executing, check arrivals that might have exactly arrivalTime == time (already handled above),
                // but we still check again after execution.
                // Execute one time unit
                gantt.add(runningBackground.getPid());
                runningBackground.setRemainingTime(runningBackground.getRemainingTime() - 1);
                time++;

                // Add any new arrivals that appeared during this time unit
                while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                    Process arriving = processes.get(arrivalIndex);
                    if (arriving.getPriority() <= 2) {
                        // A foreground arrival should preempt the background immediately:
                        // - Put current background at front of background queue (to resume FCFS order later)
                        ((LinkedList<Process>) background).addFirst(runningBackground);
                        runningBackground = null; // preempted
                        // Add the arriving foreground
                        foreground.add(arriving);
                    } else {
                        background.add(arriving);
                    }
                    arrivalIndex++;
                }

                // If not preempted and finished, record metrics
                if (runningBackground != null && runningBackground.getRemainingTime() == 0) {
                    runningBackground.setCompletionTime(time);
                    runningBackground.setTurnaroundTime(time - runningBackground.getArrivalTime());
                    runningBackground.setWaitingTime(runningBackground.getTurnaroundTime() - runningBackground.getBurstTime());
                    completed++;
                    runningBackground = null;
                }

                continue; // scheduler loop
            }

            // 4) Nothing to run -> idle one unit
            gantt.add("idle");
            time++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}
