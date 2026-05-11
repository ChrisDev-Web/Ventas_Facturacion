package DAO;

import Config.Database;
import Models.Employee;
import Models.SelectOption;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public void create(Employee employee) throws SQLException {
        String sql = "{CALL sp_employee_create(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            fillCreateUpdateStatement(statement, employee, false);
            statement.execute();
        }
    }

    public void update(Employee employee) throws SQLException {
        String sql = "{CALL sp_employee_update(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, employee.getIdEmployee());
            fillCreateUpdateStatement(statement, employee, true);
            statement.execute();
        }
    }

    private void fillCreateUpdateStatement(CallableStatement statement, Employee employee, boolean update)
            throws SQLException {

        int index = update ? 2 : 1;

        statement.setString(index++, employee.getName());
        statement.setString(index++, employee.getLastNamePaternal());
        statement.setString(index++, employee.getLastNameMaternal());

        setNullableInt(statement, index++, employee.getIdDocumentType());
        statement.setString(index++, employee.getDocumentNumber());
        statement.setString(index++, employee.getPhone());
        statement.setString(index++, employee.getEmail());
        statement.setString(index++, employee.getAddress());

        setNullableInt(statement, index++, employee.getIdRegions());
        setNullableInt(statement, index++, employee.getIdProvinces());
        setNullableInt(statement, index++, employee.getIdDistrict());

        statement.setInt(index, employee.getIdRol());
    }

    public Employee findById(int idEmployee) throws SQLException {
        String sql = "{CALL sp_employee_find_by_id(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idEmployee);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }
            }
        }

        return null;
    }

    public List<Employee> listActive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_employee_list_active(?, ?, ?)}";
        List<Employee> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapEmployee(resultSet));
                }
            }
        }

        return list;
    }

    public int countActive(String search) throws SQLException {
        String sql = "{CALL sp_employee_count_active(?)}";

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

    public List<Employee> listInactive(String search, int page, int limit) throws SQLException {
        String sql = "{CALL sp_employee_list_inactive(?, ?, ?)}";
        List<Employee> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setString(1, search);
            statement.setInt(2, page);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapEmployee(resultSet));
                }
            }
        }

        return list;
    }

    public int countInactive(String search) throws SQLException {
        String sql = "{CALL sp_employee_count_inactive(?)}";

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

    public void deleteLogical(int idEmployee) throws SQLException {
        String sql = "{CALL sp_employee_delete_logical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idEmployee);
            statement.execute();
        }
    }

    public void restore(int idEmployee) throws SQLException {
        String sql = "{CALL sp_employee_restore(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idEmployee);
            statement.execute();
        }
    }

    public void deletePhysical(int idEmployee) throws SQLException {
        String sql = "{CALL sp_employee_delete_physical(?)}";

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idEmployee);
            statement.execute();
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws SQLException {
        return listOptions("{CALL sp_employee_document_type_options()}");
    }

    public List<SelectOption> listRoleOptions() throws SQLException {
        return listOptions("{CALL sp_employee_role_options()}");
    }

    public List<SelectOption> listRegionOptions() throws SQLException {
        return listOptions("{CALL sp_employee_region_options()}");
    }

    public List<SelectOption> listProvinceOptions(int idRegion) throws SQLException {
        String sql = "{CALL sp_employee_province_options(?)}";
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idRegion);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapOption(resultSet));
                }
            }
        }

        return list;
    }

    public List<SelectOption> listDistrictOptions(int idProvince) throws SQLException {
        String sql = "{CALL sp_employee_district_options(?)}";
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql)
        ) {
            statement.setInt(1, idProvince);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapOption(resultSet));
                }
            }
        }

        return list;
    }

    private List<SelectOption> listOptions(String sql) throws SQLException {
        List<SelectOption> list = new ArrayList<>();

        try (
            Connection connection = Database.getConnection();
            CallableStatement statement = connection.prepareCall(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                list.add(mapOption(resultSet));
            }
        }

        return list;
    }

    private SelectOption mapOption(ResultSet resultSet) throws SQLException {
        return new SelectOption(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();

        employee.setIdEmployee(resultSet.getInt("id_employee"));
        employee.setName(resultSet.getString("name"));
        employee.setLastNamePaternal(resultSet.getString("last_name_paternal"));
        employee.setLastNameMaternal(resultSet.getString("last_name_maternal"));

        employee.setIdDocumentType(getNullableInt(resultSet, "id_document_type"));
        employee.setDocumentTypeName(resultSet.getString("document_type_name"));
        employee.setDocumentNumber(resultSet.getString("document_number"));

        employee.setPhone(resultSet.getString("phone"));
        employee.setEmail(resultSet.getString("email"));
        employee.setAddress(resultSet.getString("address"));

        employee.setIdRegions(getNullableInt(resultSet, "id_regions"));
        employee.setRegionName(resultSet.getString("region_name"));

        employee.setIdProvinces(getNullableInt(resultSet, "id_provinces"));
        employee.setProvinceName(resultSet.getString("province_name"));

        employee.setIdDistrict(getNullableInt(resultSet, "id_district"));
        employee.setDistrictName(resultSet.getString("district_name"));

        employee.setIdRol(resultSet.getInt("id_rol"));
        employee.setRoleName(resultSet.getString("role_name"));

        employee.setStatus(resultSet.getInt("status"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        Timestamp deletedAt = resultSet.getTimestamp("deleted_at");

        if (createdAt != null) {
            employee.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            employee.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        if (deletedAt != null) {
            employee.setDeletedAt(deletedAt.toLocalDateTime());
        }

        return employee;
    }

    private Integer getNullableInt(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);

        if (resultSet.wasNull()) {
            return null;
        }

        return value;
    }

    private void setNullableInt(CallableStatement statement, int index, Integer value) throws SQLException {
        if (value == null || value <= 0) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}