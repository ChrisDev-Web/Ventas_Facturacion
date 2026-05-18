package DAO;

import Config.Database;
import Models.Order;
import Models.OrderDetail;
import Models.OrderStats;
import Models.Sale;
import Models.SaleProductItem;
import Models.SelectOption;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public List<SaleProductItem> listProducts(String search, int idCategory, int idBrand) throws SQLException {
        String sql = "{CALL sp_order_product_catalog(?, ?, ?)}";
        List<SaleProductItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, idCategory);
            statement.setInt(3, idBrand);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    SaleProductItem item = new SaleProductItem();
                    item.setIdProduct(rs.getInt("id_product"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setStock(rs.getInt("stock"));
                    item.setImage(rs.getString("image"));
                    item.setIdCategory(rs.getInt("id_category"));
                    item.setCategoryName(rs.getString("category_name"));
                    item.setIdBrand(rs.getInt("id_brand"));
                    item.setBrandName(rs.getString("brand_name"));
                    list.add(item);
                }
            }
        }

        return list;
    }

    public List<SelectOption> listCategoryOptions() throws SQLException {
        return listOptions("{CALL sp_order_category_options()}");
    }

    public List<SelectOption> listBrandOptions() throws SQLException {
        return listOptions("{CALL sp_order_brand_options()}");
    }

    public List<SelectOption> listDocumentTypeOptions() throws SQLException {
        return listOptions("{CALL sp_order_document_type_options()}");
    }

    public List<SelectOption> listPaymentMethodOptions() throws SQLException {
        return listOptions("{CALL sp_order_payment_method_options()}");
    }

    private List<SelectOption> listOptions(String sql) throws SQLException {
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                list.add(new SelectOption(rs.getInt("id"), rs.getString("name")));
            }
        }

        return list;
    }

    public Order create(Order order) throws SQLException {
        String sql = "{CALL sp_order_create(?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillCreateUpdateStatement(statement, order);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Order result = new Order();
                    result.setIdOrder(rs.getInt("id_order"));
                    result.setOrderCode(rs.getString("order_code"));
                    result.setTotal(rs.getBigDecimal("total"));
                    result.setStatus(rs.getString("status"));
                    return result;
                }
            }
        }

        return null;
    }

    public void update(Order order) throws SQLException {
        String sql = "{CALL sp_order_update(?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, order.getIdOrder());
            fillCreateUpdateStatement(statement, order, 2);
            statement.execute();
        }
    }

    private void fillCreateUpdateStatement(CallableStatement statement, Order order) throws SQLException {
        fillCreateUpdateStatement(statement, order, 1);
    }

    private void fillCreateUpdateStatement(CallableStatement statement, Order order, int startIndex) throws SQLException {
        int index = startIndex;
        statement.setInt(index++, order.getIdUser());
        setNullableTimestamp(statement, index++, order.getExpectedDate());
        setNullableInt(statement, index++, order.getCustomerDocumentTypeId());
        statement.setString(index++, order.getCustomerDocumentNumber());
        statement.setString(index++, order.getNotes());
        statement.setString(index, buildDetailsJson(order.getDetails()));
    }

    public int count(String search, String status, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        String sql = "{CALL sp_order_count(?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, status);
            setNullableDate(statement, 3, dateFrom);
            setNullableDate(statement, 4, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }

        return 0;
    }

    public List<Order> list(String search, String status, LocalDate dateFrom, LocalDate dateTo, int page, int limit)
            throws SQLException {
        String sql = "{CALL sp_order_list(?, ?, ?, ?, ?, ?)}";
        List<Order> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, status);
            setNullableDate(statement, 3, dateFrom);
            setNullableDate(statement, 4, dateTo);
            statement.setInt(5, page);
            statement.setInt(6, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrder(rs));
                }
            }
        }

        return list;
    }

    public OrderStats getStats(String search, String status, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        String sql = "{CALL sp_order_stats(?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, status);
            setNullableDate(statement, 3, dateFrom);
            setNullableDate(statement, 4, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    OrderStats stats = new OrderStats();
                    stats.setPendingCount(rs.getInt("pending_count"));
                    stats.setConvertedCount(rs.getInt("converted_count"));
                    stats.setCancelledCount(rs.getInt("cancelled_count"));
                    stats.setPendingAmount(rs.getBigDecimal("pending_amount"));
                    return stats;
                }
            }
        }

        return new OrderStats();
    }

    public Order findById(int idOrder) throws SQLException {
        String sql = "{CALL sp_order_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idOrder);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrder(rs);
                    order.setDetails(listDetails(idOrder));
                    return order;
                }
            }
        }

        return null;
    }

    public List<OrderDetail> listDetails(int idOrder) throws SQLException {
        String sql = "{CALL sp_order_detail_list(?)}";
        List<OrderDetail> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idOrder);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setIdOrderDetail(rs.getInt("id_order_detail"));
                    detail.setIdOrder(rs.getInt("id_order"));
                    detail.setIdProduct(rs.getInt("id_product"));
                    detail.setProductName(rs.getString("product_name"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setOriginalUnitPrice(rs.getBigDecimal("original_unit_price"));
                    detail.setDiscountType(rs.getString("discount_type"));
                    detail.setDiscountValue(rs.getBigDecimal("discount_value"));
                    detail.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                    detail.setIgvAmount(rs.getBigDecimal("igv_amount"));
                    detail.setUnitPrice(rs.getBigDecimal("unit_price"));
                    detail.setSubtotalBeforeDiscount(rs.getBigDecimal("subtotal_before_discount"));
                    detail.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(detail);
                }
            }
        }

        return list;
    }

    public void cancel(int idOrder) throws SQLException {
        String sql = "{CALL sp_order_cancel(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idOrder);
            statement.execute();
        }
    }

    public Sale convertToSale(int idOrder, int idUser, int idPaymentMethod, String documentKind, BigDecimal paidAmount)
            throws SQLException {
        String sql = "{CALL sp_order_convert_to_sale(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idOrder);
            statement.setInt(2, idUser);
            statement.setInt(3, idPaymentMethod);
            statement.setString(4, documentKind);
            statement.setBigDecimal(5, paidAmount);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Sale sale = new Sale();
                    sale.setIdSale(rs.getInt("id_sale"));
                    sale.setVoucherCode(rs.getString("voucher_code"));
                    sale.setTotal(rs.getBigDecimal("total"));
                    sale.setChangeAmount(rs.getBigDecimal("change_amount"));
                    return sale;
                }
            }
        }

        return null;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setIdOrder(rs.getInt("id_order"));
        order.setOrderSeries(rs.getString("order_series"));
        order.setOrderNumber(rs.getInt("order_number"));
        order.setOrderCode(rs.getString("order_code"));
        order.setIdUser(rs.getInt("id_user"));
        order.setUserName(rs.getString("user_name"));
        order.setIdClient(getNullableInt(rs, "id_client"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerDocumentTypeId(getNullableInt(rs, "customer_document_type_id"));
        order.setCustomerDocumentTypeName(rs.getString("customer_document_type_name"));
        order.setCustomerDocumentNumber(rs.getString("customer_document_number"));
        order.setNotes(rs.getString("notes"));
        order.setSubtotal(getMoney(rs.getBigDecimal("subtotal")));
        order.setDiscountAmount(getMoney(rs.getBigDecimal("discount_amount")));
        order.setIgvAmount(getMoney(rs.getBigDecimal("igv_amount")));
        order.setTotal(getMoney(rs.getBigDecimal("total")));
        order.setStatus(rs.getString("status"));
        order.setLinkedSaleId(getNullableInt(rs, "linked_sale_id"));
        order.setLinkedVoucherCode(rs.getString("linked_voucher_code"));

        Timestamp orderDate = rs.getTimestamp("order_date");
        if (orderDate != null) {
            order.setOrderDate(orderDate.toLocalDateTime());
        }

        Timestamp expectedDate = rs.getTimestamp("expected_date");
        if (expectedDate != null) {
            order.setExpectedDate(expectedDate.toLocalDateTime());
        }

        Timestamp convertedAt = rs.getTimestamp("converted_at");
        if (convertedAt != null) {
            order.setConvertedAt(convertedAt.toLocalDateTime());
        }

        Timestamp cancelledAt = rs.getTimestamp("cancelled_at");
        if (cancelledAt != null) {
            order.setCancelledAt(cancelledAt.toLocalDateTime());
        }

        return order;
    }

    private BigDecimal getMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private void setNullableInt(CallableStatement statement, int index, Integer value) throws SQLException {
        if (value == null || value <= 0) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private void setNullableDate(CallableStatement statement, int index, LocalDate value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(value));
        }
    }

    private void setNullableTimestamp(CallableStatement statement, int index, java.time.LocalDateTime value)
            throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    private String buildDetailsJson(List<OrderDetail> details) {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{");
            json.append("\"id_product\":").append(detail.getIdProduct()).append(",");
            json.append("\"quantity\":").append(detail.getQuantity()).append(",");
            json.append("\"discount_type\":\"").append(escapeJson(detail.getDiscountType())).append("\",");
            json.append("\"discount_value\":").append(detail.getDiscountValue() == null ? "0" : detail.getDiscountValue());
            json.append("}");
        }

        json.append("]");
        return json.toString();
    }

    private String escapeJson(String text) {
        if (text == null) {
            return "NONE";
        }

        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
