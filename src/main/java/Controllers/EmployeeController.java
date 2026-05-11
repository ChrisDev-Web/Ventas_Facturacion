package Controllers;

import DAO.EmployeeDAO;
import Models.Employee;
import Models.ReniecPerson;
import Models.SelectOption;
import Services.DecolectaService;
import java.sql.SQLException;
import java.util.List;

public class EmployeeController {

    private final EmployeeDAO employeeDAO;
    private final DecolectaService decolectaService;

    public EmployeeController() {
        this.employeeDAO = new EmployeeDAO();
        this.decolectaService = new DecolectaService();
    }

    public void create(Employee employee) throws Exception {
        validateEmployee(employee);

        try {
            normalizeEmployee(employee);
            employeeDAO.create(employee);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(Employee employee) throws Exception {
        if (employee.getIdEmployee() <= 0) {
            throw new Exception("Seleccione un empleado válido.");
        }

        validateEmployee(employee);

        try {
            normalizeEmployee(employee);
            employeeDAO.update(employee);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Employee findById(int idEmployee) throws Exception {
        if (idEmployee <= 0) {
            throw new Exception("Seleccione un empleado válido.");
        }

        try {
            Employee employee = employeeDAO.findById(idEmployee);

            if (employee == null) {
                throw new Exception("No se encontró el empleado.");
            }

            return employee;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Employee> listActive(String search, int page, int limit) throws Exception {
        try {
            return employeeDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return employeeDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Employee> listInactive(String search, int page, int limit) throws Exception {
        try {
            return employeeDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return employeeDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idEmployee) throws Exception {
        if (idEmployee <= 0) {
            throw new Exception("Seleccione un empleado válido.");
        }

        try {
            employeeDAO.deleteLogical(idEmployee);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idEmployee) throws Exception {
        if (idEmployee <= 0) {
            throw new Exception("Seleccione un empleado válido.");
        }

        try {
            employeeDAO.restore(idEmployee);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idEmployee) throws Exception {
        if (idEmployee <= 0) {
            throw new Exception("Seleccione un empleado válido.");
        }

        try {
            employeeDAO.deletePhysical(idEmployee);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws Exception {
        try {
            return employeeDAO.listDocumentTypeOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listRoleOptions() throws Exception {
        try {
            return employeeDAO.listRoleOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listRegionOptions() throws Exception {
        try {
            return employeeDAO.listRegionOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listProvinceOptions(int idRegion) throws Exception {
        if (idRegion <= 0) {
            return java.util.Collections.emptyList();
        }

        try {
            return employeeDAO.listProvinceOptions(idRegion);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDistrictOptions(int idProvince) throws Exception {
        if (idProvince <= 0) {
            return java.util.Collections.emptyList();
        }

        try {
            return employeeDAO.listDistrictOptions(idProvince);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Employee searchByDni(String dni) throws Exception {
        ReniecPerson person = decolectaService.findByDni(dni);

        Employee employee = new Employee();
        employee.setName(person.getFirstName());
        employee.setLastNamePaternal(person.getFirstLastName());
        employee.setLastNameMaternal(person.getSecondLastName());
        employee.setDocumentNumber(person.getDocumentNumber());
        return employee;
    }

    private void validateEmployee(Employee employee) throws Exception {
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new Exception("Ingrese el nombre del empleado.");
        }

        if (employee.getLastNamePaternal() == null || employee.getLastNamePaternal().trim().isEmpty()) {
            throw new Exception("Ingrese el apellido paterno.");
        }

        if (employee.getLastNameMaternal() == null || employee.getLastNameMaternal().trim().isEmpty()) {
            throw new Exception("Ingrese el apellido materno.");
        }

        if (employee.getIdRol() <= 0) {
            throw new Exception("Seleccione un rol.");
        }

        if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty()) {
            String email = employee.getEmail().trim();

            if (!email.contains("@") || !email.contains(".")) {
                throw new Exception("Ingrese un correo válido.");
            }

            if (email.length() > 150) {
                throw new Exception("El correo no debe superar los 150 caracteres.");
            }
        }

        if (employee.getIdProvinces() != null && employee.getIdProvinces() > 0
                && (employee.getIdRegions() == null || employee.getIdRegions() <= 0)) {
            throw new Exception("Seleccione una región antes de seleccionar provincia.");
        }

        if (employee.getIdDistrict() != null && employee.getIdDistrict() > 0
                && (employee.getIdProvinces() == null || employee.getIdProvinces() <= 0)) {
            throw new Exception("Seleccione una provincia antes de seleccionar distrito.");
        }
    }

    private void normalizeEmployee(Employee employee) {
        employee.setName(normalizeRequired(employee.getName()));
        employee.setLastNamePaternal(normalizeRequired(employee.getLastNamePaternal()));
        employee.setLastNameMaternal(normalizeRequired(employee.getLastNameMaternal()));

        employee.setDocumentNumber(normalizeText(employee.getDocumentNumber()));
        employee.setPhone(normalizeText(employee.getPhone()));
        employee.setEmail(normalizeText(employee.getEmail()));
        employee.setAddress(normalizeText(employee.getAddress()));

        employee.setIdDocumentType(normalizeInteger(employee.getIdDocumentType()));
        employee.setIdRegions(normalizeInteger(employee.getIdRegions()));
        employee.setIdProvinces(normalizeInteger(employee.getIdProvinces()));
        employee.setIdDistrict(normalizeInteger(employee.getIdDistrict()));
    }

    private String normalizeRequired(String text) {
        return text == null ? "" : text.trim();
    }

    private String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        return text.trim();
    }

    private Integer normalizeInteger(Integer value) {
        if (value == null || value <= 0) {
            return null;
        }

        return value;
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
