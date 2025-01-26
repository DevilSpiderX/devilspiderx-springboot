package devilSpiderX.server.webServer.core.service;

import devilSpiderX.server.webServer.core.service.impl.LinuxOS;
import devilSpiderX.server.webServer.core.service.impl.WindowsOS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public interface OS {
    String getOSName();

    Charset getCharset();

    default String system(String... cmd) throws IOException, InterruptedException {
        return system(Arrays.asList(cmd));
    }

    default String system(List<String> cmd) throws IOException, InterruptedException {
        final StringBuilder resultBuilder = new StringBuilder();
        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            process = builder.start();
            try (var resultReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream(),
                    getCharset()
            ))) {
                String line;
                while ((line = resultReader.readLine()) != null) {
                    resultBuilder.append(line).append("\n");
                }
            }
            process.waitFor();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return resultBuilder.toString();
    }

    void reboot(long millis);

    void shutdown(long millis);

    class Factory {
        private static OS os;

        public static OS getOS() {
            if (os == null) {
                String name = System.getProperty("os.name");
                if (name.startsWith("Windows")) {
                    os = new WindowsOS();
                } else if (name.startsWith("Linux")) {
                    os = new LinuxOS();
                } else {
                    throw new UnsupportedOperationException("Unsupported OS: " + name);
                }
            }
            return os;
        }
    }
}
