package Controllers;

import DAO.PaymentMethodDAO;
import Models.PaymentMethod;
import java.sql.SQLException;
import java.util.List;

public class PaymentMethodController {

    private final PaymentMethodDAO paymentMethodDAO;

    public PaymentMethodController() {
        this.paymentMethodDAO = new PaymentMethodDAO();
    }

    public void create(String name, String description) throws Exception {
        validateName(name);

        try {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(name.trim());
            paymentMethod.setDescription(normalizeText(description));

            paymentMethodDAO.create(paymentMethod);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(int idPaymentMethod, String name, String description) throws Exception {
        if (idPaymentMethod <= 0) {
            throw new Exception("Seleccione un método de pago válido.");
        }

        validateName(name);

        try {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setIdPaymentMethod(idPaymentMethod);
            paymentMethod.setName(name.trim());
            paymentMethod.setDescription(normalizeText(description));

            paymentMethodDAO.update(paymentMethod);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public PaymentMethod findById(int idPaymentMethod) throws Exception {
        if (idPaymentMethod <= 0) {
            throw new Exception("Seleccione un método de pago válido.");
        }

        try {
            PaymentMethod paymentMethod = paymentMethodDAO.findById(idPaymentMethod);

            if (paymentMethod == null) {
                throw new Exception("No se encontró el método de pago.");
            }

            return paymentMethod;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<PaymentMethod> listActive(String search, int page, int limit) throws Exception {
        try {
            return paymentMethodDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return paymentMethodDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<PaymentMethod> listInactive(String search, int page, int limit) throws Exception {
        try {
            return paymentMethodDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return paymentMethodDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idPaymentMethod) throws Exception {
        if (idPaymentMethod <= 0) {
            throw new Exception("Seleccione un método de pago válido.");
        }

        try {
            paymentMethodDAO.deleteLogical(idPaymentMethod);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idPaymentMethod) throws Exception {
        if (idPaymentMethod <= 0) {
            throw new Exception("Seleccione un método de pago válido.");
        }

        try {
            paymentMethodDAO.restore(idPaymentMethod);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idPaymentMethod) throws Exception {
        if (idPaymentMethod <= 0) {
            throw new Exception("Seleccione un método de pago válido.");
        }

        try {
            paymentMethodDAO.deletePhysical(idPaymentMethod);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Ingrese el nombre del método de pago.");
        }

        if (name.trim().length() > 50) {
            throw new Exception("El nombre no debe superar los 50 caracteres.");
        }
    }

    private String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        return text.trim();
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