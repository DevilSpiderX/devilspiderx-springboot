package devilSpiderX.server.webServer.frame;


import devilSpiderX.server.webServer.MainApplication;
import devilSpiderX.server.webServer.config.MyConfig;
import devilSpiderX.server.webServer.service.OS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class DSXTrayIcon {
    private final MyConfig config;
    private final TrayIcon trayIcon;

    public DSXTrayIcon(MyConfig config) {
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
        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(new MenuItem("Open Directory"));
        popupMenu.addSeparator();
        popupMenu.add(new MenuItem("Exit"));

        MenuItem explorerMenuItem = popupMenu.getItem(0);
        explorerMenuItem.addActionListener(e -> OS.system("explorer", config.getLocalPath()));

        MenuItem exitMenuItem = popupMenu.getItem(2);
        exitMenuItem.addActionListener(e -> MainApplication.close());
        trayIcon.setPopupMenu(popupMenu);
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
}
