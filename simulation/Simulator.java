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

    // ---------------------------------------------------------
    // LOAD PROCESSES FROM CSV
    // ---------------------------------------------------------
    public List<Process> loadProcesses(String filePath) {
        try {
            return CSVReader.readProcesses(Path.of(filePath));
        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ---------------------------------------------------------
    // MAKE A DEEP COPY OF A PROCESS LIST
    // (Each scheduler must use a fresh clean list)
    // ---------------------------------------------------------
    private List<Process> copyList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copy());
        }
        return copy;
    }

    // ---------------------------------------------------------
    // RUN ALL SCHEDULING ALGORITHMS
    // ---------------------------------------------------------
    public void runAll(String filePath) {

        // 1. Load processes
        List<Process> processes = loadProcesses(filePath);

        if (processes.isEmpty()) {
            System.out.println("No processes found. Check your CSV file.");
            return;
        }

        // -------------------------------
        // FCFS
        // -------------------------------
        FCFS fcfs = new FCFS();
        SimulationResult r1 = fcfs.run(copyList(processes));
        OutputFormatter.printResults("FCFS", r1);

        // -------------------------------
        // SJF (Non-Preemptive)
        // -------------------------------
        SJF sjf = new SJF();
        SimulationResult r2 = sjf.run(copyList(processes));
        OutputFormatter.printResults("SJF (Non-Preemptive)", r2);

        // -------------------------------
        // Priority (Non-Preemptive)
        // -------------------------------
        PriorityNP pnp = new PriorityNP();
        SimulationResult r3 = pnp.run(copyList(processes));
        OutputFormatter.printResults("Priority (Non-Preemptive)", r3);

        // -------------------------------
        // Round Robin (q = 4)
        // -------------------------------
        RR rr = new RR(4);
        SimulationResult r4 = rr.run(copyList(processes));
        OutputFormatter.printResults("Round Robin (Quantum = 4)", r4);

        // -------------------------------
        // MLQ (Preemptive)
        // -------------------------------
        MLQPreemptive mlq = new MLQPreemptive(4);
        SimulationResult r5 = mlq.run(copyList(processes));
        OutputFormatter.printResults("MLQ (Preemptive)", r5);

        System.out.println("\nAll algorithms executed successfully!");
    }
}
