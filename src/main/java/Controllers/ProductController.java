package Controllers;

import DAO.ProductDAO;
import Models.Product;
import Models.SelectOption;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class ProductController {

    private final ProductDAO productDAO;

    public ProductController() {
        this.productDAO = new ProductDAO();
    }

    public void create(Product product) throws Exception {
        validateProduct(product);

        try {
            normalizeProduct(product);
            productDAO.create(product);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(Product product) throws Exception {
        if (product.getIdProduct() <= 0) {
            throw new Exception("Seleccione un producto válido.");
        }

        validateProduct(product);

        try {
            normalizeProduct(product);
            productDAO.update(product);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Product findById(int idProduct) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto válido.");
        }

        try {
            Product product = productDAO.findById(idProduct);

            if (product == null) {
                throw new Exception("No se encontró el producto.");
            }

            return product;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Product> listActive(String search, int page, int limit) throws Exception {
        try {
            return productDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return productDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Product> listInactive(String search, int page, int limit) throws Exception {
        try {
            return productDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return productDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idProduct) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto válido.");
        }

        try {
            productDAO.deleteLogical(idProduct);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idProduct) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto válido.");
        }

        try {
            productDAO.restore(idProduct);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idProduct) throws Exception {
        if (idProduct <= 0) {
            throw new Exception("Seleccione un producto válido.");
        }

        try {
            productDAO.deletePhysical(idProduct);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listCategoryOptions() throws Exception {
        try {
            return productDAO.listCategoryOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listBrandOptions() throws Exception {
        try {
            return productDAO.listBrandOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateProduct(Product product) throws Exception {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new Exception("Ingrese el nombre del producto.");
        }

        if (product.getName().trim().length() > 150) {
            throw new Exception("El nombre no debe superar los 150 caracteres.");
        }

        if (product.getIdCategory() <= 0) {
            throw new Exception("Seleccione una categoría.");
        }

        if (product.getIdBrand() <= 0) {
            throw new Exception("Seleccione una marca.");
        }

        if (product.getCost() == null || product.getCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El costo no puede ser negativo.");
        }

        if (product.getProfitMargin() == null || product.getProfitMargin().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El margen de ganancia no puede ser negativo.");
        }

        if (product.getProfitMargin().compareTo(BigDecimal.valueOf(100)) >= 0) {
            throw new Exception("El margen de ganancia debe ser menor a 100%.");
        }

        if (product.getStock() < 0) {
            throw new Exception("El stock no puede ser negativo.");
        }
    }

    private void normalizeProduct(Product product) {
        product.setName(product.getName().trim());
        product.setDescription(normalizeText(product.getDescription()));
        product.setImage(normalizeText(product.getImage()));

        product.setCost(scaleMoney(product.getCost()));
        product.setProfitMargin(scaleMoney(product.getProfitMargin()));

        BigDecimal marginDecimal = product.getProfitMargin()
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        BigDecimal divisor = BigDecimal.ONE.subtract(marginDecimal);

        BigDecimal price = product.getCost()
                .divide(divisor, 2, RoundingMode.HALF_UP);

        product.setPrice(price);
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
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