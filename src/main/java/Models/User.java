package Models;

import java.time.LocalDateTime;

public class User {

    private int idUser;
    private String userName;
    private String password;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(int idUser, String userName, String password, int status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.idUser = idUser;
        this.userName = userName;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.status = 1;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Aquí se guarda la contraseña ya encriptada, no la contraseña plana.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return status == 1;
    }

    @Override
    public String toString() {
        return userName;
    }
}