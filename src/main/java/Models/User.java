package Models;

import java.time.LocalDateTime;

public class User {

    private int idUser;
    private String userName;
    private String fullName;
    private String email;
    private String phone;
    private String profileImagePath;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
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

    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName.trim();
        }

        if (userName != null && !userName.trim().isEmpty()) {
            return userName.trim();
        }

        return "Usuario";
    }

    @Override
    public String toString() {
        return userName;
    }
}
