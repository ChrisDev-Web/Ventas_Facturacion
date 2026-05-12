package DAO;

import Config.Database;
import Models.InventoryMetrics;
import Models.InventoryProduct;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public InventoryMetrics getMetrics() throws SQLException {
        String sql = ""
                + "SELECT "
                + "COUNT(*) AS active_products, "
                + "COALESCE(SUM(stock), 0) AS total_units, "
                + "COALESCE(SUM(cost * stock), 0) AS total_cost_value, "
                + "COALESCE(SUM(price * stock), 0) AS total_sale_value, "
                + "COALESCE(SUM(CASE WHEN stock = 0 THEN 1 ELSE 0 END), 0) AS out_of_stock_products, "
                + "COALESCE(SUM(CASE WHEN stock > 0 AND stock <= 5 THEN 1 ELSE 0 END), 0) AS low_stock_products "
                + "FROM products "
                + "WHERE status = 1 AND deleted_at IS NULL";

        try (
            Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                InventoryMetrics metrics = new InventoryMetrics();
                metrics.setActiveProducts(resultSet.getInt("active_products"));
                metrics.setTotalUnits(resultSet.getInt("total_units"));
                metrics.setTotalCostValue(resultSet.getBigDecimal("total_cost_value"));
                metrics.setTotalSaleValue(resultSet.getBigDecimal("total_sale_value"));
                metrics.setOutOfStockProducts(resultSet.getInt("out_of_stock_products"));
                metrics.setLowStockProducts(resultSet.getInt("low_stock_products"));
                return metrics;
            }
        }

        return new InventoryMetrics();
    }

    public List<InventoryProduct> list(String search, String stockFilter, int page, int limit) throws SQLException {
        String sql = "{CALL sp_inventory_list(?, ?, ?, ?)}";
        List<InventoryProduct> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, stockFilter);
            statement.setInt(3, page);
            statement.setInt(4, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapProduct(resultSet));
                }
            }
        }

        return list;
    }

    public int count(String search, String stockFilter) throws SQLException {
        String sql = "{CALL sp_inventory_count(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setString(2, stockFilter);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public InventoryProduct findById(int idProduct) throws SQLException {
        String sql = "{CALL sp_inventory_find_by_id(?)}";

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

    private InventoryProduct mapProduct(ResultSet resultSet) throws SQLException {
        InventoryProduct product = new InventoryProduct();

        product.setIdProduct(resultSet.getInt("id_product"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setCost(resultSet.getBigDecimal("cost"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setStock(resultSet.getInt("stock"));

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
