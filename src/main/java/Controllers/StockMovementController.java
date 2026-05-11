package Controllers;

import DAO.StockMovementDAO;
import Models.SelectOption;
import Models.StockMovement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StockMovementController {

    private final StockMovementDAO stockMovementDAO;

    public StockMovementController() {
        this.stockMovementDAO = new StockMovementDAO();
    }

    public void create(int idProduct, String movementType, int quantity, String description) throws Exception {
        validateMovement(idProduct, movementType, quantity);

        try {
            StockMovement movement = new StockMovement();
            movement.setIdProduct(idProduct);
            movement.setMovementType(movementType.trim().toUpperCase());
            movement.setQuantity(quantity);
            movement.setDescription(normalizeText(description));
            movement.setReference("MANUAL");
            movement.setReferenceId(null);

            stockMovementDAO.create(movement);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<StockMovement> list(
            String search,
            int idProduct,
            String movementType,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int limit
    ) throws Exception {
        try {
            return stockMovementDAO.list(
                    normalizeSearch(search),
                    idProduct,
                    normalizeMovementType(movementType),
                    dateFrom,
                    dateTo,
                    normalizePage(page),
                    normalizeLimit(limit)
            );
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int count(
            String search,
            int idProduct,
            String movementType,
            LocalDate dateFrom,
            LocalDate dateTo
    ) throws Exception {
        try {
            return stockMovementDAO.count(
                    normalizeSearch(search),
                    idProduct,
                    normalizeMovementType(movementType),
                    dateFrom,
                    dateTo
            );
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public StockMovement findById(int idStockMovement) throws Exception {
        if (idStockMovement <= 0) {
            throw new Exception("Seleccione un movimiento válido.");
        }

        try {
            StockMovement movement = stockMovementDAO.findById(idStockMovement);

            if (movement == null) {
                throw new Exception("No se encontró el movimiento.");
            }

            return movement;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listProductOptions() throws Exception {
        try {
            return stockMovementDAO.listProductOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateMovement(int idProduct, String movementType, int quantity) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto.");
        }

        if (movementType == null || movementType.trim().isEmpty()) {
            throw new Exception("Seleccione el tipo de movimiento.");
        }

        String type = movementType.trim().toUpperCase();

        if (!type.equals("ENTRADA") && !type.equals("SALIDA") && !type.equals("AJUSTE")) {
            throw new Exception("El tipo debe ser ENTRADA, SALIDA o AJUSTE.");
        }

        if (quantity < 0) {
            throw new Exception("La cantidad no puede ser negativa.");
        }

        if ((type.equals("ENTRADA") || type.equals("SALIDA")) && quantity <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero.");
        }
    }

    private String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        return text.trim();
    }

    private String normalizeSearch(String search) {
        if (search == null || search.trim().isEmpty()) {
            return "";
        }

        return search.trim();
    }

    private String normalizeMovementType(String movementType) {
        if (movementType == null || movementType.trim().isEmpty()) {
            return "TODOS";
        }

        return movementType.trim().toUpperCase();
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