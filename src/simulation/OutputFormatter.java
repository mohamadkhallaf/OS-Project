package simulation;

import java.util.List;
import process.Process;

public class OutputFormatter {

    /**
     * Print full results of a scheduling algorithm.
     */
    public static void printResults(String algorithmName, SimulationResult result) {

        System.out.println("\n==============================================");
        System.out.println("       RESULTS FOR: " + algorithmName);
        System.out.println("==============================================\n");

        printGanttChart(result.getGanttChart());
        printProcessTable(result.getProcesses());
        printOverallMetrics(result);
    }



    // -----------------------------------------------------------
    // GANTT CHART
    // -----------------------------------------------------------
    private static void printGanttChart(List<String> gantt) {

        System.out.println("GANTT CHART:");
        System.out.print("| ");

        for (String slot : gantt) {
            System.out.print(slot + " | ");
        }

        System.out.println("\nTime: 0 → " + gantt.size());
        System.out.println();
    }



    // -----------------------------------------------------------
    // PER-PROCESS TABLE
    // -----------------------------------------------------------
    private static void printProcessTable(List<Process> processes) {

        System.out.println("PROCESS METRICS:");
        System.out.printf("%-6s %-8s %-8s %-10s %-12s %-12s\n",
                "PID", "Arrive", "Burst", "Waiting", "Turnaround", "Response");

        for (Process p : processes) {
            System.out.printf("%-6s %-8d %-8d %-10d %-12d %-12d\n",
                    p.getPid(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime(),
                    p.getResponseTime()
            );
        }

        System.out.println();
    }



    // -----------------------------------------------------------
    // OVERALL METRICS
    // -----------------------------------------------------------
    private static void printOverallMetrics(SimulationResult result) {

        List<Process> processes = result.getProcesses();
        int n = processes.size();
        int totalTime = result.getTotalTime();

        double avgWT = 0, avgTT = 0, avgRT = 0;
        int busyTime = 0;

        // Calculate averages + busy time
        for (Process p : processes) {
            avgWT += p.getWaitingTime();
            avgTT += p.getTurnaroundTime();
            avgRT += p.getResponseTime();
        }

        // Count number of non-idle slots in Gantt chart
        for (String s : result.getGanttChart()) {
            if (!s.equals("idle")) busyTime++;
        }

        avgWT /= n;
        avgTT /= n;
        avgRT /= n;

        double cpuUtil = (busyTime * 100.0) / totalTime;
        double throughput = (double) n / totalTime;

        System.out.println("OVERALL METRICS:");
        System.out.printf("Average Waiting Time     : %.2f\n", avgWT);
        System.out.printf("Average Turnaround Time  : %.2f\n", avgTT);
        System.out.printf("Average Response Time    : %.2f\n", avgRT);
        System.out.printf("CPU Utilization          : %.2f%%\n", cpuUtil);
        System.out.printf("Throughput               : %.4f processes/unit time\n", throughput);

        System.out.println("\n----------------------------------------------\n");
    }
}
