package digital.srp.sreport.internal;

public class StringUtils {

    public static String toConst(String string) {
        String c = string.toUpperCase().replaceAll("[ /-]", "_")
                .replaceAll("[:;%&()]", "").replaceAll("___", "_")
                .replaceAll("__", "_");
        if (Character.isDigit(c.charAt(0))) {
            return "_" + c;
        } else {
            return c;
        }
    }

    public static String camelCaseToConst(String string) {
        return string.replaceAll("([a-z])([A-Z]+)", "$1_$2").toUpperCase();
    }

    public static String toSentanceCase(String s) {
        return Character.toUpperCase(s.charAt(0))
                + s.substring(1).toLowerCase().replaceAll("_", " ");
    }

}
