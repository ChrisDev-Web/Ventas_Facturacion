package Models;

import java.math.BigDecimal;

public class SaleHistoryStats {

    private BigDecimal totalSales;
    private BigDecimal averageTicket;
    private BigDecimal totalReturns;
    private int discountSalesCount;

    public SaleHistoryStats() {
        this.totalSales = BigDecimal.ZERO;
        this.averageTicket = BigDecimal.ZERO;
        this.totalReturns = BigDecimal.ZERO;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getAverageTicket() {
        return averageTicket;
    }

    public void setAverageTicket(BigDecimal averageTicket) {
        this.averageTicket = averageTicket;
    }

    public BigDecimal getTotalReturns() {
        return totalReturns;
    }

    public void setTotalReturns(BigDecimal totalReturns) {
        this.totalReturns = totalReturns;
    }

    public int getDiscountSalesCount() {
        return discountSalesCount;
    }

    public void setDiscountSalesCount(int discountSalesCount) {
        this.discountSalesCount = discountSalesCount;
    }
}