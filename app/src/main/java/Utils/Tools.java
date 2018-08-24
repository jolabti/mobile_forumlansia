package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    public static boolean validateEmail(String toCheck) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = toCheck;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }
}
