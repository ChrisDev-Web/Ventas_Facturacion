package DAO;

import Config.Database;
import Models.PaymentMethod;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDAO {

    public void create(PaymentMethod paymentMethod) throws SQLException {
        String sql = "{CALL sp_payment_method_create(?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, paymentMethod.getName());
            statement.setString(2, paymentMethod.getDescription());
            statement.execute();
        }
    }

    public void update(PaymentMethod paymentMethod) throws SQLException {
        String sql = "{CALL sp_payment_method_update(?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, paymentMethod.getIdPaymentMethod());
            statement.setString(2, paymentMethod.getName());
            statement.setString(3, paymentMethod.getDescription());
            statement.execute();
        }
    }

    public PaymentMethod findById(int idPaymentMethod) throws SQLException {
        String sql = "{CALL sp_payment_method_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idPaymentMethod);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPaymentMethod(resultSet);
                }
            }
        }

        return null;
    }

    public List<PaymentMethod> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_payment_method_list_active(?, ?, ?)}";
        List<PaymentMethod> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapPaymentMethod(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_payment_method_count_active(?)}";

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

    public List<PaymentMethod> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_payment_method_list_inactive(?, ?, ?)}";
        List<PaymentMethod> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapPaymentMethod(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_payment_method_count_inactive(?)}";

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

    public void deleteLogical(int idPaymentMethod) throws SQLException {
        String sql = "{CALL sp_payment_method_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idPaymentMethod);
            statement.execute();
        }
    }

    public void restore(int idPaymentMethod) throws SQLException {
        String sql = "{CALL sp_payment_method_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idPaymentMethod);
            statement.execute();
        }
    }

    public void deletePhysical(int idPaymentMethod) throws SQLException {
        String sql = "{CALL sp_payment_method_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idPaymentMethod);
            statement.execute();
        }
    }

    private PaymentMethod mapPaymentMethod(ResultSet resultSet) throws SQLException {
        PaymentMethod paymentMethod = new PaymentMethod();

        paymentMethod.setIdPaymentMethod(resultSet.getInt("id_payment_method"));
        paymentMethod.setName(resultSet.getString("name"));
        paymentMethod.setDescription(resultSet.getString("description"));
        paymentMethod.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            paymentMethod.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            paymentMethod.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            paymentMethod.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return paymentMethod;
    }
}