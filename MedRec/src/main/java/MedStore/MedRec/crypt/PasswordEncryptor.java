package MedStore.MedRec.crypt;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class PasswordEncryptor {

    public static String encrypt(String password, String salt){
        return Hashing
                .sha256()
                .hashString(
                        password + salt,
                        StandardCharsets.UTF_8)
                .toString();
    }

    public static boolean isNotValidHash(String passwordhash1, String passwordhash2){
        return !passwordhash1.equals(passwordhash2);
    }
}
