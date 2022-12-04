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
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.*;
import com.simsilica.lemur.style.BaseStyles;

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
    protected DirectionalLight camLight = new DirectionalLight();
    protected String arenaName;
    protected TextField projectNameNew;
    protected TextField dirNameTextNew;
    protected TextField sessionsTextNew;
    protected TextField trialsTextNew;
    protected TextField projectFileTextNew;
    protected TextField modelPathNew;
    protected  TextField sessionNoEdit;
    protected  TextField trialNoEdit;
    protected  TextField sizeEdit;
    protected TextField platXEdit;
    protected TextField platZEdit;
    protected TextField platShapeEdit;
    protected TextField cueNoEdit;
    protected TextField cueXEdit;
    protected TextField cueYEdit;
    protected TextField cueZEdit;
    protected TextField cueNameEdit;
    protected ProjectManager currentProject;


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
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

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
        cam.setLocation(new Vector3f(0, 0, 10f));
        cam.setRotation(new Quaternion(-0.0052f, 0.9828f, 0.1820f, 0.0283f));
        inputManager.setCursorVisible(false);
        this.app.getFlyByCamera().setEnabled(true);
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

    public void newProject(String name) {
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
            arenaName = "default1";
            scene.setLocalTranslation(0, 0, 0);
            rotNode.attachChild(scene);
            cam.lookAt(new Vector3f(50f, 0f, 0f), Vector3f.UNIT_Y);
        }



        Container infoMenu = new Container();
        infoMenu.setLocalTranslation(0, 700, 0);
        infoMenu.setPreferredSize(new Vector3f(cam.getWidth()/2f, cam.getHeight(), 1));

        infoMenu.addChild(new Label("Name: "), 0, 0);
        projectNameNew = infoMenu.addChild(new TextField("New Project-1"), 0, 1);
        infoMenu.addChild(new Label("Root Directory: "), 1, 0);
        dirNameTextNew = infoMenu.addChild(new TextField(System.getProperty("user.dir")), 1, 1);
        infoMenu.addChild(new Label("Rounds:"), 2, 0);
        sessionsTextNew = infoMenu.addChild(new TextField("10"), 2, 1);
        infoMenu.addChild(new Label("Trials:"), 3, 0);
        trialsTextNew = infoMenu.addChild(new TextField("4"), 3, 1);
        infoMenu.addChild(new Label("Platform file:"), 4, 0);
        projectFileTextNew = infoMenu.addChild(new TextField("newProject1.csv"), 4, 1);
        infoMenu.addChild(new Label("Model files:"), 5, 0);
        modelPathNew = infoMenu.addChild(new TextField("/models/<model_name>/<model_name>.gltf"), 5, 1);
        Button saveButton = infoMenu.addChild(new Button("Main Menu"), 6, 0);
        saveButton.addClickCommands(source -> {rotNode.detachAllChildren();openMenu();});
        Button loadButton = infoMenu.addChild(new Button("Start Project"), 6, 1);
        loadButton.addClickCommands(source -> {mainGuiNode.detachAllChildren(); editProject();});

        mainGuiNode.attachChild(infoMenu);

        inputManager.deleteMapping("click");
        /*
        Removed Because once entries gain focus, these are pointless
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addListener(onAnalog, "up", "down", "left", "right");*/

        inputManager.addMapping("left", new MouseButtonTrigger(MouseInput.AXIS_WHEEL));
        inputManager.addListener(onAnalog, "left");

    }

    protected void editProject() {
        this.currentProject = ProjectManager.newProject(this, projectNameNew.getText(), dirNameTextNew.getText(), projectFileTextNew.getText());
        int sessions = Integer.parseInt(sessionsTextNew.getText());
        int trials = Integer.parseInt(trialsTextNew.getText());
        currentProject.initPlatformLocations(sessions, trials, arenaName);
        mode = 4;
        this.currentProject.save();
        Container editProjectMenu = new Container();
        editProjectMenu.setLocalTranslation(0, 770, 0);
        editProjectMenu.setPreferredSize(new Vector3f(cam.getWidth()/2f, cam.getHeight(), 1));

        Container static_ = editProjectMenu.addChild(new Container(), 0, 0);
        Container dynamic1 = editProjectMenu.addChild(new Container(), 0, 0);
//        Container dynamic2 = editProjectMenu.addChild(new Container());
        Container buttons = dynamic1.addChild(new Container(), 8, 1);

        editProjectMenu.removeChild(static_);

        dynamic1.addChild(new Label("Session no:"), 0, 0);
        sessionNoEdit = dynamic1.addChild(new TextField("0"), 0, 1);
        dynamic1.addChild(new Label("Trial no:"), 1, 0);
        trialNoEdit = dynamic1.addChild(new TextField("0"), 1, 1);

        dynamic1.addChild(new Label("End Region Location:"), 2, 0);
        Container platContainer = dynamic1.addChild(new Container(), 2, 1);
        platContainer.addChild(new Label("X:"));
        platXEdit = platContainer.addChild(new TextField("0"), 1);
        platContainer.addChild(new Label("Z:"));
        platZEdit = platContainer.addChild(new TextField("0"), 1);
        platContainer.addChild(new Label("Shape(rect, circle):"));
        platShapeEdit = platContainer.addChild(new TextField("rect"), 1);

        dynamic1.addChild(new Label("Cue Location:"), 3, 0);
        Container cueContainer = dynamic1.addChild(new Container(), 3, 1);
        cueContainer.addChild(new Label("Cue No."), 0, 0);
        cueNoEdit = cueContainer.addChild(new TextField("1"), 0, 1);
        cueContainer.addChild(new Label("Cue X"), 1, 0);
        cueXEdit = cueContainer.addChild(new TextField("0"), 1, 1);
        cueContainer.addChild(new Label("Cue Y"), 2, 0);
        cueYEdit = cueContainer.addChild(new TextField("1"), 2, 1);
        cueContainer.addChild(new Label("Cue Z"), 3, 0);
        cueZEdit = cueContainer.addChild(new TextField("0"), 3, 1);
        cueContainer.addChild(new Label("Cue Name"), 4, 0);
        cueNameEdit = cueContainer.addChild(new TextField("Monkey"), 4, 1);

        Button applyButton = buttons.addChild(new Button("Apply"), 0, 1);
        Button staticButton = buttons.addChild(new Button("undo"), 0, 0);
        Button dynamicButton = buttons.addChild(new Button("Back"), 1, 0);
        Button exitButton = buttons.addChild(new Button("Exit"), 1, 1);

        static_.addChild(new Label("Arena relative scale:"), 0, 0);
        sizeEdit = static_.addChild(new TextField("35.00"), 0, 1);


        mainGuiNode.attachChild(editProjectMenu);

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
                            newProject("lawn_round");
                        }
                    } catch (NullPointerException ignored) {

                    }
                }
        }
    }
    private final AnalogListener onAnalog = (name, value, tpf) -> {
        if (mode == 3 || mode==4) {
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
