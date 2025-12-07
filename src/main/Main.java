package main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import process.CSVReader;
import process.Process;
import schedulers.non_preemptive.FCFS;
import schedulers.non_preemptive.MLQNP;
import schedulers.non_preemptive.PriorityNP;
import schedulers.non_preemptive.SJF;
import schedulers.preemptive.MLFQ;
import schedulers.preemptive.MLQPreemptive;
import schedulers.preemptive.PriorityPreemptive;
import schedulers.preemptive.RR;
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

        // -------------------------------
        // MLQ (Non-Preemptive)
        // -------------------------------
        System.out.println("\n**** TESTING MLQ (Non-Preemptive) ****");
        MLQNP mlqnp = new MLQNP();
        SimulationResult r4 = mlqnp.run(copy(processes));
        OutputFormatter.printResults("MLQ (Non-Preemptive)", r4);

        System.out.println("\n===== END OF NON-PREEMPTIVE TESTS =====\n");

        // =====================================================
        //                 PREEMPTIVE TESTS
        // =====================================================

        // *************************************
        // 1. ROUND ROBIN
        // *************************************
        System.out.println("\n**** TESTING ROUND ROBIN (q=4) ****");
        RR rr = new RR(4);
        SimulationResult rrRes = rr.run(copy(processes));
        OutputFormatter.printResults("Round Robin (q=4)", rrRes);

        // *************************************
        // 2. PREEMPTIVE PRIORITY
        // *************************************
        System.out.println("\n**** TESTING PRIORITY (Preemptive) ****");
        PriorityPreemptive pp = new PriorityPreemptive();
        SimulationResult ppRes = pp.run(copy(processes));
        OutputFormatter.printResults("Priority (Preemptive)", ppRes);

        // *************************************
        // 3. MLQ PREEMPTIVE
        // *************************************
        System.out.println("\n**** TESTING MLQ Preemptive ****");
        MLQPreemptive mlq = new MLQPreemptive(4);
        SimulationResult mlqRes = mlq.run(copy(processes));
        OutputFormatter.printResults("MLQ (Preemptive)", mlqRes);

        // *************************************
        // 4. MULTI-LEVEL FEEDBACK QUEUE
        // *************************************
        System.out.println("\n**** TESTING MLFQ ****");
        MLFQ mlfq = new MLFQ();
        SimulationResult mlfqRes = mlfq.run(copy(processes));
        OutputFormatter.printResults("MLFQ", mlfqRes);

        System.out.println("\n===== END OF PREEMPTIVE TESTS =====\n");
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