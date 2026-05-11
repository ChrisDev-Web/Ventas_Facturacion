package Controllers;

import DAO.DocumentTypeDAO;
import Models.DocumentType;
import java.sql.SQLException;
import java.util.List;

public class DocumentTypeController {

    private final DocumentTypeDAO documentTypeDAO;

    public DocumentTypeController() {
        this.documentTypeDAO = new DocumentTypeDAO();
    }

    public void create(String name, String description) throws Exception {
        validateName(name);

        try {
            DocumentType documentType = new DocumentType();
            documentType.setName(name.trim());
            documentType.setDescription(normalizeText(description));

            documentTypeDAO.create(documentType);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void update(int idDocumentType, String name, String description) throws Exception {
        if (idDocumentType <= 0) {
            throw new Exception("Seleccione un tipo de documento válido.");
        }

        validateName(name);

        try {
            DocumentType documentType = new DocumentType();
            documentType.setIdDocumentType(idDocumentType);
            documentType.setName(name.trim());
            documentType.setDescription(normalizeText(description));

            documentTypeDAO.update(documentType);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public DocumentType findById(int idDocumentType) throws Exception {
        if (idDocumentType <= 0) {
            throw new Exception("Seleccione un tipo de documento válido.");
        }

        try {
            DocumentType documentType = documentTypeDAO.findById(idDocumentType);

            if (documentType == null) {
                throw new Exception("No se encontró el tipo de documento.");
            }

            return documentType;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<DocumentType> listActive(String search, int page, int limit) throws Exception {
        try {
            return documentTypeDAO.listActive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countActive(String search) throws Exception {
        try {
            return documentTypeDAO.countActive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public List<DocumentType> listInactive(String search, int page, int limit) throws Exception {
        try {
            return documentTypeDAO.listInactive(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countInactive(String search) throws Exception {
        try {
            return documentTypeDAO.countInactive(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteLogical(int idDocumentType) throws Exception {
        if (idDocumentType <= 0) {
            throw new Exception("Seleccione un tipo de documento válido.");
        }

        try {
            documentTypeDAO.deleteLogical(idDocumentType);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void restore(int idDocumentType) throws Exception {
        if (idDocumentType <= 0) {
            throw new Exception("Seleccione un tipo de documento válido.");
        }

        try {
            documentTypeDAO.restore(idDocumentType);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deletePhysical(int idDocumentType) throws Exception {
        if (idDocumentType <= 0) {
            throw new Exception("Seleccione un tipo de documento válido.");
        }

        try {
            documentTypeDAO.deletePhysical(idDocumentType);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    private void validateName(String name) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Ingrese el nombre del tipo de documento.");
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