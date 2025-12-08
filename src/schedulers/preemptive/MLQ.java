package schedulers.preemptive;

import java.util.*;
import process.Process;
import simulation.SimulationResult;

public class MLQ {

    private final int quantum;

    public MLQ(int quantum) {
        this.quantum = quantum;
    }
    @Override
    public String toString() {
        return String.format(
        "MLQ\n" +
        "   Foreground : RR (q = %d)\n" +
        "   Background : FCFS",
        quantum
    );
}



    public SimulationResult run(List<Process> originalProcesses) {
        List<Process> processes = new ArrayList<>();
        for (Process p : originalProcesses) processes.add(p.copy());

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        int n = processes.size();
        int arrivalIndex = 0;

        Queue<Process> foreground = new LinkedList<>();
        Deque<Process> background = new LinkedList<>();

        List<String> gantt = new ArrayList<>();
        int time = 0;
        int completed = 0;

        Process runningBackground = null;

        while (completed < n) {
            while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                Process arriving = processes.get(arrivalIndex);
                if (arriving.getPriority() <= 2) foreground.add(arriving);
                else background.add(arriving);
                arrivalIndex++;
            }

            if (!foreground.isEmpty()) {
                Process current = foreground.poll();

                if (current.getStartTime() == null) {
                    current.setStartTime(time);
                    current.setResponseTime(time - current.getArrivalTime());
                }

                int runTime = Math.min(quantum, current.getRemainingTime());
                for (int i = 0; i < runTime; i++) {
                    gantt.add(current.getPid());
                    current.setRemainingTime(current.getRemainingTime() - 1);
                    time++;

                    while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                        Process arriving = processes.get(arrivalIndex);
                        if (arriving.getPriority() <= 2) foreground.add(arriving);
                        else background.add(arriving);
                        arrivalIndex++;
                    }
                }

                if (current.getRemainingTime() == 0) {
                    current.setCompletionTime(time);
                    current.setTurnaroundTime(time - current.getArrivalTime());
                    current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                    completed++;
                } else {
                    foreground.add(current);
                }

                if (runningBackground != null) {
                }

                continue;
            }

            if (runningBackground == null) {
                if (!background.isEmpty()) {
                    runningBackground = background.poll();
                    if (runningBackground.getStartTime() == null) {
                        runningBackground.setStartTime(time);
                        runningBackground.setResponseTime(time - runningBackground.getArrivalTime());
                    }
                }
            }

            if (runningBackground != null) {
                gantt.add(runningBackground.getPid());
                runningBackground.setRemainingTime(runningBackground.getRemainingTime() - 1);
                time++;

                while (arrivalIndex < n && processes.get(arrivalIndex).getArrivalTime() <= time) {
                    Process arriving = processes.get(arrivalIndex);
                    if (arriving.getPriority() <= 2) {
                        ((LinkedList<Process>) background).addFirst(runningBackground);
                        runningBackground = null;
                        foreground.add(arriving);
                    } else {
                        background.add(arriving);
                    }
                    arrivalIndex++;
                }

                if (runningBackground != null && runningBackground.getRemainingTime() == 0) {
                    runningBackground.setCompletionTime(time);
                    runningBackground.setTurnaroundTime(time - runningBackground.getArrivalTime());
                    runningBackground.setWaitingTime(runningBackground.getTurnaroundTime() - runningBackground.getBurstTime());
                    completed++;
                    runningBackground = null;
                }

                continue;
            }

            gantt.add("idle");
            time++;
        }

        return new SimulationResult(gantt, processes, time);
    }
}