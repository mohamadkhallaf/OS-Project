package simulation;

import process.Process;
import java.util.List;

public class SimulationResult {

    private List<String> ganttChart;   // Stores timeline: P1, P1, idle, P2...
    private List<Process> processes;   // Stores updated processes after scheduling
    private int totalTime;             // How long the CPU ran

    // Constructor
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
