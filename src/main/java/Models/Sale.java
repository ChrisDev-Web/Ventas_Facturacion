package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {

    private int idSale;
    private int idUser;
    private String userName;
    private int idPaymentMethod;
    private String paymentMethodName;
    private LocalDateTime saleDate;

    private String documentKind;
    private String documentLabel;
    private String voucherSeries;
    private int voucherNumber;
    private String voucherCode;
    
    private String customerName;
    private Integer customerDocumentTypeId;
    private String customerDocumentTypeName;
    private String customerDocumentNumber;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal igvAmount;
    private BigDecimal total;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;

    private int status;

    private List<SaleDetail> details;

    public Sale() {
        this.subtotal = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.igvAmount = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.changeAmount = BigDecimal.ZERO;
        this.details = new ArrayList<>();
        this.documentKind = "TICKET";
    }

    public int getIdSale() {
        return idSale;
    }

    public void setIdSale(int idSale) {
        this.idSale = idSale;
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

    public int getIdPaymentMethod() {
        return idPaymentMethod;
    }

    public void setIdPaymentMethod(int idPaymentMethod) {
        this.idPaymentMethod = idPaymentMethod;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getDocumentKind() {
        return documentKind;
    }

    public void setDocumentKind(String documentKind) {
        this.documentKind = documentKind;
    }

    public String getDocumentLabel() {
        return documentLabel;
    }

    public void setDocumentLabel(String documentLabel) {
        this.documentLabel = documentLabel;
    }

    public String getVoucherSeries() {
        return voucherSeries;
    }

    public void setVoucherSeries(String voucherSeries) {
        this.voucherSeries = voucherSeries;
    }

    public int getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(int voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
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

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }
}