package DAO;

import Config.Database;
import Models.SelectOption;
import Models.StockMovement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockMovementDAO {

    public void create(StockMovement movement) throws SQLException {
        String sql = "{CALL sp_stock_movement_create(?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, movement.getIdProduct());
            statement.setString(2, movement.getMovementType());
            statement.setInt(3, movement.getQuantity());
            statement.setString(4, movement.getDescription());
            statement.setString(5, movement.getReference());

            if (movement.getReferenceId() == null || movement.getReferenceId() <= 0) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, movement.getReferenceId());
            }

            statement.execute();
        }
    }

    public List<StockMovement> list(
            String search,
            int idProduct,
            String movementType,
            LocalDate dateFrom,
            LocalDate dateTo,
            int page,
            int limit
    ) throws SQLException {
        String sql = "{CALL sp_stock_movement_list(?, ?, ?, ?, ?, ?, ?)}";
        List<StockMovement> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillFilters(statement, search, idProduct, movementType, dateFrom, dateTo);
            statement.setInt(6, page);
            statement.setInt(7, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapMovement(resultSet));
                }
            }
        }

        return list;
    }

    public int count(
            String search,
            int idProduct,
            String movementType,
            LocalDate dateFrom,
            LocalDate dateTo
    ) throws SQLException {
        String sql = "{CALL sp_stock_movement_count(?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillFilters(statement, search, idProduct, movementType, dateFrom, dateTo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    public StockMovement findById(int idStockMovement) throws SQLException {
        String sql = "{CALL sp_stock_movement_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idStockMovement);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapMovement(resultSet);
                }
            }
        }

        return null;
    }

    public List<SelectOption> listProductOptions() throws SQLException {
        String sql = "{CALL sp_stock_movement_product_options()}";
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

    private void fillFilters(
            CallableStatement statement,
            String search,
            int idProduct,
            String movementType,
            LocalDate dateFrom,
            LocalDate dateTo
    ) throws SQLException {
        statement.setString(1, search);
        statement.setInt(2, idProduct);
        statement.setString(3, movementType);

        if (dateFrom == null) {
            statement.setNull(4, Types.DATE);
        } else {
            statement.setDate(4, Date.valueOf(dateFrom));
        }

        if (dateTo == null) {
            statement.setNull(5, Types.DATE);
        } else {
            statement.setDate(5, Date.valueOf(dateTo));
        }
    }

    private StockMovement mapMovement(ResultSet resultSet) throws SQLException {
        StockMovement movement = new StockMovement();

        movement.setIdStockMovement(resultSet.getInt("id_stock_movement"));
        movement.setIdProduct(resultSet.getInt("id_product"));
        movement.setProductName(resultSet.getString("product_name"));
        movement.setCategoryName(resultSet.getString("category_name"));
        movement.setBrandName(resultSet.getString("brand_name"));
        movement.setMovementType(resultSet.getString("movement_type"));
        movement.setQuantity(resultSet.getInt("quantity"));
        movement.setDescription(resultSet.getString("description"));
        movement.setReference(resultSet.getString("reference"));

        int referenceId = resultSet.getInt("reference_id");
        movement.setReferenceId(resultSet.wasNull() ? null : referenceId);

        Timestamp movementDate = resultSet.getTimestamp("movement_date");

        if (movementDate != null) {
            movement.setMovementDate(movementDate.toLocalDateTime());
        }

        return movement;
    }
}