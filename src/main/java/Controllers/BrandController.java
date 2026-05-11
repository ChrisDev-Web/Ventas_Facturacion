package Controllers;

import DAO.BrandDAO;
import Models.Brand;
import java.sql.SQLException;
import java.util.List;

public class BrandController {

    private final BrandDAO brandDAO;

    public BrandController() {
        this.brandDAO = new BrandDAO();
    }

    public void create(String name) throws Exception {
        validateName(name);

        try {
            Brand brand = new Brand();
            brand.setName(name.trim());

            brandDAO.create(brand);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(int idBrand, String name) throws Exception {
        if (idBrand <= 0) {
            throw new Exception("Seleccione una marca válida.");
        }

        validateName(name);

        try {
            Brand brand = new Brand();
            brand.setIdBrand(idBrand);
            brand.setName(name.trim());

            brandDAO.update(brand);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Brand findById(int idBrand) throws Exception {
        if (idBrand <= 0) {
            throw new Exception("Seleccione una marca válida.");
        }

        try {
            Brand brand = brandDAO.findById(idBrand);

            if (brand == null) {
                throw new Exception("No se encontró la marca.");
            }

            return brand;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Brand> listActive(String search, int page, int limit) throws Exception {
        try {
            return brandDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return brandDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Brand> listInactive(String search, int page, int limit) throws Exception {
        try {
            return brandDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return brandDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idBrand) throws Exception {
        if (idBrand <= 0) {
            throw new Exception("Seleccione una marca válida.");
        }

        try {
            brandDAO.deleteLogical(idBrand);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idBrand) throws Exception {
        if (idBrand <= 0) {
            throw new Exception("Seleccione una marca válida.");
        }

        try {
            brandDAO.restore(idBrand);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idBrand) throws Exception {
        if (idBrand <= 0) {
            throw new Exception("Seleccione una marca válida.");
        }

        try {
            brandDAO.deletePhysical(idBrand);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Ingrese el nombre de la marca.");
        }

        if (name.trim().length() > 100) {
            throw new Exception("El nombre no debe superar los 100 caracteres.");
        }
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