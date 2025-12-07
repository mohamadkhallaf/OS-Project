package process;

public class Process implements Comparable<Process> {

    private String pid;
    private int arrivalTime;
    private int burstTime;
    private int priority;

    private int remainingTime;
    private Integer startTime;
    private Integer completionTime;

    private int waitingTime;
    private int turnaroundTime;
    private int responseTime;

    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.startTime = null;
        this.completionTime = null;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.responseTime = 0;
    }

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

    public Process copy() {
        return new Process(pid, arrivalTime, burstTime, priority);
    }

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