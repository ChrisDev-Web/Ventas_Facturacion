package Controllers;

import DAO.AlertDAO;
import Models.StockAlert;
import Models.StockAlertSummary;
import java.sql.SQLException;
import java.util.List;

public class AlertController {

    private final AlertDAO alertDAO;

    public AlertController() {
        this.alertDAO = new AlertDAO();
    }

    public StockAlertSummary getSummary() throws Exception {
        try {
            return alertDAO.getSummary();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int count(String search, String severity) throws Exception {
        try {
            return alertDAO.count(normalizeSearch(search), normalizeSeverity(severity));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<StockAlert> list(String search, String severity, int page, int limit) throws Exception {
        try {
            return alertDAO.list(
                    normalizeSearch(search),
                    normalizeSeverity(severity),
                    normalizePage(page),
                    normalizeLimit(limit)
            );
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<StockAlert> listActiveAlerts(int limit) throws Exception {
        int safeLimit = limit <= 0 ? 20 : Math.min(limit, 50);

        try {
            return alertDAO.listActiveAlerts(safeLimit);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private String normalizeSearch(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }

        return search.trim();
    }

    private String normalizeSeverity(String severity) {
        if (severity == null || severity.trim().isEmpty()) {
            return "TODOS";
        }

        return severity.trim().toUpperCase();
    }

    private int normalizePage(int page) {
        return page < 1 ? 1 : page;
    }

    private int normalizeLimit(int limit) {
        if (limit == 20 || limit == 50) {
            return limit;
        }

        return 10;
    }

    private String getSqlMessage(SQLException e) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            return e.getMessage();
        }

        return "Ocurrio un error al comunicarse con la base de datos.";
    }
}
