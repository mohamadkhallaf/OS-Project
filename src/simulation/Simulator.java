package simulation;

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

public class Simulator {

    public List<Process> loadProcesses(String filePath) {
        try {
            return CSVReader.readProcesses(Path.of(filePath));
        } catch (Exception e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Process> copyList(List<Process> original) {
        List<Process> copy = new ArrayList<>();
        for (Process p : original) {
            copy.add(p.copy());
        }
        return copy;
    }

    public void runAll(String filePath) {
        List<Process> processes = loadProcesses(filePath);

        if (processes.isEmpty()) {
            System.out.println("No processes found. Check your CSV file.");
            return;
        }

        FCFS fcfs = new FCFS();
        SimulationResult r1 = fcfs.run(copyList(processes));
        OutputFormatter.printResults("FCFS", r1);

        SJF sjf = new SJF();
        SimulationResult r2 = sjf.run(copyList(processes));
        OutputFormatter.printResults("SJF (Non-Preemptive)", r2);

        PriorityNP pnp = new PriorityNP();
        SimulationResult r3 = pnp.run(copyList(processes));
        OutputFormatter.printResults("Priority (Non-Preemptive)", r3);

        MLQNP mlqnp = new MLQNP();
        SimulationResult r4 = mlqnp.run(copyList(processes));
        OutputFormatter.printResults("MLQ (Non-Preemptive)", r4);

        RR rr = new RR(4);
        SimulationResult r5 = rr.run(copyList(processes));
        OutputFormatter.printResults("Round Robin (Quantum = 4)", r5);

        PriorityPreemptive pp = new PriorityPreemptive();
        SimulationResult r6 = pp.run(copyList(processes));
        OutputFormatter.printResults("Priority (Preemptive)", r6);

        MLQPreemptive mlq = new MLQPreemptive(4);
        SimulationResult r7 = mlq.run(copyList(processes));
        OutputFormatter.printResults("MLQ (Preemptive)", r7);

        MLFQ mlfq = new MLFQ();
        SimulationResult r8 = mlfq.run(copyList(processes));
        OutputFormatter.printResults("MLFQ", r8);

        System.out.println("\nAll algorithms executed successfully!");
    }
}