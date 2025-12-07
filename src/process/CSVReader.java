package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<Process> readProcesses(Path path, int dataset) throws IOException {

        List<Process> processes = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {

            String line;
            boolean header = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                if (!header) {
                    header = true;
                    continue; // skip header
                }

                String[] parts = line.split(",");

                if (parts.length < 5)
                    continue; // ignore malformed lines

                int fileDataset = Integer.parseInt(parts[0].trim());
                if (fileDataset != dataset)
                    continue; // skip other datasets

                String pid = parts[1].trim();
                int arrival = Integer.parseInt(parts[2].trim());
                int burst = Integer.parseInt(parts[3].trim());
                int priority = Integer.parseInt(parts[4].trim());

                processes.add(new Process(pid, arrival, burst, priority));
            }
        }

        return processes;
    }

}