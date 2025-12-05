package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a CSV file and converts each row into a Process object.
 * Expected format:
 *
 *   PID,Arrival,Burst,Priority
 *
 * Features:
 * - Ignores blank lines and comment lines starting with '#'
 * - Automatically skips header lines
 * - Validates numeric fields with helpful error messages
 * - Enforces burstTime > 0 and arrivalTime >= 0
 */
public class CSVReader {

    public static List<Process> readProcesses(Path path) throws IOException {

        List<Process> processes = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {

            String line;
            int lineNumber = 0;
            boolean headerChecked = false;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines or comments
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                // Detect and skip header once
                if (!headerChecked) {
                    String lower = line.toLowerCase();
                    if (lower.startsWith("pid")) {
                        headerChecked = true;
                        continue;
                    }
                    headerChecked = true;
                }

                // Split row into CSV fields
                String[] parts = line.split(",");

                if (parts.length < 4) {
                    throw new IllegalArgumentException(
                            "Error at line " + lineNumber +
                            ": Expected 4 columns (PID,Arrival,Burst,Priority) but found " +
                            parts.length + ". Line: '" + line + "'"
                    );
                }

                // Extract and clean fields
                String pid        = parts[0].trim();
                String arrivalStr = parts[1].trim();
                String burstStr   = parts[2].trim();
                String priorityStr = parts[3].trim();

                if (pid.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Error at line " + lineNumber + ": PID is empty. Line: '" + line + "'"
                    );
                }

                // Convert numeric fields with custom errors
                int arrival = parseInt(arrivalStr, "Arrival", lineNumber);
                int burst   = parseInt(burstStr, "Burst", lineNumber);
                int priority = parseInt(priorityStr, "Priority", lineNumber);

                if (arrival < 0)
                    throw new IllegalArgumentException(
                            "Error at line " + lineNumber + ": Arrival must be >= 0. Found: " + arrival
                    );

                if (burst <= 0)
                    throw new IllegalArgumentException(
                            "Error at line " + lineNumber + ": Burst must be > 0. Found: " + burst
                    );

                // Create and store the process
                processes.add(new Process(pid, arrival, burst, priority));
            }
        }

        return processes;
    }

    private static int parseInt(String value, String fieldName, int lineNumber) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Error at line " + lineNumber + ": " + fieldName +
                    " '" + value + "' is not a valid integer.", e
            );
        }
    }
}
