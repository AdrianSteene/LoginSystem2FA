package MedStore.MedRec.crypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MedRecCryptUtils {

    public static int SALT_LENGTH = 7;
    public static int TOKEN_LENGTH = 64;

    public static String randomString(int length){
        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789:;.,?!@#$%^&*()_+-=";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int)(alphaNumeric.length() * Math.random());
            sb.append(alphaNumeric.charAt(index));
        }
        return sb.toString();
    }
}
