package DAO;

import Config.Database;
import Models.Product;
import Models.SelectOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public void create(Product product) throws SQLException {
        String sql = "{CALL sp_product_create(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getCost());
            statement.setBigDecimal(4, product.getProfitMargin());
            statement.setBigDecimal(5, product.getPrice());
            statement.setInt(6, product.getStock());
            statement.setString(7, product.getImage());
            statement.setInt(8, product.getIdCategory());
            statement.setInt(9, product.getIdBrand());
            statement.execute();
        }
    }

    public void update(Product product) throws SQLException {
        String sql = "{CALL sp_product_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, product.getIdProduct());
            statement.setString(2, product.getName());
            statement.setString(3, product.getDescription());
            statement.setBigDecimal(4, product.getCost());
            statement.setBigDecimal(5, product.getProfitMargin());
            statement.setBigDecimal(6, product.getPrice());
            statement.setInt(7, product.getStock());
            statement.setString(8, product.getImage());
            statement.setInt(9, product.getIdCategory());
            statement.setInt(10, product.getIdBrand());
            statement.execute();
        }
    }

    public Product findById(int idProduct) throws SQLException {
        String sql = "{CALL sp_product_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idProduct);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }
            }
        }

        return null;
    }

    public List<Product> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_product_list_active(?, ?, ?)}";
        List<Product> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapProduct(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_product_count_active(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public List<Product> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_product_list_inactive(?, ?, ?)}";
        List<Product> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapProduct(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_product_count_inactive(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public void deleteLogical(int idProduct) throws SQLException {
        String sql = "{CALL sp_product_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idProduct);
            statement.execute();
        }
    }

    public void restore(int idProduct) throws SQLException {
        String sql = "{CALL sp_product_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idProduct);
            statement.execute();
        }
    }

    public void deletePhysical(int idProduct) throws SQLException {
        String sql = "{CALL sp_product_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idProduct);
            statement.execute();
        }
    }

    public List<SelectOption> listCategoryOptions() throws SQLException {
        return listOptions("{CALL sp_product_category_options()}");
    }

    public List<SelectOption> listBrandOptions() throws SQLException {
        return listOptions("{CALL sp_product_brand_options()}");
    }

    private List<SelectOption> listOptions(String sql) throws SQLException {
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                list.add(new SelectOption(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                ));
            }
        }

        return list;
    }

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();

        product.setIdProduct(resultSet.getInt("id_product"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setCost(resultSet.getBigDecimal("cost"));
        product.setProfitMargin(resultSet.getBigDecimal("profit_margin"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setStock(resultSet.getInt("stock"));
        product.setImage(resultSet.getString("image"));

        product.setIdCategory(resultSet.getInt("id_category"));
        product.setCategoryName(resultSet.getString("category_name"));

        product.setIdBrand(resultSet.getInt("id_brand"));
        product.setBrandName(resultSet.getString("brand_name"));

        product.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            product.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return product;
    }
}