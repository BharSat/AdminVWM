package com.admin.vwm.game.states;

import com.admin.vwm.game.AdminVWM;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.*;

import java.util.Objects;

public class GameAppState extends BaseAppState implements ActionListener {

    protected AdminVWM app;
    protected AppStateManager appStateManager;
    protected InputManager inputManager;
    protected AssetManager assetManager;
    protected Node rootNode;
    protected Node guiNode;
    protected Camera cam;
    protected int mode = 0;
    protected Node rotNode = new Node();
    protected Node mainGuiNode = new Node();
    protected Label statLabel;

    @Override
    protected synchronized void initialize(Application appB) {
        this.app = (AdminVWM)appB;
        appStateManager = getStateManager();
        inputManager = app.getInputManager();
        assetManager = app.getAssetManager();
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        cam = app.getCamera();
        rootNode.attachChild(this.rotNode);
        guiNode.attachChild(mainGuiNode);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        GuiGlobals.initialize(this.app);

//        BaseStyles.loadGlassStyle();
//        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        Container stats = new Container();
        stats.setLocalTranslation(app.width*30f/32f, 50,0);
        stats.setPreferredSize(new Vector3f(app.width/16f, 50, 0));
        statLabel = stats.addChild(new Label("s"));
        guiNode.attachChild(stats);

        Container startWindow = new Container();
        startWindow.setLocalTranslation(0, 800, 0);
        startWindow.setPreferredSize(new Vector3f(cam.getWidth(), cam.getHeight(), 1));

        Label startLabel = startWindow.addChild(new Label("Welcome to Gui Design"));
        startLabel.setFontSize(app.height/8f);
        startLabel.setTextHAlignment(HAlignment.Center);
        Button startButton = startWindow.addChild(new Button("Start"));
        startButton.addClickCommands(source -> openMenu());

        startButton.setFontSize(app.height/8f);
        startButton.setTextHAlignment(HAlignment.Center);

        mainGuiNode.attachChild(startWindow);
    }

    @Override
    protected void onDisable() {

    }

    protected void openMenu() {
        mainGuiNode.detachAllChildren();

        Container menu = new Container();
        menu.setLocalTranslation(0, 800, 0);
        menu.setPreferredSize(new Vector3f(cam.getWidth(), cam.getHeight(), 1));

        Button newButton = menu.addChild(new Button("New Project"));
        newButton.addClickCommands(source -> newSettings());
        newButton.setFontSize(app.height/8f);
        newButton.setTextHAlignment(HAlignment.Center);
        Button openButton = menu.addChild(new Button("Open Project"));
        openButton.setFontSize(app.height/8f);
        openButton.setTextHAlignment(HAlignment.Center);
        Button recentButton = menu.addChild(new Button("Recents Project"));
        recentButton.setFontSize(app.height/8f);
        recentButton.setTextHAlignment(HAlignment.Center);

        mainGuiNode.attachChild(menu);
    }

    protected void newSettings() {
        mode = 2;
        mainGuiNode.detachAllChildren();
        inputManager.setCursorVisible(false);
        Container newWindow = new Container();
        newWindow.setLocalTranslation(0, 700, 0);
        newWindow.setPreferredSize(new Vector3f(cam.getWidth(), cam.getHeight()/8f, 1));

        Label newLabel = newWindow.addChild(new Label("Choose Your Scene:"));
        newLabel.setFontSize(app.height/32f);
        newLabel.setTextHAlignment(HAlignment.Center);
        mainGuiNode.attachChild(newWindow);

        Spatial model = assetManager.loadModel("Models/lawn_round.glb");
        model.setLocalScale(0.05f, 0.05f, 0.1f);
        model.setLocalTranslation(0, 0, -5f);
        rotNode.attachChild(model);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f, 0f, -1f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection(new Vector3f(0f, 0f, 1f).normalizeLocal());
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);
        inputManager.addMapping("click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "click");

        Container startWindow = new Container();
        startWindow.setLocalTranslation(0, (app.height)-300, 0);
        startWindow.setPreferredSize(new Vector3f(cam.getWidth(), cam.getHeight(), 1));

//        Label startLabel = startWindow.addChild(new Label("Welcome to Gui Design"));
//        startLabel.setFontSize(1f);
//        startLabel.setTextHAlignment(HAlignment.Center);
//        startLabel.setLocalTranslation(0f, 0f, 10f);
//        startLabel.rotate(0, 180, 0);
//
//        rootNode.attachChild(startLabel);
    }

    public void newGame(String name) {
        mode = 3;
        System.out.println(name);
        mainGuiNode.detachAllChildren();
        rotNode.detachAllChildren();
        this.app.getFlyByCamera().setEnabled(false);
        cam.setLocation(new Vector3f(50f, 175f, 0));

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        Spatial quad_div = assetManager.loadModel("Models/quad_div.glb");
        rotNode.attachChild(quad_div);

        Spatial scene;

        if ("lawn_round".equals(name)) {
            scene = assetManager.loadModel("Models/lawn_round.glb");
            scene.setLocalTranslation(0, 0, 0);
            rotNode.attachChild(scene);
            cam.lookAt(new Vector3f(50f, 0f, 0f), Vector3f.UNIT_Y);
        }



        Container infoMenu = new Container();
        infoMenu.setLocalTranslation(0, 700, 0);
        infoMenu.setPreferredSize(new Vector3f(cam.getWidth()/2f, cam.getHeight(), 1));

        infoMenu.addChild(new Label("Name: "), 0, 0);
        TextField projectName = infoMenu.addChild(new TextField("New Project-1"), 0, 1);
        infoMenu.addChild(new Label("Rounds:"), 1, 0);
        TextField sessionsText = infoMenu.addChild(new TextField("10"), 1, 1);
        infoMenu.addChild(new Label("Trials:"), 2, 0);
        TextField trialsText = infoMenu.addChild(new TextField("4"), 2, 1);
        infoMenu.addChild(new Label("Platform file:"), 3, 0);
        TextField platformFileText = infoMenu.addChild(new TextField("platforms.csv"), 3, 1);
        Button saveButton = infoMenu.addChild(new Button("Save Project"), 4, 0);
        Button loadButton = infoMenu.addChild(new Button("Load Locations"), 4, 1);

        guiNode.attachChild(infoMenu);

        inputManager.deleteMapping("click");
        /*
        Removed Because once entries gain focus, these are pointless
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addListener(onAnalog, "up", "down", "left", "right");
        */

    }

    @Override
    public void update(float tpf) {
        switch (mode) {
            case 0:
                break;
            case 2:
                for (Spatial child: rotNode.getChildren()) {
                    child.rotate(0.01f, 0, 0);
                }
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rotNode.collideWith(ray, results);
                try {
                    if (results.getClosestCollision().getGeometry().getName().equals("lawn_round")) {
                        statLabel.setText("Lawn - Round");
                    }
                } catch (NullPointerException ignored) {
                        statLabel.setText("");
                }
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (Objects.equals(name, "destroy")) {
            app.destroy();
        }
        switch (mode) {
            case 0:
                break;
            case 2:
                if (Objects.equals(name, "click")) {
                    CollisionResults results = new CollisionResults();
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    rotNode.collideWith(ray, results);
                    try {
                        if (results.getClosestCollision().getGeometry().getName().equals("lawn_round")) {
                            newGame("lawn_round");
                        }
                    } catch (NullPointerException ignored) {

                    }
                }
        }
    }
    private final AnalogListener onAnalog = (name, value, tpf) -> {
        switch (mode) {
            case 3:
                if (Objects.equals(name, "up")) {
                    for (Spatial child : rotNode.getChildren()) {
                        child.rotate(0.01f, 0, 0);
                    }
                } else if (Objects.equals(name, "down")) {
                    for (Spatial child : rotNode.getChildren()) {
                        child.rotate(-0.01f, 0, 0);
                    }
                }
                if (Objects.equals(name, "left")) {
                    for (Spatial child : rotNode.getChildren()) {
                        child.rotate(0f, 0f, 0.01f);
                    }
                } else if (Objects.equals(name, "right")) {
                    for (Spatial child : rotNode.getChildren()) {
                        child.rotate(0f, 0f, -0.01f);
                    }
                }
        }
    };
}
