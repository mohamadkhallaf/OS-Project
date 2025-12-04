package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    /**
     * Read processes from a CSV file with columns:
     * PID, Arrival Time, Burst Time, Priority
     *
     * Behavior (per project requirements):
     * - Skip empty lines and lines that start with '#' (allow comments).
     * - Skip a header line if it begins with "PID" (case-insensitive).
     * - Trim whitespace around fields.
     * - Validate that each non-header line has exactly 4 fields.
     * - Parse arrival/burst/priority as integers and throw informative exceptions
     * on parse errors.
     *
     * Returns a list of Process objects (one per valid CSV row).
     *
     * @param path path to CSV file
     * @return List<Process>
     * @throws IOException              if file can't be read
     * @throws IllegalArgumentException if CSV contains invalid lines
     */
    public static List<Process> readProcesses(Path path) throws IOException {
        List<Process> processes = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            boolean headerChecked = false;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // skip blank lines
                if (line.isEmpty())
                    continue;

                // skip comment lines (optional, helpful during testing)
                if (line.startsWith("#"))
                    continue;

                // If header not yet checked, detect header like "PID,Arrival..."
                if (!headerChecked) {
                    String lower = line.toLowerCase();
                    if (lower.startsWith("pid") || lower.startsWith("pid,")) {
                        // header line — skip and mark as checked
                        headerChecked = true;
                        continue;
                    }
                    headerChecked = true; // even if not header, mark it checked so we don't re-check
                }

                // split by comma (CSV simple parser). Trim each part.
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "CSV parse error at line %d: expected 4 columns (PID,Arrival,Burst,Priority) but found %d. Line: '%s'",
                                    lineNumber, parts.length, line));
                }

                // take first 4 columns only (ignore extra columns)
                String pid = parts[0].trim();
                String arrivalStr = parts[1].trim();
                String burstStr = parts[2].trim();
                String priorityStr = parts[3].trim();

                if (pid.isEmpty()) {
                    throw new IllegalArgumentException(
                            String.format("CSV parse error at line %d: PID is empty. Line: '%s'", lineNumber, line));
                }

                int arrival;
                int burst;
                int priority;
                try {
                    arrival = Integer.parseInt(arrivalStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            String.format("CSV parse error at line %d: Arrival time '%s' is not an integer.",
                                    lineNumber, arrivalStr),
                            e);
                }
                try {
                    burst = Integer.parseInt(burstStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            String.format("CSV parse error at line %d: Burst time '%s' is not an integer.", lineNumber,
                                    burstStr),
                            e);
                }
                try {
                    priority = Integer.parseInt(priorityStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            String.format("CSV parse error at line %d: Priority '%s' is not an integer.", lineNumber,
                                    priorityStr),
                            e);
                }

                // Burst must be positive (project assumes positive CPU bursts)
                if (burst <= 0) {
                    throw new IllegalArgumentException(
                            String.format("CSV parse error at line %d: Burst time must be > 0. Found: %d", lineNumber,
                                    burst));
                }

                // Arrival must be >= 0
                if (arrival < 0) {
                    throw new IllegalArgumentException(
                            String.format("CSV parse error at line %d: Arrival time must be >= 0. Found: %d",
                                    lineNumber, arrival));
                }

                // Create Process object (uses your Process constructor)
                Process p = new Process(pid, arrival, burst, priority);
                processes.add(p);
            }
        }

        return processes;
    }
}
