package main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import process.Process;
import process.CSVReader;

import schedulers.non_preemptive.FCFS;
import schedulers.non_preemptive.SJF;
import schedulers.non_preemptive.PriorityNP;

import simulation.OutputFormatter;
import simulation.SimulationResult;

public class Main {

    public static void main(String[] args) {

        // Load processes from CSV
        List<Process> processes = null;

        try {
            processes = CSVReader.readProcesses(Path.of("processes.csv"));
        } catch (Exception e) {
            System.out.println("Failed to load CSV: " + e.getMessage());
            return;
        }

        if (processes.isEmpty()) {
            System.out.println("CSV file is empty or unreadable.");
            return;
        }

        // -------------------------------
        // FCFS TEST
        // -------------------------------
        System.out.println("\n**** TESTING FCFS ****");
        FCFS fcfs = new FCFS();
        SimulationResult r1 = fcfs.run(copy(processes));
        OutputFormatter.printResults("FCFS", r1);

        // -------------------------------
        // SJF (Non-Preemptive)
        // -------------------------------
        System.out.println("\n**** TESTING SJF (Non-Preemptive) ****");
        SJF sjf = new SJF();
        SimulationResult r2 = sjf.run(copy(processes));
        OutputFormatter.printResults("SJF (Non-Preemptive)", r2);

        // -------------------------------
        // PRIORITY (Non-Preemptive)
        // -------------------------------
        System.out.println("\n**** TESTING Priority (Non-Preemptive) ****");
        PriorityNP pnp = new PriorityNP();
        SimulationResult r3 = pnp.run(copy(processes));
        OutputFormatter.printResults("Priority (Non-Preemptive)", r3);

        System.out.println("\n===== END OF NON-PREEMPTIVE TESTS =====\n");
    }

    // Deep copy helper
    private static List<Process> copy(List<Process> list) {
        List<Process> newList = new ArrayList<>();
        for (Process p : list) {
            newList.add(p.copy());
        }
        return newList;
    }
}
