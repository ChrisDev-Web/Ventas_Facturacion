package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Centraliza la conexion a MySQL para que todo el proyecto use la misma configuracion.
public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/Ventas_Facturacion?useSSL=false&serverTimezone=America/Lima";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
