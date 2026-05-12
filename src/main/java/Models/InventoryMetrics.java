package Models;

import java.math.BigDecimal;

public class InventoryMetrics {

    private int activeProducts;
    private int totalUnits;
    private int outOfStockProducts;
    private int lowStockProducts;
    private BigDecimal totalCostValue;
    private BigDecimal totalSaleValue;

    public InventoryMetrics() {
        this.totalCostValue = BigDecimal.ZERO;
        this.totalSaleValue = BigDecimal.ZERO;
    }

    public int getActiveProducts() {
        return activeProducts;
    }

    public void setActiveProducts(int activeProducts) {
        this.activeProducts = activeProducts;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public int getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(int outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public BigDecimal getTotalCostValue() {
        return totalCostValue;
    }

    public void setTotalCostValue(BigDecimal totalCostValue) {
        this.totalCostValue = totalCostValue;
    }

    public BigDecimal getTotalSaleValue() {
        return totalSaleValue;
    }

    public void setTotalSaleValue(BigDecimal totalSaleValue) {
        this.totalSaleValue = totalSaleValue;
    }
}
