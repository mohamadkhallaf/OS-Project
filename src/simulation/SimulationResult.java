package simulation;

import java.util.List;
import process.Process;

public class SimulationResult {

    private List<String> ganttChart;
    private List<Process> processes;
    private int totalTime;

    public SimulationResult(List<String> ganttChart, List<Process> processes, int totalTime) {
        this.ganttChart = ganttChart;
        this.processes = processes;
        this.totalTime = totalTime;
    }

    public List<String> getGanttChart() {
        return ganttChart;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public int getTotalTime() {
        return totalTime;
    }
}