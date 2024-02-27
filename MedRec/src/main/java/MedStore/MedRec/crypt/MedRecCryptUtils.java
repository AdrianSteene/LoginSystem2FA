package MedStore.MedRec.crypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MedRecCryptUtils {

    public static int SALT_LENGTH = 7;
    public static int TOKEN_LENGTH = 64;

    public static String randomString(int length){
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }
}
