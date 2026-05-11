package DAO;

import Config.Database;
import Models.Role;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public void create(Role role) throws SQLException {
        String sql = "{CALL sp_role_create(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, role.getName());
            statement.setString(2, role.getDescription());
            statement.execute();
        }
    }

    public void update(Role role) throws SQLException {
        String sql = "{CALL sp_role_update(?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, role.getIdRol());
            statement.setString(2, role.getName());
            statement.setString(3, role.getDescription());
            statement.execute();
        }
    }

    public Role findById(int idRol) throws SQLException {
        String sql = "{CALL sp_role_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idRol);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRole(resultSet);
                }
            }
        }

        return null;
    }

    public List<Role> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_role_list_active(?, ?, ?)}";
        List<Role> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapRole(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_role_count_active(?)}";

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

    public List<Role> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_role_list_inactive(?, ?, ?)}";
        List<Role> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapRole(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_role_count_inactive(?)}";

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

    public void deleteLogical(int idRol) throws SQLException {
        String sql = "{CALL sp_role_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idRol);
            statement.execute();
        }
    }

    public void restore(int idRol) throws SQLException {
        String sql = "{CALL sp_role_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idRol);
            statement.execute();
        }
    }

    public void deletePhysical(int idRol) throws SQLException {
        String sql = "{CALL sp_role_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idRol);
            statement.execute();
        }
    }

    private Role mapRole(ResultSet resultSet) throws SQLException {
        Role role = new Role();

        role.setIdRol(resultSet.getInt("id_rol"));
        role.setName(resultSet.getString("name"));
        role.setDescription(resultSet.getString("description"));
        role.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            role.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            role.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            role.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return role;
    }
}