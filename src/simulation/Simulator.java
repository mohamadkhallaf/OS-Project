package simulation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import process.CSVReader;
import process.Process;
import schedulers.non_preemptive.FCFS;
import schedulers.non_preemptive.PriorityNP;
import schedulers.non_preemptive.SJF;
import schedulers.preemptive.MLFQ;
import schedulers.preemptive.MLQ;
import schedulers.preemptive.PriorityPreemptive;
import schedulers.preemptive.RR;

public class Simulator {


    public List<Process> loadProcesses(String filePath, int dataset) {
        try {
            return CSVReader.readProcesses(Path.of(filePath), dataset);
        } catch (Exception e) {
            System.out.println("Error loading dataset " + dataset + ": " + e.getMessage());
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


    public void runAll(String filePath, int dataset, int quantum) {

        List<Process> processes = loadProcesses(filePath, dataset);

        if (processes.isEmpty()) {
            System.out.println("Dataset " + dataset + " is empty or missing.");
            return;
        }

        System.out.println("\n====================================================");
        System.out.println(" RUNNING ALL SCHEDULERS FOR DATASET " + dataset);
        System.out.println("====================================================");


        OutputFormatter.printResults("FCFS", new FCFS().run(copyList(processes)));
        OutputFormatter.printResults("SJF (Non-Preemptive)", new SJF().run(copyList(processes)));
        OutputFormatter.printResults("Priority (Non-Preemptive)", new PriorityNP().run(copyList(processes)));
        

        RR rr = new RR(quantum);
        OutputFormatter.printResults(rr.toString(), rr.run(copyList(processes)));
        OutputFormatter.printResults("Priority (Preemptive)", new PriorityPreemptive().run(copyList(processes)));
        MLQ mlq = new MLQ(quantum);
        OutputFormatter.printResults(mlq.toString(), mlq.run(copyList(processes)));

        MLFQ mlfq = new MLFQ(quantum);
        OutputFormatter.printResults(mlfq.toString(), mlfq.run(copyList(processes)));



        System.out.println("\nCompleted Dataset " + dataset);
        System.out.println("====================================================\n");
    }
}
