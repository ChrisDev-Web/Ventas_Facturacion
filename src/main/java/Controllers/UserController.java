package Controllers;

import Config.PasswordUtil;
import DAO.UserDAO;
import Models.User;
import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;

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
