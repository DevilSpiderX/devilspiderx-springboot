package devilSpiderX.server.webServer.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public class FormatUtil {
    public static Tuple2<Double, String> unitBytes(long n, int scale) {
        if (n >= 1099511627776L) {
            return Tuple.of(Arithmetic.div(n, 1099511627776L, scale), "TB");
        } else if (n >= 1073741824L) {
            return Tuple.of(Arithmetic.div(n, 1073741824L, scale), "GB");
        } else if (n >= 1048576L) {
            return Tuple.of(Arithmetic.div(n, 1048576L, scale), "MB");
        } else if (n >= 1024L) {
            return Tuple.of(Arithmetic.div(n, 1024L, scale), "KB");
        } else {
            return Tuple.of(n * 1.0, "B");
        }
    }

    public static String formatBytes(long n, int scale, CharSequence delimiter) {
        if (delimiter == null) delimiter = "";
        String formatStr = "%." + scale + "f" + delimiter + "%s";
        Tuple2<Double, String> tup = unitBytes(n, scale);
        return String.format(formatStr, tup._1, tup._2);
    }
}
