package com.admin.vwm.game.android;

import com.jme3.app.AndroidHarness;
import com.admin.vwm.game.game.AdminVWM;


public class AndroidLauncher extends AndroidHarness {

    public AndroidLauncher() {
        appClass = AdminVWM.class.getCanonicalName();
    }
}
