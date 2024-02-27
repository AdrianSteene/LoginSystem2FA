package MedStore.MedRec.dto.incoming;

import MedStore.MedRec.crypt.MedRecCryptUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.coyote.BadRequestException;

public class IncomingLoginToken {

    private final String longinToken;

    @JsonCreator
    public IncomingLoginToken(String loginToken) throws BadRequestException {
        if (loginToken == null || loginToken.isBlank() || loginToken.length() != MedRecCryptUtils.TOKEN_LENGTH) {
            throw new BadRequestException("loginToken cannot be null");
        }
        this.longinToken = loginToken;
    }

    public String getLonginToken() {
        return longinToken;
    }
}
