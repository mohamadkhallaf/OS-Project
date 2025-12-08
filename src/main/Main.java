package main;

import java.util.Scanner;
import simulation.Simulator;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Simulator simulator = new Simulator();

        System.out.println("=== CPU Scheduling Simulator ===");


        System.out.print("How many datasets do you want to run? ");
        int count = sc.nextInt();

        int[] datasets = new int[count];

        for (int i = 0; i < count; i++) {
            System.out.print("Enter dataset number #" + (i + 1) + " (1–7): ");
            datasets[i] = sc.nextInt();
        }

    
        System.out.print("Enter Round Robin time quantum: ");
        int quantum = sc.nextInt();

        String file = "processes.csv";

        for (int d : datasets) {
            simulator.runAll(file, d, quantum);
        }

        sc.close();
    }
}
