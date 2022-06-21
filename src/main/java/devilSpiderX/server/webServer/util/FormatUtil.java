package devilSpiderX.server.webServer.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public class FormatUtil {
    public static final long KB = 1L << 10;
    public static final long MB = 1L << 20;
    public static final long GB = 1L << 30;
    public static final long TB = 1L << 40;
    public static final long PB = 1L << 50;
    public static final long EB = 1L << 60;

    public static Tuple2<Double, String> unitBytes(long n, int scale) {
        if (n >= EB) {
            return Tuple.of(Arithmetic.div(n, EB, scale), "EB");
        } else if (n >= PB) {
            return Tuple.of(Arithmetic.div(n, PB, scale), "PB");
        } else if (n >= TB) {
            return Tuple.of(Arithmetic.div(n, TB, scale), "TB");
        } else if (n >= GB) {
            return Tuple.of(Arithmetic.div(n, GB, scale), "GB");
        } else if (n >= MB) {
            return Tuple.of(Arithmetic.div(n, MB, scale), "MB");
        } else if (n >= KB) {
            return Tuple.of(Arithmetic.div(n, KB, scale), "KB");
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
