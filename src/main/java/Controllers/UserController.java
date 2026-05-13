package Controllers;

import Config.PasswordUtil;
import DAO.UserDAO;
import Models.User;
import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    public void registerUser(String userName, char[] password, char[] confirmPassword) throws Exception {
        validateRegister(userName, password, confirmPassword);

        try {
            String hashedPassword = PasswordUtil.hashPassword(password);

            User user = new User();
            user.setUserName(userName.trim());
            user.setPassword(hashedPassword);
            user.setStatus(1);

            userDAO.register(user);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            clearPassword(password);
            clearPassword(confirmPassword);
        }
    }

    public void createUser(User user, char[] password, char[] confirmPassword) throws Exception {
        validateUserForm(user, true);
        validatePasswordPair(password, confirmPassword, true);

        try {
            user.setUserName(user.getUserName().trim());
            user.setFullName(normalizeText(user.getFullName()));
            user.setEmail(normalizeText(user.getEmail()));
            user.setPhone(normalizeText(user.getPhone()));
            user.setProfileImagePath(normalizeText(user.getProfileImagePath()));
            user.setPassword(PasswordUtil.hashPassword(password));
            user.setStatus(normalizeStatus(user.getStatus()));

            userDAO.create(user);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            clearPassword(password);
            clearPassword(confirmPassword);
        }
    }

    public void updateUser(User user, char[] password, char[] confirmPassword) throws Exception {
        validateUserForm(user, false);

        boolean changePassword = (password != null && password.length > 0)
                || (confirmPassword != null && confirmPassword.length > 0);

        if (changePassword) {
            validatePasswordPair(password, confirmPassword, false);
        }

        try {
            user.setUserName(user.getUserName().trim());
            user.setFullName(normalizeText(user.getFullName()));
            user.setEmail(normalizeText(user.getEmail()));
            user.setPhone(normalizeText(user.getPhone()));
            user.setProfileImagePath(normalizeText(user.getProfileImagePath()));
            user.setStatus(normalizeStatus(user.getStatus()));

            if (changePassword) {
                user.setPassword(PasswordUtil.hashPassword(password));
            } else {
                user.setPassword(null);
            }

            userDAO.update(user);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            clearPassword(password);
            clearPassword(confirmPassword);
        }
    }

    public List<User> listUsers(String search, int page, int limit) throws Exception {
        try {
            return userDAO.list(normalizeSearch(search), normalizePage(page), normalizeLimit(limit));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public int countUsers(String search) throws Exception {
        try {
            return userDAO.count(normalizeSearch(search));
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public User findUserById(int idUser) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Usuario no valido.");
        }

        try {
            User user = userDAO.findManagementById(idUser);

            if (user == null) {
                throw new Exception("No se encontro el usuario.");
            }

            return user;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public void deleteUserPhysical(int idUser) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Usuario no valido.");
        }

        try {
            userDAO.deletePhysical(idUser);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public User loginUser(String userName, char[] password) throws Exception {
        validateLogin(userName, password);

        try {
            User user = userDAO.login(userName.trim());

            if (user == null) {
                throw new Exception("Usuario o contraseña incorrectos.");
            }

            boolean validPassword = PasswordUtil.verifyPassword(password, user.getPassword());

            if (!validPassword) {
                throw new Exception("Usuario o contraseña incorrectos.");
            }

            return user;

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            clearPassword(password);
        }
    }

    public void logoutUser(int idUser) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Usuario no valido.");
        }

        try {
            userDAO.logout(idUser);
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public User findUserProfile(int idUser) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Usuario no válido.");
        }

        try {
            User user = userDAO.findById(idUser);

            if (user == null) {
                throw new Exception("No se encontró el perfil del usuario.");
            }

            return user;
        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        }
    }

    public User updateProfile(int idUser, String userName, String fullName, String email, String phone,
            String profileImagePath, char[] password, char[] confirmPassword) throws Exception {
        validateProfile(idUser, userName, fullName, email, phone, profileImagePath, password, confirmPassword);

        try {
            User user = new User();
            user.setIdUser(idUser);
            user.setUserName(userName.trim());
            user.setFullName(normalizeText(fullName));
            user.setEmail(normalizeText(email));
            user.setPhone(normalizeText(phone));
            user.setProfileImagePath(normalizeText(profileImagePath));

            if (password != null && password.length > 0) {
                user.setPassword(PasswordUtil.hashPassword(password));
            } else {
                user.setPassword(null);
            }

            userDAO.updateProfile(user);
            return findUserProfile(idUser);

        } catch (SQLException e) {
            throw new Exception(getSqlMessage(e));
        } finally {
            clearPassword(password);
            clearPassword(confirmPassword);
        }
    }

    private void validateRegister(String userName, char[] password, char[] confirmPassword) throws Exception {
        validateUserName(userName);
        validatePasswordPair(password, confirmPassword, true);
    }

    private void validateLogin(String userName, char[] password) throws Exception {
        if (userName == null || userName.trim().isEmpty()) {
            throw new Exception("Ingrese su usuario.");
        }

        if (password == null || password.length == 0) {
            throw new Exception("Ingrese su contraseña.");
        }
    }

    private void validateProfile(int idUser, String userName, String fullName, String email, String phone,
            String profileImagePath, char[] password, char[] confirmPassword) throws Exception {
        if (idUser <= 0) {
            throw new Exception("Usuario no válido.");
        }

        validateUserName(userName);

        if (fullName != null && fullName.trim().length() > 150) {
            throw new Exception("El nombre completo no puede exceder 150 caracteres.");
        }

        if (email != null && !email.trim().isEmpty() && !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new Exception("Ingrese un correo válido.");
        }

        if (phone != null && phone.trim().length() > 20) {
            throw new Exception("El teléfono no puede exceder 20 caracteres.");
        }

        if (profileImagePath != null && !profileImagePath.trim().isEmpty()) {
            File imageFile = new File(profileImagePath.trim());

            if (!imageFile.exists() || !imageFile.isFile()) {
                throw new Exception("La ruta de la foto de perfil no existe.");
            }
        }

        if ((password != null && password.length > 0) || (confirmPassword != null && confirmPassword.length > 0)) {
            validatePasswordPair(password, confirmPassword, false);
        }
    }

    private void validateUserForm(User user, boolean creating) throws Exception {
        if (user == null) {
            throw new Exception("Complete los datos del usuario.");
        }

        if (!creating && user.getIdUser() <= 0) {
            throw new Exception("Usuario no valido.");
        }

        validateUserName(user.getUserName());

        if (user.getFullName() != null && user.getFullName().trim().length() > 150) {
            throw new Exception("El nombre completo no puede exceder 150 caracteres.");
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()
                && !user.getEmail().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new Exception("Ingrese un correo valido.");
        }

        if (user.getPhone() != null && user.getPhone().trim().length() > 20) {
            throw new Exception("El telefono no puede exceder 20 caracteres.");
        }

        if (user.getProfileImagePath() != null && user.getProfileImagePath().trim().length() > 500) {
            throw new Exception("La ruta de la foto no puede exceder 500 caracteres.");
        }

        if (user.getStatus() != 0 && user.getStatus() != 1) {
            throw new Exception("Seleccione un estado valido.");
        }
    }

    private void validateUserName(String userName) throws Exception {
        if (userName == null || userName.trim().isEmpty()) {
            throw new Exception("Ingrese un nombre de usuario.");
        }

        if (userName.trim().length() < 4) {
            throw new Exception("El nombre de usuario debe tener mínimo 4 caracteres.");
        }
    }

    private void validatePasswordPair(char[] password, char[] confirmPassword, boolean required) throws Exception {
        if (required && (password == null || password.length == 0)) {
            throw new Exception("Ingrese una contraseña.");
        }

        if (required && (confirmPassword == null || confirmPassword.length == 0)) {
            throw new Exception("Confirme la contraseña.");
        }

        if (!required && (password == null || password.length == 0) && (confirmPassword == null || confirmPassword.length == 0)) {
            return;
        }

        if (password == null || password.length == 0) {
            throw new Exception("Ingrese la nueva contraseña.");
        }

        if (password.length < 6) {
            throw new Exception("La contraseña debe tener mínimo 6 caracteres.");
        }

        if (confirmPassword == null || confirmPassword.length == 0) {
            throw new Exception("Confirme la nueva contraseña.");
        }

        if (!Arrays.equals(password, confirmPassword)) {
            throw new Exception("Las contraseñas no coinciden.");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
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

    private int normalizeStatus(int status) {
        return status == 0 ? 0 : 1;
    }

    private void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }

    private String getSqlMessage(SQLException e) {
        if (e.getMessage() != null && !e.getMessage().isBlank()) {
            return e.getMessage();
        }

        return "Ocurrió un error al comunicarse con la base de datos.";
    }
}
