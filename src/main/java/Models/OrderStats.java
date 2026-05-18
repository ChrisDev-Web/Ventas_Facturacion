package Models;

import java.math.BigDecimal;

public class OrderStats {

    private int pendingCount;
    private int convertedCount;
    private int cancelledCount;
    private BigDecimal pendingAmount;

    public OrderStats() {
        this.pendingAmount = BigDecimal.ZERO;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getConvertedCount() {
        return convertedCount;
    }

    public void setConvertedCount(int convertedCount) {
        this.convertedCount = convertedCount;
    }

    public int getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(int cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }
}
