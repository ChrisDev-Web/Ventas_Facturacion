package DAO;

import Config.Database;
import Models.Sale;
import Models.SaleDetail;
import Models.SaleHistoryStats;
import Models.SaleProductItem;
import Models.SaleRanking;
import Models.SelectOption;
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

public class SaleDAO {

    public List<SaleProductItem> listProducts(String search, int idCategory, int idBrand) throws SQLException {
        String sql = "{CALL sp_sale_product_catalog(?, ?, ?)}";
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
        return listOptions("{CALL sp_sale_category_options()}");
    }

    public List<SelectOption> listBrandOptions() throws SQLException {
        return listOptions("{CALL sp_sale_brand_options()}");
    }

    public List<SelectOption> listPaymentMethodOptions() throws SQLException {
        return listOptions("{CALL sp_sale_payment_method_options()}");
    }

    public List<SelectOption> listDocumentTypeOptions() throws SQLException {
        return listOptions("{CALL sp_sale_document_type_options()}");
    }

    public List<SelectOption> listUserOptions() throws SQLException {
        return listOptions("{CALL sp_sale_user_options()}");
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

    public Sale createSale(Sale sale) throws SQLException {
        String sql = "{CALL sp_sale_create(?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, sale.getIdUser());
            statement.setInt(2, sale.getIdPaymentMethod());
            statement.setString(3, sale.getDocumentKind());

            if (sale.getCustomerDocumentTypeId() == null || sale.getCustomerDocumentTypeId() <= 0) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, sale.getCustomerDocumentTypeId());
            }

            statement.setString(5, sale.getCustomerDocumentNumber());
            statement.setDouble(6, sale.getPaidAmount().doubleValue());
            statement.setString(7, buildDetailsJson(sale.getDetails()));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Sale result = new Sale();
                    result.setIdSale(rs.getInt("id_sale"));
                    result.setVoucherCode(rs.getString("voucher_code"));
                    result.setTotal(rs.getBigDecimal("total"));
                    result.setChangeAmount(rs.getBigDecimal("change_amount"));
                    return result;
                }
            }
        }

        return null;
    }

    private String buildDetailsJson(List<SaleDetail> details) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < details.size(); i++) {
            SaleDetail d = details.get(i);

            if (i > 0) {
                json.append(",");
            }

            json.append("{");
            json.append("\"id_product\":").append(d.getIdProduct()).append(",");
            json.append("\"quantity\":").append(d.getQuantity()).append(",");
            json.append("\"discount_type\":\"").append(escapeJson(d.getDiscountType())).append("\",");
            json.append("\"discount_value\":").append(d.getDiscountValue() == null ? "0" : d.getDiscountValue());
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

    public List<Sale> listHistory(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo, int page, int limit) throws SQLException {
        String sql = "{CALL sp_sale_history_list(?, ?, ?, ?, ?, ?, ?)}";
        List<Sale> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillHistoryFilters(statement, search, idPaymentMethod, idUser, dateFrom, dateTo);
            statement.setInt(6, page);
            statement.setInt(7, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHistorySale(rs));
                }
            }
        }

        return list;
    }

    public int countHistory(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        String sql = "{CALL sp_sale_history_count(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillHistoryFilters(statement, search, idPaymentMethod, idUser, dateFrom, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }

        return 0;
    }

    public SaleHistoryStats getStats(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        String sql = "{CALL sp_sale_history_stats(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillHistoryFilters(statement, search, idPaymentMethod, idUser, dateFrom, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    SaleHistoryStats stats = new SaleHistoryStats();
                    stats.setTotalSales(rs.getBigDecimal("total_sales"));
                    stats.setAverageTicket(rs.getBigDecimal("average_ticket"));
                    stats.setTotalReturns(rs.getBigDecimal("total_returns"));
                    stats.setDiscountSalesCount(rs.getInt("discount_sales_count"));
                    return stats;
                }
            }
        }

        return new SaleHistoryStats();
    }

    public List<SaleRanking> getRanking(String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        String sql = "{CALL sp_sale_ranking(?, ?, ?, ?, ?)}";
        List<SaleRanking> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, idPaymentMethod);
            statement.setInt(3, idUser);
            setNullableDate(statement, 4, dateFrom);
            setNullableDate(statement, 5, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    SaleRanking ranking = new SaleRanking();
                    ranking.setIdUser(rs.getInt("id_user"));
                    ranking.setUserName(rs.getString("user_name"));
                    ranking.setTotalSales(rs.getBigDecimal("total_sales"));
                    list.add(ranking);
                }
            }
        }

        return list;
    }

    public Sale findById(int idSale) throws SQLException {
        String sql = "{CALL sp_sale_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idSale);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Sale sale = mapFullSale(rs);
                    sale.setDetails(listDetails(idSale));
                    return sale;
                }
            }
        }

        return null;
    }

    public List<SaleDetail> listDetails(int idSale) throws SQLException {
        String sql = "{CALL sp_sale_detail_list(?)}";
        List<SaleDetail> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idSale);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    SaleDetail detail = new SaleDetail();
                    detail.setIdSaleDetail(rs.getInt("id_sale_detail"));
                    detail.setIdSale(rs.getInt("id_sale"));
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

    private void fillHistoryFilters(CallableStatement statement, String search, int idPaymentMethod, int idUser, LocalDate dateFrom, LocalDate dateTo)
            throws SQLException {
        statement.setString(1, search);
        statement.setInt(2, idPaymentMethod);
        statement.setInt(3, idUser);
        setNullableDate(statement, 4, dateFrom);
        setNullableDate(statement, 5, dateTo);
    }

    private void setNullableDate(CallableStatement statement, int index, LocalDate date) throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(date));
        }
    }

    private Sale mapHistorySale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setIdSale(rs.getInt("id_sale"));
        sale.setVoucherCode(rs.getString("voucher_code"));
        sale.setDocumentKind(rs.getString("document_kind"));
        sale.setDocumentLabel(rs.getString("document_label"));
        sale.setIdClient(getNullableInt(rs, "id_client"));
        sale.setCustomerName(rs.getString("customer_name"));

        Timestamp saleDate = rs.getTimestamp("sale_date");
        if (saleDate != null) {
            sale.setSaleDate(saleDate.toLocalDateTime());
        }

        sale.setTotal(rs.getBigDecimal("total"));
        sale.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        sale.setIgvAmount(rs.getBigDecimal("igv_amount"));
        sale.setPaidAmount(rs.getBigDecimal("paid_amount"));
        sale.setChangeAmount(rs.getBigDecimal("change_amount"));
        sale.setStatus(rs.getInt("status"));
        sale.setUserName(rs.getString("user_name"));
        sale.setPaymentMethodName(rs.getString("payment_method_name"));
        return sale;
    }

    private Sale mapFullSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();

        sale.setIdSale(rs.getInt("id_sale"));
        sale.setIdUser(rs.getInt("id_user"));
        sale.setUserName(rs.getString("user_name"));

        sale.setIdPaymentMethod(rs.getInt("id_payment_method"));
        sale.setPaymentMethodName(rs.getString("payment_method_name"));

        Timestamp saleDate = rs.getTimestamp("sale_date");
        if (saleDate != null) {
            sale.setSaleDate(saleDate.toLocalDateTime());
        }

        sale.setDocumentKind(rs.getString("document_kind"));
        sale.setDocumentLabel(rs.getString("document_label"));
        sale.setVoucherSeries(rs.getString("voucher_series"));
        sale.setVoucherNumber(rs.getInt("voucher_number"));
        sale.setVoucherCode(rs.getString("voucher_code"));

        sale.setIdClient(getNullableInt(rs, "id_client"));
        sale.setCustomerName(rs.getString("customer_name"));
        sale.setCustomerDocumentTypeId(getNullableInt(rs, "customer_document_type_id"));
        sale.setCustomerDocumentTypeName(rs.getString("customer_document_type_name"));
        sale.setCustomerDocumentNumber(rs.getString("customer_document_number"));

        sale.setSubtotal(rs.getBigDecimal("subtotal"));
        sale.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        sale.setIgvAmount(rs.getBigDecimal("igv_amount"));
        sale.setTotal(rs.getBigDecimal("total"));
        sale.setPaidAmount(rs.getBigDecimal("paid_amount"));
        sale.setChangeAmount(rs.getBigDecimal("change_amount"));
        sale.setStatus(rs.getInt("status"));

        return sale;
    }

    private Integer getNullableInt(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }
}
