package MedStore.MedRec.dto.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.coyote.BadRequestException;

import java.io.Serializable;

public class IncomingJWT implements Serializable {
    private final String token;

    @JsonCreator
    public IncomingJWT(String token) throws BadRequestException {
        if (token == null || token.isBlank()) throw new BadRequestException("Invalid Token");
        this.token = token;
    }


    public String getToken() {
        return token;
    }
}
