package Models;

import java.math.BigDecimal;

public class DashboardChartItem {

    private String label;
    private BigDecimal amount;
    private BigDecimal comparisonAmount;
    private int quantity;

    public DashboardChartItem() {
        this.amount = BigDecimal.ZERO;
        this.comparisonAmount = BigDecimal.ZERO;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getComparisonAmount() {
        return comparisonAmount;
    }

    public void setComparisonAmount(BigDecimal comparisonAmount) {
        this.comparisonAmount = comparisonAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
