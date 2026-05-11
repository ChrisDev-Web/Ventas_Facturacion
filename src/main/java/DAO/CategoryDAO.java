package DAO;

import Config.Database;
import Models.Category;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public void create(Category category) throws SQLException {
        String sql = "{CALL sp_category_create(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.execute();
        }
    }

    public void update(Category category) throws SQLException {
        String sql = "{CALL sp_category_update(?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, category.getIdCategory());
            statement.setString(2, category.getName());
            statement.setString(3, category.getDescription());
            statement.execute();
        }
    }

    public Category findById(int idCategory) throws SQLException {
        String sql = "{CALL sp_category_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idCategory);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCategory(resultSet);
                }
            }
        }

        return null;
    }

    public List<Category> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_category_list_active(?, ?, ?)}";
        List<Category> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapCategory(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_category_count_active(?)}";

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

    public List<Category> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_category_list_inactive(?, ?, ?)}";
        List<Category> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapCategory(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_category_count_inactive(?)}";

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

    public void deleteLogical(int idCategory) throws SQLException {
        String sql = "{CALL sp_category_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idCategory);
            statement.execute();
        }
    }

    public void restore(int idCategory) throws SQLException {
        String sql = "{CALL sp_category_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idCategory);
            statement.execute();
        }
    }

    public void deletePhysical(int idCategory) throws SQLException {
        String sql = "{CALL sp_category_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idCategory);
            statement.execute();
        }
    }

    private Category mapCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();

        category.setIdCategory(resultSet.getInt("id_category"));
        category.setName(resultSet.getString("name"));
        category.setDescription(resultSet.getString("description"));
        category.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            category.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            category.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            category.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return category;
    }
}