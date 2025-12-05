package process;

public class Process implements Comparable<Process> {

    // ------------ BASIC PROCESS INFO (from CSV) ------------
    private String pid;       // Process ID (P1, P2...)
    private int arrivalTime;  // When the process arrives to the ready queue
    private int burstTime;    // Total CPU time required
    private int priority;     // Lower number = higher priority

    // ------------ VALUES USED DURING SIMULATION ------------
    private int remainingTime;    // Remaining CPU time (important for RR, SRT, Preemptive Priority)
    private Integer startTime;    // First time the process enters CPU
    private Integer completionTime; // Time when process finishes execution

    // ------------ METRICS REQUIRED BY PROJECT ------------
    private int waitingTime;      // Total time spent waiting in ready queue
    private int turnaroundTime;   // completionTime - arrivalTime
    private int responseTime;     // startTime - arrivalTime

    // ------------ CONSTRUCTOR ------------
    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;

        // Initially remaining time = burst time (full job)
        this.remainingTime = burstTime;

        // Null until assigned by scheduler
        this.startTime = null;
        this.completionTime = null;

        // Metrics initialized to 0 (computed later)
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.responseTime = 0;
    }

    // ------------ GETTERS AND SETTERS ------------
    public String getPid() { return pid; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public Integer getStartTime() { return startTime; }
    public void setStartTime(int startTime) { this.startTime = startTime; }

    public Integer getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public int getResponseTime() { return responseTime; }
    public void setResponseTime(int responseTime) { this.responseTime = responseTime; }

    // ------------ CLONING (important so each algorithm gets a fresh process list) ------------
    public Process copy() {
        return new Process(pid, arrivalTime, burstTime, priority);
    }

    // ------------ FOR SORTING BY ARRIVAL TIME ------------
    @Override
    public int compareTo(Process other) {
        return Integer.compare(this.arrivalTime, other.arrivalTime);
    }

    @Override
    public String toString() {
        return pid + " [arrival=" + arrivalTime + ", burst=" + burstTime +
               ", priority=" + priority + "]";
    }
}
