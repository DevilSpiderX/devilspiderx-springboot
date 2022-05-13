package devilSpiderX.server.webServer.frame;


import devilSpiderX.server.webServer.MainApplication;
import devilSpiderX.server.webServer.config.MyConfig;
import devilSpiderX.server.webServer.service.OS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.nio.file.Paths;

public class DSXTrayIcon {
    private static DSXTrayIcon instance;
    private final MyConfig config;
    private final TrayIcon trayIcon;

    private DSXTrayIcon(MyConfig config) {
        this.config = config;
        trayIcon = new TrayIcon(getIcon());
        trayIcon.setToolTip("WebServer Of DevilSpiderX");
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JOptionPane.showMessageDialog(null,
                            "这是DevilSpiderX的网页服务器",
                            "关于",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        addPopupMenu();
    }

    private void addPopupMenu() {
        String localPathStr = config.getLocalPath().toString();
        MenuItem explorerMenuItem = new MenuItem("Open Directory");
        explorerMenuItem.addActionListener(e -> OS.system("explorer", localPathStr));

        String logPathStr = Paths.get(localPathStr, "log").toString();
        MenuItem logDirMenuItem = new MenuItem("Log Directory");
        logDirMenuItem.addActionListener(e -> OS.system("explorer", logPathStr));

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addActionListener(e -> {
            trayIcon.setToolTip("WebServer is stopping");
            MainApplication.close();
        });

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(explorerMenuItem);
        popupMenu.add(logDirMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(exitMenuItem);
        trayIcon.setPopupMenu(popupMenu);
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public MenuItem getMenuItem(int index) {
        return trayIcon.getPopupMenu().getItem(index);
    }

    public void addSystemTrip() {
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                systemTray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "无法添加托盘",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Image getIcon() {
        URL url = ClassLoader.getSystemResource("TrayIcon_" + config.getTRAY_ICON_INDEX() + ".png");
        Image result = null;
        try {
            result = new ImageIcon(url).getImage();
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "托盘图标不存在",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(e.hashCode());
        }
        return result;
    }

    public static DSXTrayIcon getInstance() {
        return instance;
    }

    public static DSXTrayIcon getInstance(MyConfig config) {
        if (instance == null) {
            instance = new DSXTrayIcon(config);
        }
        return instance;
    }
}
