package Controllers;

import DAO.RoleDAO;
import Models.Role;
import java.sql.SQLException;
import java.util.List;

public class RoleController {

    private final RoleDAO roleDAO;

    public RoleController() {
        this.roleDAO = new RoleDAO();
    }

    public void create(String name, String description) throws Exception {
        validateName(name);

        try {
            Role role = new Role();
            role.setName(name.trim());
            role.setDescription(normalizeText(description));

            roleDAO.create(role);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(int idRol, String name, String description) throws Exception {
        if (idRol <= 0) {
            throw new Exception("Seleccione un rol válido.");
        }

        validateName(name);

        try {
            Role role = new Role();
            role.setIdRol(idRol);
            role.setName(name.trim());
            role.setDescription(normalizeText(description));

            roleDAO.update(role);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Role findById(int idRol) throws Exception {
        if (idRol <= 0) {
            throw new Exception("Seleccione un rol válido.");
        }

        try {
            Role role = roleDAO.findById(idRol);

            if (role == null) {
                throw new Exception("No se encontró el rol.");
            }

            return role;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Role> listActive(String search, int page, int limit) throws Exception {
        try {
            return roleDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return roleDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Role> listInactive(String search, int page, int limit) throws Exception {
        try {
            return roleDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return roleDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idRol) throws Exception {
        if (idRol <= 0) {
            throw new Exception("Seleccione un rol válido.");
        }

        try {
            roleDAO.deleteLogical(idRol);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idRol) throws Exception {
        if (idRol <= 0) {
            throw new Exception("Seleccione un rol válido.");
        }

        try {
            roleDAO.restore(idRol);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idRol) throws Exception {
        if (idRol <= 0) {
            throw new Exception("Seleccione un rol válido.");
        }

        try {
            roleDAO.deletePhysical(idRol);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Ingrese el nombre del rol.");
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