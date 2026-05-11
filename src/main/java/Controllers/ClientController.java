package Controllers;

import DAO.ClientDAO;
import Models.Client;
import Models.ReniecPerson;
import Models.SelectOption;
import Services.DecolectaService;
import java.sql.SQLException;
import java.util.List;

public class ClientController {

    private final ClientDAO clientDAO;
    private final DecolectaService decolectaService;

    public ClientController() {
        this.clientDAO = new ClientDAO();
        this.decolectaService = new DecolectaService();
    }

    public void create(Client client) throws Exception {
        validateClient(client);

        try {
            normalizeClient(client);
            clientDAO.create(client);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(Client client) throws Exception {
        if (client.getIdClient() <= 0) {
            throw new Exception("Seleccione un cliente vÃ¡lido.");
        }

        validateClient(client);

        try {
            normalizeClient(client);
            clientDAO.update(client);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Client findById(int idClient) throws Exception {
        if (idClient <= 0) {
            throw new Exception("Seleccione un cliente vÃ¡lido.");
        }

        try {
            Client client = clientDAO.findById(idClient);

            if (client == null) {
                throw new Exception("No se encontrÃ³ el cliente.");
            }

            return client;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Client> listActive(String search, int page, int limit) throws Exception {
        try {
            return clientDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return clientDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<Client> listInactive(String search, int page, int limit) throws Exception {
        try {
            return clientDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return clientDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idClient) throws Exception {
        if (idClient <= 0) {
            throw new Exception("Seleccione un cliente vÃ¡lido.");
        }

        try {
            clientDAO.deleteLogical(idClient);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idClient) throws Exception {
        if (idClient <= 0) {
            throw new Exception("Seleccione un cliente vÃ¡lido.");
        }

        try {
            clientDAO.restore(idClient);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idClient) throws Exception {
        if (idClient <= 0) {
            throw new Exception("Seleccione un cliente vÃ¡lido.");
        }

        try {
            clientDAO.deletePhysical(idClient);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDocumentTypeOptions() throws Exception {
        try {
            return clientDAO.listDocumentTypeOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listRegionOptions() throws Exception {
        try {
            return clientDAO.listRegionOptions();
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listProvinceOptions(int idRegion) throws Exception {
        if (idRegion <= 0) {
            return java.util.Collections.emptyList();
        }

        try {
            return clientDAO.listProvinceOptions(idRegion);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<SelectOption> listDistrictOptions(int idProvince) throws Exception {
        if (idProvince <= 0) {
            return java.util.Collections.emptyList();
        }

        try {
            return clientDAO.listDistrictOptions(idProvince);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public Client searchByDni(String dni) throws Exception {
        ReniecPerson person = decolectaService.findByDni(dni);

        Client client = new Client();
        client.setName(person.getFirstName());
        client.setLastNamePaternal(person.getFirstLastName());
        client.setLastNameMaternal(person.getSecondLastName());
        client.setDocumentNumber(person.getDocumentNumber());
        return client;
    }

    private void validateClient(Client client) throws Exception {
        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new Exception("Ingrese el nombre del cliente.");
        }

        if (client.getLastNamePaternal() == null || client.getLastNamePaternal().trim().isEmpty()) {
            throw new Exception("Ingrese el apellido paterno.");
        }

        if (client.getLastNameMaternal() == null || client.getLastNameMaternal().trim().isEmpty()) {
            throw new Exception("Ingrese el apellido materno.");
        }

        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            String email = client.getEmail().trim();

            if (!email.contains("@") || !email.contains(".")) {
                throw new Exception("Ingrese un correo vÃ¡lido.");
            }

            if (email.length() > 150) {
                throw new Exception("El correo no debe superar los 150 caracteres.");
            }
        }

        if (client.getIdProvinces() != null && client.getIdProvinces() > 0
                && (client.getIdRegions() == null || client.getIdRegions() <= 0)) {
            throw new Exception("Seleccione una regiÃ³n antes de seleccionar provincia.");
        }

        if (client.getIdDistrict() != null && client.getIdDistrict() > 0
                && (client.getIdProvinces() == null || client.getIdProvinces() <= 0)) {
            throw new Exception("Seleccione una provincia antes de seleccionar distrito.");
        }
    }

    private void normalizeClient(Client client) {
        client.setName(normalizeRequired(client.getName()));
        client.setLastNamePaternal(normalizeRequired(client.getLastNamePaternal()));
        client.setLastNameMaternal(normalizeRequired(client.getLastNameMaternal()));

        client.setDocumentNumber(normalizeText(client.getDocumentNumber()));
        client.setPhone(normalizeText(client.getPhone()));
        client.setEmail(normalizeText(client.getEmail()));
        client.setAddress(normalizeText(client.getAddress()));

        client.setIdDocumentType(normalizeInteger(client.getIdDocumentType()));
        client.setIdRegions(normalizeInteger(client.getIdRegions()));
        client.setIdProvinces(normalizeInteger(client.getIdProvinces()));
        client.setIdDistrict(normalizeInteger(client.getIdDistrict()));
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

        return "OcurriÃ³ un error al comunicarse con la base de datos.";
    }
}
