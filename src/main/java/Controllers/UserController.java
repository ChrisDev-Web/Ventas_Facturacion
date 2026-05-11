package Controllers;

import Config.PasswordUtil;
import DAO.UserDAO;
import Models.User;
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

    private void validateRegister(String userName, char[] password, char[] confirmPassword) throws Exception {
        if (userName == null || userName.trim().isEmpty()) {
            throw new Exception("Ingrese un nombre de usuario.");
        }

        if (userName.trim().length() < 4) {
            throw new Exception("El nombre de usuario debe tener mínimo 4 caracteres.");
        }

        if (password == null || password.length == 0) {
            throw new Exception("Ingrese una contraseña.");
        }

        if (password.length < 6) {
            throw new Exception("La contraseña debe tener mínimo 6 caracteres.");
        }

        if (confirmPassword == null || confirmPassword.length == 0) {
            throw new Exception("Confirme la contraseña.");
        }

        if (!Arrays.equals(password, confirmPassword)) {
            throw new Exception("Las contraseñas no coinciden.");
        }
    }

    private void validateLogin(String userName, char[] password) throws Exception {
        if (userName == null || userName.trim().isEmpty()) {
            throw new Exception("Ingrese su usuario.");
        }

        if (password == null || password.length == 0) {
            throw new Exception("Ingrese su contraseña.");
        }
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