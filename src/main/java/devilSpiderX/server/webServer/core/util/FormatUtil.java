package devilSpiderX.server.webServer.core.util;

import java.text.DecimalFormat;

public class FormatUtil {
    public static final long KB = 1L << 10;
    public static final long MB = 1L << 20;
    public static final long GB = 1L << 30;
    public static final long TB = 1L << 40;
    public static final long PB = 1L << 50;
    public static final long EB = 1L << 60;

    public record UnitRecord(Double value, String unit) {
    }

    public static UnitRecord unitBytes(long n, int scale) {
        if (n >= EB) {
            return new UnitRecord(Arithmetic.div(n, EB, scale), "EB");
        } else if (n >= PB) {
            return new UnitRecord(Arithmetic.div(n, PB, scale), "PB");
        } else if (n >= TB) {
            return new UnitRecord(Arithmetic.div(n, TB, scale), "TB");
        } else if (n >= GB) {
            return new UnitRecord(Arithmetic.div(n, GB, scale), "GB");
        } else if (n >= MB) {
            return new UnitRecord(Arithmetic.div(n, MB, scale), "MB");
        } else if (n >= KB) {
            return new UnitRecord(Arithmetic.div(n, KB, scale), "KB");
        } else {
            return new UnitRecord(n * 1.0, "B");
        }
    }

    public static String formatBytes(long n, int scale, CharSequence delimiter) {
        if (delimiter == null) delimiter = "";
        UnitRecord record = unitBytes(n, scale);
        String num = new DecimalFormat("#." + "#".repeat(scale)).format(record.value());
        return String.format("%s" + delimiter + "%s", num, record.unit());
    }
}
