package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private int idOrder;
    private String orderSeries;
    private int orderNumber;
    private String orderCode;
    private int idUser;
    private String userName;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDate;

    private Integer idClient;
    private String customerName;
    private Integer customerDocumentTypeId;
    private String customerDocumentTypeName;
    private String customerDocumentNumber;

    private String notes;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal igvAmount;
    private BigDecimal total;
    private String status;
    private Integer linkedSaleId;
    private String linkedVoucherCode;
    private LocalDateTime convertedAt;
    private LocalDateTime cancelledAt;

    private List<OrderDetail> details;

    public Order() {
        this.orderSeries = "O001";
        this.orderCode = "";
        this.notes = "";
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.igvAmount = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.status = "PENDIENTE";
        this.details = new ArrayList<>();
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public String getOrderSeries() {
        return orderSeries;
    }

    public void setOrderSeries(String orderSeries) {
        this.orderSeries = orderSeries;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(LocalDateTime expectedDate) {
        this.expectedDate = expectedDate;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustomerDocumentTypeId() {
        return customerDocumentTypeId;
    }

    public void setCustomerDocumentTypeId(Integer customerDocumentTypeId) {
        this.customerDocumentTypeId = customerDocumentTypeId;
    }

    public String getCustomerDocumentTypeName() {
        return customerDocumentTypeName;
    }

    public void setCustomerDocumentTypeName(String customerDocumentTypeName) {
        this.customerDocumentTypeName = customerDocumentTypeName;
    }

    public String getCustomerDocumentNumber() {
        return customerDocumentNumber;
    }

    public void setCustomerDocumentNumber(String customerDocumentNumber) {
        this.customerDocumentNumber = customerDocumentNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getIgvAmount() {
        return igvAmount;
    }

    public void setIgvAmount(BigDecimal igvAmount) {
        this.igvAmount = igvAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLinkedSaleId() {
        return linkedSaleId;
    }

    public void setLinkedSaleId(Integer linkedSaleId) {
        this.linkedSaleId = linkedSaleId;
    }

    public String getLinkedVoucherCode() {
        return linkedVoucherCode;
    }

    public void setLinkedVoucherCode(String linkedVoucherCode) {
        this.linkedVoucherCode = linkedVoucherCode;
    }

    public LocalDateTime getConvertedAt() {
        return convertedAt;
    }

    public void setConvertedAt(LocalDateTime convertedAt) {
        this.convertedAt = convertedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }
}
