package Models;

public class StockAlertSummary {

    private int warningCount;
    private int criticalCount;
    private int urgentCount;
    private int totalAlerts;

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getCriticalCount() {
        return criticalCount;
    }

    public void setCriticalCount(int criticalCount) {
        this.criticalCount = criticalCount;
    }

    public int getUrgentCount() {
        return urgentCount;
    }

    public void setUrgentCount(int urgentCount) {
        this.urgentCount = urgentCount;
    }

    public int getTotalAlerts() {
        return totalAlerts;
    }

    public void setTotalAlerts(int totalAlerts) {
        this.totalAlerts = totalAlerts;
    }
}
