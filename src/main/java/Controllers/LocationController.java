package Controllers;

import DAO.LocationDAO;
import Models.LocationItem;
import java.sql.SQLException;
import java.util.List;

public class LocationController {

    private final LocationDAO locationDAO;

    public LocationController() {
        this.locationDAO = new LocationDAO();
    }

    public void create(String entity, Integer parentId, String name, String description) throws Exception {
        validateEntity(entity);
        validateParent(entity, parentId);
        validateName(name);

        try {
            locationDAO.create(entity, parentId, name.trim(), normalizeText(description));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(String entity, int id, Integer parentId, String name, String description) throws Exception {
        validateEntity(entity);

        if (id <= 0) {
            throw new Exception("Seleccione un registro válido.");
        }

        validateParent(entity, parentId);
        validateName(name);

        try {
            locationDAO.update(entity, id, parentId, name.trim(), normalizeText(description));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public LocationItem findById(String entity, int id) throws Exception {
        validateEntity(entity);

        if (id <= 0) {
            throw new Exception("Seleccione un registro válido.");
        }

        try {
            LocationItem item = locationDAO.findById(entity, id);

            if (item == null) {
                throw new Exception("No se encontró el registro.");
            }

            return item;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<LocationItem> listActive(String entity, String search, int page, int limit) throws Exception {
        validateEntity(entity);

        try {
            return locationDAO.listActive(entity, normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String entity, String search) throws Exception {
        validateEntity(entity);

        try {
            return locationDAO.countActive(entity, normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<LocationItem> listInactive(String entity, String search, int page, int limit) throws Exception {
        validateEntity(entity);

        try {
            return locationDAO.listInactive(entity, normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String entity, String search) throws Exception {
        validateEntity(entity);

        try {
            return locationDAO.countInactive(entity, normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(String entity, int id) throws Exception {
        validateEntity(entity);

        if (id <= 0) {
            throw new Exception("Seleccione un registro válido.");
        }

        try {
            locationDAO.deleteLogical(entity, id);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(String entity, int id) throws Exception {
        validateEntity(entity);

        if (id <= 0) {
            throw new Exception("Seleccione un registro válido.");
        }

        try {
            locationDAO.restore(entity, id);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(String entity, int id) throws Exception {
        validateEntity(entity);

        if (id <= 0) {
            throw new Exception("Seleccione un registro válido.");
        }

        try {
            locationDAO.deletePhysical(entity, id);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<LocationItem> listParentOptions(String entity) throws Exception {
        validateEntity(entity);

        try {
            return locationDAO.listParentOptions(entity);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateEntity(String entity) throws Exception {
        if (!"COUNTRY".equals(entity)
                && !"REGION".equals(entity)
                && !"PROVINCE".equals(entity)
                && !"DISTRICT".equals(entity)) {
            throw new Exception("Entidad de ubicación no válida.");
        }
    }

    private void validateParent(String entity, Integer parentId) throws Exception {
        if (!"COUNTRY".equals(entity) && (parentId == null || parentId <= 0)) {
            throw new Exception("Seleccione una ubicación padre válida.");
        }
    }

    private void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Ingrese el nombre.");
        }

        if (name.trim().length() > 100) {
            throw new Exception("El nombre no debe superar los 100 caracteres.");
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