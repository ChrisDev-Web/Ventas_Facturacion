package DAO;

import Config.Database;
import Models.LocationItem;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO {

    public void create(String entity, Integer parentId, String name, String description) throws SQLException {
        String sql = "{CALL sp_location_create(?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);

            if (parentId == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, parentId);
            }

            statement.setString(3, name);
            statement.setString(4, description);
            statement.execute();
        }
    }

    public void update(String entity, int id, Integer parentId, String name, String description) throws SQLException {
        String sql = "{CALL sp_location_update(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setInt(2, id);

            if (parentId == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, parentId);
            }

            statement.setString(4, name);
            statement.setString(5, description);
            statement.execute();
        }
    }

    public LocationItem findById(String entity, int id) throws SQLException {
        String sql = "{CALL sp_location_find_by_id(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setInt(2, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapLocationItem(resultSet);
                }
            }
        }

        return null;
    }

    public List<LocationItem> listActive(String entity, String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_location_list_active(?, ?, ?, ?)}";
        List<LocationItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setString(2, search);
            statement.setInt(3, page);
            statement.setInt(4, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapLocationItem(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String entity, String search) throws SQLException {
        String sql = "{CALL sp_location_count_active(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setString(2, search);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public List<LocationItem> listInactive(String entity, String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_location_list_inactive(?, ?, ?, ?)}";
        List<LocationItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setString(2, search);
            statement.setInt(3, page);
            statement.setInt(4, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapLocationItem(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String entity, String search) throws SQLException {
        String sql = "{CALL sp_location_count_inactive(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setString(2, search);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public void deleteLogical(String entity, int id) throws SQLException {
        String sql = "{CALL sp_location_delete_logical(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setInt(2, id);
            statement.execute();
        }
    }

    public void restore(String entity, int id) throws SQLException {
        String sql = "{CALL sp_location_restore(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setInt(2, id);
            statement.execute();
        }
    }

    public void deletePhysical(String entity, int id) throws SQLException {
        String sql = "{CALL sp_location_delete_physical(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);
            statement.setInt(2, id);
            statement.execute();
        }
    }

    public List<LocationItem> listParentOptions(String entity) throws SQLException {
        String sql = "{CALL sp_location_parent_options(?)}";
        List<LocationItem> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, entity);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocationItem item = new LocationItem();
                    item.setId(resultSet.getInt("id"));
                    item.setName(resultSet.getString("name"));
                    list.add(item);
                }
            }
        }

        return list;
    }

    private LocationItem mapLocationItem(ResultSet resultSet) throws SQLException {
        LocationItem item = new LocationItem();

        item.setId(resultSet.getInt("id"));

        int parentId = resultSet.getInt("parent_id");

        if (resultSet.wasNull()) {
            item.setParentId(null);
        } else {
            item.setParentId(parentId);
        }

        item.setParentName(resultSet.getString("parent_name"));
        item.setName(resultSet.getString("name"));
        item.setDescription(resultSet.getString("description"));
        item.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            item.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            item.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            item.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return item;
    }
}