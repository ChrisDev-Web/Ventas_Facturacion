package DAO;

import Config.Database;
import Models.Brand;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {

    public void create(Brand brand) throws SQLException {
        String sql = "{CALL sp_brand_create(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, brand.getName());
            statement.execute();
        }
    }

    public void update(Brand brand) throws SQLException {
        String sql = "{CALL sp_brand_update(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, brand.getIdBrand());
            statement.setString(2, brand.getName());
            statement.execute();
        }
    }

    public Brand findById(int idBrand) throws SQLException {
        String sql = "{CALL sp_brand_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBrand);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapBrand(resultSet);
                }
            }
        }

        return null;
    }

    public List<Brand> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_brand_list_active(?, ?, ?)}";
        List<Brand> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapBrand(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_brand_count_active(?)}";

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

    public List<Brand> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_brand_list_inactive(?, ?, ?)}";
        List<Brand> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapBrand(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_brand_count_inactive(?)}";

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

    public void deleteLogical(int idBrand) throws SQLException {
        String sql = "{CALL sp_brand_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBrand);
            statement.execute();
        }
    }

    public void restore(int idBrand) throws SQLException {
        String sql = "{CALL sp_brand_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBrand);
            statement.execute();
        }
    }

    public void deletePhysical(int idBrand) throws SQLException {
        String sql = "{CALL sp_brand_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idBrand);
            statement.execute();
        }
    }

    private Brand mapBrand(ResultSet resultSet) throws SQLException {
        Brand brand = new Brand();

        brand.setIdBrand(resultSet.getInt("id_brand"));
        brand.setName(resultSet.getString("name"));
        brand.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            brand.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            brand.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            brand.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return brand;
    }
}