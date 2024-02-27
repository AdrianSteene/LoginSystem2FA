package MedStore.MedRec.crypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Salt {
    private final String salt;

    public Salt() {
        this.salt = MedRecCryptUtils.randomString(MedRecCryptUtils.SALT_LENGTH);
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public String toString() {
        return "Salt{" +
                "salt='" + salt + '\'' +
                '}';
    }
}
