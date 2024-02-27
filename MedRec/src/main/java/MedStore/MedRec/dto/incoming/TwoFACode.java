package MedStore.MedRec.dto.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.coyote.BadRequestException;

public class TwoFACode {
    private final String twoFACode;

    @JsonCreator
    public TwoFACode(String loginToken) throws BadRequestException {
        if (loginToken == null || loginToken.isBlank()) {
            throw new BadRequestException("loginToken cannot be null");
        }
        this.twoFACode = loginToken;
    }

    public String getLonginToken() {
        return twoFACode;
    }
}
