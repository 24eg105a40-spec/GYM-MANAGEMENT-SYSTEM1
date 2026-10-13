package com.telemetry.dto;

public class DashboardSummaryDTO {
    private long totalVehicles;
    private long onlineVehicles;
    private long offlineVehicles;
    private long warningVehicles;
    private long maintenanceVehicles;
    private long activeAlerts;
    private double averageSpeed;
    private double averageBatteryLevel;
    private double fleetHealthScore;
    private boolean simulationRunning;

    public DashboardSummaryDTO() {}

    public DashboardSummaryDTO(long totalVehicles, long onlineVehicles, long offlineVehicles,
                               long warningVehicles, long maintenanceVehicles, long activeAlerts,
                               double averageSpeed, double averageBatteryLevel,
                               double fleetHealthScore, boolean simulationRunning) {
        this.totalVehicles = totalVehicles;
        this.onlineVehicles = onlineVehicles;
        this.offlineVehicles = offlineVehicles;
        this.warningVehicles = warningVehicles;
        this.maintenanceVehicles = maintenanceVehicles;
        this.activeAlerts = activeAlerts;
        this.averageSpeed = averageSpeed;
        this.averageBatteryLevel = averageBatteryLevel;
        this.fleetHealthScore = fleetHealthScore;
        this.simulationRunning = simulationRunning;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getOnlineVehicles() {
        return onlineVehicles;
    }

    public void setOnlineVehicles(long onlineVehicles) {
        this.onlineVehicles = onlineVehicles;
    }

    public long getOfflineVehicles() {
        return offlineVehicles;
    }

    public void setOfflineVehicles(long offlineVehicles) {
        this.offlineVehicles = offlineVehicles;
    }

    public long getWarningVehicles() {
        return warningVehicles;
    }

    public void setWarningVehicles(long warningVehicles) {
        this.warningVehicles = warningVehicles;
    }

    public long getMaintenanceVehicles() {
        return maintenanceVehicles;
    }

    public void setMaintenanceVehicles(long maintenanceVehicles) {
        this.maintenanceVehicles = maintenanceVehicles;
    }

    public long getActiveAlerts() {
        return activeAlerts;
    }

    public void setActiveAlerts(long activeAlerts) {
        this.activeAlerts = activeAlerts;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getAverageBatteryLevel() {
        return averageBatteryLevel;
    }

    public void setAverageBatteryLevel(double averageBatteryLevel) {
        this.averageBatteryLevel = averageBatteryLevel;
    }

    public double getFleetHealthScore() {
        return fleetHealthScore;
    }

    public void setFleetHealthScore(double fleetHealthScore) {
        this.fleetHealthScore = fleetHealthScore;
    }

    public boolean isSimulationRunning() {
        return simulationRunning;
    }

    public void setSimulationRunning(boolean simulationRunning) {
        this.simulationRunning = simulationRunning;
    }
}
