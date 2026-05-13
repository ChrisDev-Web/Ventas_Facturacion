package Models;

import java.math.BigDecimal;

public class DashboardSummary {

    private BigDecimal totalSales;
    private BigDecimal totalSalesGrowth;
    private BigDecimal daySales;
    private BigDecimal daySalesGrowth;
    private int orders;
    private BigDecimal ordersGrowth;
    private int newClients;
    private BigDecimal newClientsGrowth;
    private int productsSold;
    private BigDecimal productsSoldGrowth;
    private BigDecimal averageTicket;
    private BigDecimal averageTicketGrowth;
    private int lowStockProducts;
    private int outOfStockProducts;

    public DashboardSummary() {
        this.totalSales = BigDecimal.ZERO;
        this.totalSalesGrowth = BigDecimal.ZERO;
        this.daySales = BigDecimal.ZERO;
        this.daySalesGrowth = BigDecimal.ZERO;
        this.ordersGrowth = BigDecimal.ZERO;
        this.newClientsGrowth = BigDecimal.ZERO;
        this.productsSoldGrowth = BigDecimal.ZERO;
        this.averageTicket = BigDecimal.ZERO;
        this.averageTicketGrowth = BigDecimal.ZERO;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalSalesGrowth() {
        return totalSalesGrowth;
    }

    public void setTotalSalesGrowth(BigDecimal totalSalesGrowth) {
        this.totalSalesGrowth = totalSalesGrowth;
    }

    public BigDecimal getDaySales() {
        return daySales;
    }

    public void setDaySales(BigDecimal daySales) {
        this.daySales = daySales;
    }

    public BigDecimal getDaySalesGrowth() {
        return daySalesGrowth;
    }

    public void setDaySalesGrowth(BigDecimal daySalesGrowth) {
        this.daySalesGrowth = daySalesGrowth;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public BigDecimal getOrdersGrowth() {
        return ordersGrowth;
    }

    public void setOrdersGrowth(BigDecimal ordersGrowth) {
        this.ordersGrowth = ordersGrowth;
    }

    public int getNewClients() {
        return newClients;
    }

    public void setNewClients(int newClients) {
        this.newClients = newClients;
    }

    public BigDecimal getNewClientsGrowth() {
        return newClientsGrowth;
    }

    public void setNewClientsGrowth(BigDecimal newClientsGrowth) {
        this.newClientsGrowth = newClientsGrowth;
    }

    public int getProductsSold() {
        return productsSold;
    }

    public void setProductsSold(int productsSold) {
        this.productsSold = productsSold;
    }

    public BigDecimal getProductsSoldGrowth() {
        return productsSoldGrowth;
    }

    public void setProductsSoldGrowth(BigDecimal productsSoldGrowth) {
        this.productsSoldGrowth = productsSoldGrowth;
    }

    public BigDecimal getAverageTicket() {
        return averageTicket;
    }

    public void setAverageTicket(BigDecimal averageTicket) {
        this.averageTicket = averageTicket;
    }

    public BigDecimal getAverageTicketGrowth() {
        return averageTicketGrowth;
    }

    public void setAverageTicketGrowth(BigDecimal averageTicketGrowth) {
        this.averageTicketGrowth = averageTicketGrowth;
    }

    public int getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(int lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public int getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(int outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }
}
