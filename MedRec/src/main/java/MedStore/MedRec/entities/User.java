package MedStore.MedRec.entities;

import MedStore.MedRec.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;
    private String username;
    private String passwordhash;
    private String salt;
    private Role role;
    @Nullable
    private Long divisionId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Nullable
    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(@Nullable Long divisionId) {
        this.divisionId = divisionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        if (userId != user.userId) return false;
        if (!Objects.equals(username, user.username)) return false;
        if (!Objects.equals(passwordhash, user.passwordhash)) return false;
        if (!Objects.equals(salt, user.salt)) return false;
        if (role != user.role) return false;

        return Objects.equals(divisionId, user.divisionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, passwordhash, salt, role, divisionId);
    }

    @Override
    public String toString(){
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", divisionId=" + divisionId +
                '}';
    }

}
