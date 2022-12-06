package com.admin.vwm.game;

import com.admin.vwm.game.classes.GameAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;

/**
 * The JMonkeyEngine game entry, you should only do initializations for your game here, game logic is handled by
 * Custom states {@link com.jme3.app.state.BaseAppState}, Custom controls {@link com.jme3.scene.control.AbstractControl}
 * and your custom entities implementations of the previous.
 *
 */
public class AdminVWM extends SimpleApplication {
    public int width;
    public int height;
    public AdminVWM(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public AdminVWM(int width, int height, AppState... initialStates) {
        super(initialStates);
        this.width = width;
        this.height = height;
    }

    @Override
    public void simpleInitApp() {

        GameAppState gameAppState = new GameAppState();
        stateManager.attach(gameAppState);
    }

}
