package MedStore.MedRec.crypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Salt {
    private final String salt;

    public Salt() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        this.salt = new String(array, StandardCharsets.UTF_8);
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
