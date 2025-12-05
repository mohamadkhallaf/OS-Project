package simulation;

public class Metrics {

    // Per-process metrics
    private int waitingTime;
    private int turnaroundTime;
    private int responseTime;
    private int completionTime;

    // Constructor
    public Metrics(int waitingTime, int turnaroundTime, int responseTime, int completionTime) {
        this.waitingTime = waitingTime;
        this.turnaroundTime = turnaroundTime;
        this.responseTime = responseTime;
        this.completionTime = completionTime;
    }

    // Getters
    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    // Setters (optional, if you want to update metrics later)
    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }
}
