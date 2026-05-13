package Controllers;

import DAO.DashboardDAO;
import Models.DashboardAlertItem;
import Models.DashboardChartItem;
import Models.DashboardLatestSale;
import Models.DashboardSummary;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    private final DashboardDAO dashboardDAO;

    public DashboardController() {
        this.dashboardDAO = new DashboardDAO();
    }

    public DashboardSummary getSummary(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        validateRange(dateFrom, dateTo);

        try {
            return dashboardDAO.getSummary(dateFrom, dateTo);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<DashboardChartItem> listSalesByDay(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        return loadChart(() -> dashboardDAO.listSalesByDay(dateFrom, dateTo), dateFrom, dateTo);
    }

    public List<DashboardChartItem> listSalesByCategory(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        return loadChart(() -> dashboardDAO.listSalesByCategory(dateFrom, dateTo), dateFrom, dateTo);
    }

    public List<DashboardChartItem> listTopProducts(LocalDate dateFrom, LocalDate dateTo, int limit) throws Exception {
        validateRange(dateFrom, dateTo);

        try {
            return dashboardDAO.listTopProducts(dateFrom, dateTo, limit <= 0 ? 5 : limit);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<DashboardChartItem> listSalesByHour(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        return loadChart(() -> dashboardDAO.listSalesByHour(dateFrom, dateTo), dateFrom, dateTo);
    }

    public List<DashboardChartItem> listSalesComparison(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        return loadChart(() -> dashboardDAO.listSalesComparison(dateFrom, dateTo), dateFrom, dateTo);
    }

    public List<DashboardChartItem> listPaymentMethods(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        return loadChart(() -> dashboardDAO.listPaymentMethods(dateFrom, dateTo), dateFrom, dateTo);
    }

    public List<DashboardLatestSale> listLatestSales(int limit) throws Exception {
        try {
            return dashboardDAO.listLatestSales(limit <= 0 ? 5 : limit);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<DashboardAlertItem> listAlerts(int limit) throws Exception {
        try {
            return dashboardDAO.listAlerts(limit <= 0 ? 5 : limit);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private List<DashboardChartItem> loadChart(ChartLoader loader, LocalDate dateFrom, LocalDate dateTo) throws Exception {
        validateRange(dateFrom, dateTo);

        try {
            return loader.load();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateRange(LocalDate dateFrom, LocalDate dateTo) throws Exception {
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new Exception("La fecha desde no puede ser mayor que la fecha hasta.");
        }
    }

    private String getSqlMessage(SQLException e) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            return e.getMessage();
        }
        return "Ocurrio un error al cargar los indicadores del dashboard.";
    }

    private interface ChartLoader {

        List<DashboardChartItem> load() throws SQLException;
    }
}
