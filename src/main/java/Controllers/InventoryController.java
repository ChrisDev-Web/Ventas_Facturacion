package Controllers;

import DAO.InventoryDAO;
import Models.InventoryProduct;
import java.sql.SQLException;
import java.util.List;

public class InventoryController {

    private final InventoryDAO inventoryDAO;

    public InventoryController() {
        this.inventoryDAO = new InventoryDAO();
    }

    public List<InventoryProduct> list(String search, String stockFilter, int page, int limit) throws Exception {
        try {
            return inventoryDAO.list(
                    normalizeSearch(search),
                    normalizeStockFilter(stockFilter),
                    normalizePage(page),
                    normalizeLimit(limit)
            );
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int count(String search, String stockFilter) throws Exception {
        try {
            return inventoryDAO.count(
                    normalizeSearch(search),
                    normalizeStockFilter(stockFilter)
            );
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public InventoryProduct findById(int idProduct) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto válido.");
        }

        try {
            InventoryProduct product = inventoryDAO.findById(idProduct);

            if (product == null) {
                throw new Exception("No se encontró el producto.");
            }

            return product;

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

    private String normalizeStockFilter(String filter) {
        if (filter == null || filter.trim().isEmpty()) {
            return "TODOS";
        }

        return filter.trim().toUpperCase();
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

        return "Ocurrió un error al comunicarse con la base de datos.";
    }
}