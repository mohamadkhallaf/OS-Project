package main;

import simulation.Simulator;

public class Main {

    public static void main(String[] args) {

        Simulator simulator = new Simulator();
        String file = "processes.csv";

        // List datasets you want to run
        int[] datasets = {1,2,3,4,5,6,7};

        for (int d : datasets) {
            simulator.runAll(file, d);
        }
    }
}
