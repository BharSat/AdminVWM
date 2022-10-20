package com.admin.vwm.game.desktopmodule;

import com.admin.vwm.game.AdminVWM;
import com.jme3.system.AppSettings;

/**
 * Used to launch a jme application in desktop environment
 *
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        DesktopLauncher.launch();
    }

    public static void launch() {

        int height = 1080;
        int width = 1192;
        final AdminVWM game = new AdminVWM(width, height);

        final AppSettings appSettings = new AppSettings(true);

        appSettings.setResolution(width*2, height*2);
        appSettings.setWidth(width);
        appSettings.setHeight(height);
        appSettings.setResizable(true);
        appSettings.setWindowYPosition(100);
        appSettings.setFullscreen(false);

        game.setShowSettings(false);
        game.setDisplayFps(false);
        game.setDisplayStatView(false);
        game.setSettings(appSettings);
        game.start();
    }
}
