package simulation;

import java.util.*;
import process.Process;

public class OutputFormatter {

    private static final String MARK = "*";

    public static void printResults(String title, SimulationResult result) {
        System.out.println("\n==============================================");
        System.out.println("       RESULTS FOR: " + title);
        System.out.println("==============================================\n");

        printGanttTable(result);
        printMetrics(result);

        System.out.println("----------------------------------------------\n");
    }

    private static void printGanttTable(SimulationResult result) {
        List<String> gantt = result.getGanttChart();
        List<Process> processes = result.getProcesses();
        int totalTime = gantt.size();

        System.out.println("GANTT CHART:\n");

        System.out.print("      ");
        for (int t = 1; t <= totalTime; t++) {
            System.out.printf("| %-2d", t);
        }
        System.out.println("|");

        System.out.print("      ");
        for (int t = 0; t < totalTime; t++) {
            System.out.print("|---");
        }
        System.out.println("|");

        for (Process p : processes) {
            System.out.printf("%-5s ", p.getPid());

            for (int col = 1; col <= totalTime; col++) {
                int gIndex = col - 1;

                if (gantt.get(gIndex).equals(p.getPid())) {
                    System.out.print("| " + MARK + " ");
                } else {
                    System.out.print("|   ");
                }
            }
            System.out.println("|");
        }

        System.out.println();
    }

    private static void printMetrics(SimulationResult result) {
        List<Process> list = result.getProcesses();
        int n = list.size();
        int sumWT = 0, sumTT = 0, sumRT = 0;

        System.out.println("PROCESS METRICS:");
        System.out.printf("%-5s %-8s %-8s %-8s %-12s %-8s\n",
                "PID","Arrive","Burst","Waiting","Turnaround","Response");

        for (Process p : list) {
            System.out.printf("%-5s %-8d %-8d %-8d %-12d %-8d\n",
                    p.getPid(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime(),
                    p.getResponseTime()
            );

            sumWT += p.getWaitingTime();
            sumTT += p.getTurnaroundTime();
            sumRT += p.getResponseTime();
        }

        System.out.println();
        System.out.println("OVERALL METRICS:");
        System.out.printf("Average Waiting Time     : %.2f\n", sumWT / (double) n);
        System.out.printf("Average Turnaround Time  : %.2f\n", sumTT / (double) n);
        System.out.printf("Average Response Time    : %.2f\n", sumRT / (double) n);

        List<String> gantt = result.getGanttChart();

long busy = gantt.stream()
        .filter(x -> !x.equalsIgnoreCase("idle"))
        .count();

long total = gantt.size();

double utilization = (busy * 100.0) / total;
        System.out.printf("CPU Utilization          : %.2f%%\n", utilization);

        double throughput = n / (double) total;
        System.out.printf("Throughput               : %.4f processes/unit time\n", throughput);
    }
}