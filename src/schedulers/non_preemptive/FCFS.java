package schedulers.non_preemptive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import process.Process;
import simulation.SimulationResult;

public class FCFS {

    public SimulationResult run(List<Process> processes) {
        Collections.sort(processes, Comparator.comparingInt(Process::getArrivalTime));

        List<String> gantt = new ArrayList<>();
        int time = 0;

        for (Process p : processes) {
            while (time < p.getArrivalTime()) {
                gantt.add("idle");
                time++;
            }

            p.setStartTime(time);
            p.setResponseTime(time - p.getArrivalTime());

            for (int i = 0; i < p.getBurstTime(); i++) {
                gantt.add(p.getPid());
                time++;
            }

            p.setCompletionTime(time);
            p.setTurnaroundTime(time - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        return new SimulationResult(gantt, processes, time);
    }
}