package devilSpiderX.server.webServer.core.service.factory;

import devilSpiderX.server.webServer.core.service.impl.WindowsOS;
import devilSpiderX.server.webServer.core.service.OS;
import devilSpiderX.server.webServer.core.service.impl.LinuxOS;

public final class OSFactory {
    private static OS os;

    public static OS getOS() {
        if (os == null) {
            String name = System.getProperty("os.name");
            if (name.startsWith("Windows")) {
                os = new WindowsOS();
            } else if (name.startsWith("Linux")) {
                os = new LinuxOS();
            }
        }
        return os;
    }
}
