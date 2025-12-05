package simulation;

import process.Process;
import process.CSVReader;

import schedulers.non_preemptive.FCFS;
import schedulers.non_preemptive.SJF;
import schedulers.non_preemptive.PriorityNP;

import schedulers.preemptive.RR;
import schedulers.preemptive.MLQPreemptive;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Simulator {

    // Load processes from CSV file
    public List<Process> loadProcesses(String filePath) {
        try {
            return CSVReader.readProcesses(Path.of(filePath));
        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Make a deep copy of a process list
    private List<Process> copyList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copy());
        }
        return copy;
    }

    // Run ALL schedulers and send results to OutputFormatter
    public void runAll(String filePath) {

        // Step 1: Load CSV
        List<Process> processes = loadProcesses(filePath);

        if (processes.isEmpty()) {
            System.out.println("No processes to simulate.");
            return;
        }

        OutputFormatter formatter = new OutputFormatter();

        // ------------------------------
        // FCFS
        // ------------------------------
        FCFS fcfs = new FCFS();
        SimulationResult r1 = fcfs.run(copyList(processes));
        formatter.print("FCFS", r1);

        // ------------------------------
        // SJF (Non-preemptive)
        // ------------------------------
        SJF sjf = new SJF();
        SimulationResult r2 = sjf.run(copyList(processes));
        formatter.print("SJF (Non-Preemptive)", r2);

        // ------------------------------
        // Priority (Non-preemptive)
        // ------------------------------
        PriorityNP pnp = new PriorityNP();
        SimulationResult r3 = pnp.run(copyList(processes));
        formatter.print("Priority (Non-Preemptive)", r3);

        // ------------------------------
        // Round Robin (Preemptive)
        // ------------------------------
        RR rr = new RR(4); // quantum = 4
        SimulationResult r4 = rr.run(copyList(processes));
        formatter.print("Round Robin (q=4)", r4);

        // ------------------------------
        // Multi-Level Queue (PREEMPTIVE)
        // ------------------------------
        MLQPreemptive mlq = new MLQPreemptive(4); // foreground uses RR(4)
        SimulationResult r5 = mlq.run(copyList(processes));
        formatter.print("MLQ (Preemptive)", r5);
    }
}
