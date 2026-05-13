package DAO;

import Config.Database;
import Models.DashboardAlertItem;
import Models.DashboardChartItem;
import Models.DashboardLatestSale;
import Models.DashboardSummary;
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

public class DashboardDAO {

    public DashboardSummary getSummary(LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        String sql = "{CALL sp_dashboard_summary(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            setNullableDate(statement, 1, dateFrom);
            setNullableDate(statement, 2, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    DashboardSummary summary = new DashboardSummary();
                    summary.setTotalSales(rs.getBigDecimal("total_sales"));
                    summary.setTotalSalesGrowth(rs.getBigDecimal("total_sales_growth"));
                    summary.setDaySales(rs.getBigDecimal("day_sales"));
                    summary.setDaySalesGrowth(rs.getBigDecimal("day_sales_growth"));
                    summary.setOrders(rs.getInt("orders"));
                    summary.setOrdersGrowth(rs.getBigDecimal("orders_growth"));
                    summary.setNewClients(rs.getInt("new_clients"));
                    summary.setNewClientsGrowth(rs.getBigDecimal("new_clients_growth"));
                    summary.setProductsSold(rs.getInt("products_sold"));
                    summary.setProductsSoldGrowth(rs.getBigDecimal("products_sold_growth"));
                    summary.setAverageTicket(rs.getBigDecimal("average_ticket"));
                    summary.setAverageTicketGrowth(rs.getBigDecimal("average_ticket_growth"));
                    summary.setLowStockProducts(rs.getInt("low_stock_products"));
                    summary.setOutOfStockProducts(rs.getInt("out_of_stock_products"));
                    return summary;
                }
            }
        }

        return new DashboardSummary();
    }

    public List<DashboardChartItem> listSalesByDay(LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        return listChartItems("{CALL sp_dashboard_sales_by_day(?, ?)}", dateFrom, dateTo);
    }

    public List<DashboardChartItem> listSalesByCategory(LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        return listChartItems("{CALL sp_dashboard_sales_by_category(?, ?)}", dateFrom, dateTo);
    }

    public List<DashboardChartItem> listTopProducts(LocalDate dateFrom, LocalDate dateTo, int limit) throws SQLException {
        String sql = "{CALL sp_dashboard_top_products(?, ?, ?)}";
        List<DashboardChartItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            setNullableDate(statement, 1, dateFrom);
            setNullableDate(statement, 2, dateTo);
            statement.setInt(3, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapChartItem(rs));
                }
            }
        }

        return list;
    }

    public List<DashboardChartItem> listSalesByHour(LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        return listChartItems("{CALL sp_dashboard_sales_by_hour(?, ?)}", dateFrom, dateTo);
    }

    public List<DashboardChartItem> listSalesComparison(LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        return listChartItems("{CALL sp_dashboard_sales_comparison(?, ?)}", dateFrom, dateTo);
    }

    public List<DashboardChartItem> listPaymentMethods(LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        return listChartItems("{CALL sp_dashboard_payment_methods(?, ?)}", dateFrom, dateTo);
    }

    public List<DashboardLatestSale> listLatestSales(int limit) throws SQLException {
        String sql = "{CALL sp_dashboard_latest_sales(?)}";
        List<DashboardLatestSale> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    DashboardLatestSale sale = new DashboardLatestSale();
                    Timestamp saleDate = rs.getTimestamp("sale_date");

                    if (saleDate != null) {
                        sale.setSaleDate(saleDate.toLocalDateTime());
                    }

                    sale.setVoucherCode(rs.getString("voucher_code"));
                    sale.setCustomerName(rs.getString("customer_name"));
                    sale.setTotal(rs.getBigDecimal("total"));
                    sale.setPaymentMethodName(rs.getString("payment_method_name"));
                    list.add(sale);
                }
            }
        }

        return list;
    }

    public List<DashboardAlertItem> listAlerts(int limit) throws SQLException {
        String sql = "{CALL sp_dashboard_alerts(?)}";
        List<DashboardAlertItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, limit);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    DashboardAlertItem item = new DashboardAlertItem();
                    Timestamp alertTime = rs.getTimestamp("alert_time");

                    item.setSeverity(rs.getString("severity"));
                    item.setMessage(rs.getString("message"));

                    if (alertTime != null) {
                        item.setAlertTime(alertTime.toLocalDateTime());
                    }

                    list.add(item);
                }
            }
        }

        return list;
    }

    private List<DashboardChartItem> listChartItems(String sql, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
        List<DashboardChartItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            setNullableDate(statement, 1, dateFrom);
            setNullableDate(statement, 2, dateTo);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapChartItem(rs));
                }
            }
        }

        return list;
    }

    private DashboardChartItem mapChartItem(ResultSet rs) throws SQLException {
        DashboardChartItem item = new DashboardChartItem();
        item.setLabel(rs.getString("label"));
        item.setAmount(rs.getBigDecimal("amount"));

        try {
            item.setComparisonAmount(rs.getBigDecimal("comparison_amount"));
        } catch (SQLException e) {
            item.setComparisonAmount(java.math.BigDecimal.ZERO);
        }

        try {
            item.setQuantity(rs.getInt("quantity"));
        } catch (SQLException e) {
            item.setQuantity(0);
        }

        return item;
    }

    private void setNullableDate(CallableStatement statement, int index, LocalDate date) throws SQLException {
        if (date == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(date));
        }
    }
}
