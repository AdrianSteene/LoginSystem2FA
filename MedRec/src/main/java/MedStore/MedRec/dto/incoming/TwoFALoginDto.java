package MedStore.MedRec.dto.incoming;

public class TwoFALoginDto {
    private IncomingLoginToken loginToken;
    private TwoFACode twoFACode;

    public IncomingLoginToken getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(IncomingLoginToken loginToken) {
        this.loginToken = loginToken;
    }

    public TwoFACode getTwoFACode() {
        return twoFACode;
    }

    public void setTwoFACode(TwoFACode twoFACode) {
        this.twoFACode = twoFACode;
    }
}
