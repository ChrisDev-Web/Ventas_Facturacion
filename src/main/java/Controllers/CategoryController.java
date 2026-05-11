package Controllers;

import DAO.CategoryDAO;
import Models.Category;
import java.sql.SQLException;
import java.util.List;

public class CategoryController {

    private final CategoryDAO categoryDAO;

    public CategoryController() {
        this.categoryDAO = new CategoryDAO();
    }

    public void create(String name, String description) throws Exception {
        validateName(name);

        try {
            Category category = new Category();
            category.setName(name.trim());
            category.setDescription(normalizeText(description));

            categoryDAO.create(category);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(int idCategory, String name, String description) throws Exception {
        if (idCategory <= 0) {
            throw new Exception("Seleccione una categoría válida.");
        }

        validateName(name);

        try {
            Category category = new Category();
            category.setIdCategory(idCategory);
            category.setName(name.trim());
            category.setDescription(normalizeText(description));

            categoryDAO.update(category);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Category findById(int idCategory) throws Exception {
        if (idCategory <= 0) {
            throw new Exception("Seleccione una categoría válida.");
        }

        try {
            Category category = categoryDAO.findById(idCategory);

            if (category == null) {
                throw new Exception("No se encontró la categoría.");
            }

            return category;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Category> listActive(String search, int page, int limit) throws Exception {
        try {
            return categoryDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return categoryDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Category> listInactive(String search, int page, int limit) throws Exception {
        try {
            return categoryDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return categoryDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idCategory) throws Exception {
        if (idCategory <= 0) {
            throw new Exception("Seleccione una categoría válida.");
        }

        try {
            categoryDAO.deleteLogical(idCategory);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idCategory) throws Exception {
        if (idCategory <= 0) {
            throw new Exception("Seleccione una categoría válida.");
        }

        try {
            categoryDAO.restore(idCategory);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idCategory) throws Exception {
        if (idCategory <= 0) {
            throw new Exception("Seleccione una categoría válida.");
        }

        try {
            categoryDAO.deletePhysical(idCategory);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Ingrese el nombre de la categoría.");
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