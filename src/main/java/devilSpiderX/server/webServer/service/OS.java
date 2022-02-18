package devilSpiderX.server.webServer.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class OS {

    public static String system(String cmd) {
        StringBuilder resultBuilder = new StringBuilder();
        BufferedReader resultReader = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            resultReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            while ((line = resultReader.readLine()) != null) {
                resultBuilder.append(line).append("\n");
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (resultReader != null) {
                try {
                    resultReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultBuilder.toString();
    }

    public static void reboot(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                system("shutdown /r /t 0 /d p:4:1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "reboot").start();
    }

    public static void shutdown(long millis) {
        new Thread(() -> {
            try {
                Thread.sleep(millis);
                system("shutdown /s /t 0 /d p:4:1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "shutdown").start();
    }

}
