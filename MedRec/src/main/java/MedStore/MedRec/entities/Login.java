package MedStore.MedRec.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Login implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long loginId;
    private long userId;
    private String loginToken;
    private LocalDateTime created;
    private boolean expired;

    public long getLoginId() {
        return loginId;
    }

    public void setLoginId(long loginId) {
        this.loginId = loginId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Login login = (Login) o;
        if (loginId != login.loginId) return false;
        if (userId != login.userId) return false;
        if (expired != login.expired) return false;
        if (!created.equals(login.created)) return false;
        return loginToken.equals(login.loginToken);
    }

    @Override
    public String toString() {
        return "Login{" + "loginId=" + loginId + ", userId=" + userId + ", loginToken=" + loginToken + ", created=" + created + ", expired=" + expired + "}";
    }
}
