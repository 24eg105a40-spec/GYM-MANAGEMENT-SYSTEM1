package com.telemetry.dto;

public class SimulationStatusDTO {
    private boolean running;
    private long intervalMs;
    private long tickCount;
    private String message;

    public SimulationStatusDTO() {}

    public SimulationStatusDTO(boolean running, long intervalMs, long tickCount, String message) {
        this.running = running;
        this.intervalMs = intervalMs;
        this.tickCount = tickCount;
        this.message = message;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public long getIntervalMs() {
        return intervalMs;
    }

    public void setIntervalMs(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public long getTickCount() {
        return tickCount;
    }

    public void setTickCount(long tickCount) {
        this.tickCount = tickCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
