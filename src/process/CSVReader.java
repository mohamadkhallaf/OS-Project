package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<Process> readProcesses(Path path) throws IOException {
        List<Process> processes = new ArrayList<>();
        
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                    
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().contains("pid"))
                        continue;
                }
                
                String[] parts = line.split(",");
                String pid = parts[0].trim();
                int arrival = Integer.parseInt(parts[1].trim());
                int burst = Integer.parseInt(parts[2].trim());
                int priority = Integer.parseInt(parts[3].trim());
                
                processes.add(new Process(pid, arrival, burst, priority));
            }
        }
        
        return processes;
    }
}