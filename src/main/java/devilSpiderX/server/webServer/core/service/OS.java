package devilSpiderX.server.webServer.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public interface OS {
    String getOSName();

    Charset getCharset();

    default String system(String... cmd) {
        return system(Arrays.asList(cmd));
    }

    default String system(List<String> cmd) {
        Logger logger = LoggerFactory.getLogger(OS.class);
        StringBuilder resultBuilder = new StringBuilder();
        Process process = null;
        BufferedReader resultReader = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            process = builder.start();
            resultReader = new BufferedReader(new InputStreamReader(process.getInputStream(), getCharset()));
            String line;
            while ((line = resultReader.readLine()) != null) {
                resultBuilder.append(line).append("\n");
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (resultReader != null) {
                    resultReader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultBuilder.toString();
    }

    void reboot(long millis);

    void shutdown(long millis);

}
