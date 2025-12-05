package simulation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import process.CSVReader;
import process.Process;
import schedulers.non_preemptive.FCFS;
import schedulers.non_preemptive.PriorityNP;
import schedulers.non_preemptive.SJF;
import schedulers.preemptive.PriorityPreemptive;
import schedulers.preemptive.RR;

public class Simulator {

    // ------------------------------------------------------
    // Load process list from CSV file
    // ------------------------------------------------------
    public List<Process> loadProcesses(String filePath) {
        try {
            return CSVReader.readProcesses(Path.of(filePath));
        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ------------------------------------------------------
    // Make a deep copy of process list so scheduling algorithms
    // do not modify each other's data
    // ------------------------------------------------------
    private List<Process> copyList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copy());
        }
        return copy;
    }

    // ------------------------------------------------------
    // Run all required algorithms
    // ------------------------------------------------------
    public void runAll(String filePath) {

        // Step 1: Load processes
        List<Process> processes = loadProcesses(filePath);

        if (processes.isEmpty()) {
            System.out.println("No processes to simulate.");
            return;
        }

        // ------------------------------------------
        // 1. FCFS
        // ------------------------------------------
        FCFS fcfs = new FCFS();
        SimulationResult r1 = fcfs.run(copyList(processes));
        OutputFormatter.printResults("FCFS", r1);

        // ------------------------------------------
        // 2. SJF (Non-preemptive)
        // ------------------------------------------
        SJF sjf = new SJF();
        SimulationResult r2 = sjf.run(copyList(processes));
        OutputFormatter.printResults("SJF (Non-Preemptive)", r2);

        // ------------------------------------------
        // 3. Priority (Non-preemptive)
        // ------------------------------------------
        PriorityNP pnp = new PriorityNP();
        SimulationResult r3 = pnp.run(copyList(processes));
        OutputFormatter.printResults("Priority (Non-Preemptive)", r3);

        // ------------------------------------------
        // 4. Round Robin (Preemptive)
        // ------------------------------------------
        RR rr = new RR(4); // quantum = 4
        SimulationResult r4 = rr.run(copyList(processes));
        OutputFormatter.printResults("Round Robin (q=4)", r4);

        // ------------------------------------------
        // 5. Priority (Preemptive)
        // ------------------------------------------
        PriorityPreemptive pp = new PriorityPreemptive();
        SimulationResult r5 = pp.run(copyList(processes));
        OutputFormatter.printResults("Priority (Preemptive)", r5);

        // ------------------------------------------
        // OPTIONAL:
        // Uncomment these if you implement them
        // ------------------------------------------

        /*
        MLQNP mlqnp = new MLQNP();
        SimulationResult r6 = mlqnp.run(copyList(processes));
        OutputFormatter.printResults("MLQ (Non-Preemptive)", r6);

        MLFQ mlfq = new MLFQ();
        SimulationResult r7 = mlfq.run(copyList(processes));
        OutputFormatter.printResults("MLFQ", r7);
        */
    }
}
