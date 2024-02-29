package MedStore.MedRec.crypt;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MedRecCryptUtils {

    public static int SALT_LENGTH = 7;
    public static int TOKEN_LENGTH = 64;

    public static int MAX_VALUE = 1000000;
    public static int MIN_VALUE = 1;

    public static String randomString(int length) {
        String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789:;.,?!@#$%^&*()_+-=";
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = (int) (alphaNumeric.length() * Math.random());
            sb.append(alphaNumeric.charAt(index));
        }
        return sb.toString();
    }

    public static String random2FAString() {
        Set<Integer> codes = new HashSet<>();

        int[] numbers = {
                123456, 111111, 000000, 121212, 112233, 123123, 654321, 666666,
                222222, 333333, 444444, 555555, 777777, 888888, 999999, 123321,
                123456, 654321, 111222, 101010, 202020, 303030, 404040, 505050,
                606060, 707070, 808080, 909090, 100100, 200200, 300300, 400400,
                500500, 600600, 700700, 800800, 900900, 101101, 202202, 303303,
                404404, 505505, 606606, 707707, 808808, 909909, 121121, 131313,
                141414, 151515, 161616, 171717, 181818, 191919, 212121, 232323,
                242424, 252525, 262626, 272727, 282828, 292929, 313131, 323232,
                343434, 353535, 363636, 373737, 383838, 393939, 414141, 424242,
                434343, 454545, 464646, 474747, 484848, 494949, 515151, 525252,
                535353, 545454, 565656, 575757, 585858, 595959, 616161, 626262,
                636363, 646464, 656565, 676767, 686868, 696969, 717171, 727272,
                737373, 747474, 757575, 767676, 1, 2, 3, 4, 5, 6, 7, 8, 9
        };

        for (int number : numbers) {
            codes.add(number);
        }

        String randomCode;
        int randomNum;

        do {
            Random rand = new Random();
            randomNum = rand.nextInt(MAX_VALUE) + MIN_VALUE;
        } while (codes.contains(randomNum));

        randomCode = String.valueOf(randomNum);

        for (int index = 0; index < 6 - randomCode.length(); index++) {
            randomCode = '0' + randomCode;
        }

        return randomCode;

    }
}
