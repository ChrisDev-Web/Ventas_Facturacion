package DAO;

import Config.Database;
import Models.StockAlert;
import Models.StockAlertSummary;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    public StockAlertSummary getSummary() throws SQLException {
        String sql = "{CALL sp_alerts_low_stock_summary()}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                StockAlertSummary summary = new StockAlertSummary();
                summary.setWarningCount(resultSet.getInt("warning_count"));
                summary.setCriticalCount(resultSet.getInt("critical_count"));
                summary.setUrgentCount(resultSet.getInt("urgent_count"));
                summary.setTotalAlerts(resultSet.getInt("total_alerts"));
                return summary;
            }
        }

        return new StockAlertSummary();
    }

    public int count(String search, String severity) throws SQLException {
        String sql = "{CALL sp_alerts_low_stock_count(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, severity);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public List<StockAlert> list(String search, String severity, int page, int limit) throws SQLException {
        String sql = "{CALL sp_alerts_low_stock_list(?, ?, ?, ?)}";
        List<StockAlert> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, severity);
            statement.setInt(3, page);
            statement.setInt(4, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapAlert(resultSet));
                }
            }
        }

        return list;
    }

    public List<StockAlert> listActiveAlerts(int limit) throws SQLException {
        return list("", "TODOS", 1, limit);
    }

    private StockAlert mapAlert(ResultSet resultSet) throws SQLException {
        StockAlert alert = new StockAlert();
        Timestamp alertTime = resultSet.getTimestamp("alert_time");

        alert.setIdProduct(resultSet.getInt("id_product"));
        alert.setProductName(resultSet.getString("product_name"));
        alert.setCategoryName(resultSet.getString("category_name"));
        alert.setBrandName(resultSet.getString("brand_name"));
        alert.setStock(resultSet.getInt("stock"));
        alert.setSeverity(resultSet.getString("severity"));
        alert.setSeverityLabel(resultSet.getString("severity_label"));
        alert.setRecommendedAction(resultSet.getString("recommended_action"));

        if (alertTime != null) {
            alert.setAlertTime(alertTime.toLocalDateTime());
        }

        return alert;
    }
}
