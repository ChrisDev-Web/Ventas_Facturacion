package Models;

import java.math.BigDecimal;

public class SaleDetail {

    private int idSaleDetail;
    private int idSale;
    private int idProduct;
    private String productName;
    private int quantity;
    private BigDecimal originalUnitPrice;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal igvAmount;
    private BigDecimal unitPrice;
    private BigDecimal subtotalBeforeDiscount;
    private BigDecimal subtotal;

    public SaleDetail() {
        this.discountType = "NONE";
        this.discountValue = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.igvAmount = BigDecimal.ZERO;
        this.originalUnitPrice = BigDecimal.ZERO;
        this.unitPrice = BigDecimal.ZERO;
        this.subtotalBeforeDiscount = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public int getIdSaleDetail() {
        return idSaleDetail;
    }

    public void setIdSaleDetail(int idSaleDetail) {
        this.idSaleDetail = idSaleDetail;
    }

    public int getIdSale() {
        return idSale;
    }

    public void setIdSale(int idSale) {
        this.idSale = idSale;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(BigDecimal originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotalBeforeDiscount() {
        return subtotalBeforeDiscount;
    }

    public void setSubtotalBeforeDiscount(BigDecimal subtotalBeforeDiscount) {
        this.subtotalBeforeDiscount = subtotalBeforeDiscount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}