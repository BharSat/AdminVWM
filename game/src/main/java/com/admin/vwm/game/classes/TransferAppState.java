package com.admin.vwm.game.classes;

import com.admin.vwm.game.AdminVWM;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TransferAppState extends BaseAppState {
    private BluetoothConnectionManager bluetoothConnectionManager;
    private AdminVWM app;
    private AppStateManager appStateManager;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Node rootNode;
    private Node guiNode;
    private Camera cam;

    private TextField filePathStart, assetPathStart;

    @Override
    protected void initialize(Application appB) {
        this.app = (AdminVWM)appB;
        appStateManager = getStateManager();
        inputManager = app.getInputManager();
        assetManager = app.getAssetManager();
        rootNode = app.getRootNode();
        guiNode = app.getGuiNode();
        cam = app.getCamera();
        bluetoothConnectionManager = new BluetoothConnectionManager(this);
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        guiNode.detachAllChildren();
        Container mainContainer = new Container();
        mainContainer.addChild(new Label("File Path:"), 0, 0);
        filePathStart = mainContainer.addChild(new TextField("File path..."), 0, 1);
        mainContainer.addChild(new Label("Assets Path:"), 1, 0);
        assetPathStart = mainContainer.addChild(new TextField("Asset path..."), 1, 1);
        Button transferButton = mainContainer.addChild(new Button("Start transfer"), 2, 1);
        transferButton.addClickCommands(source -> {
            searchForDevices();
        });
        guiNode.attachChild(mainContainer);
    }

    protected void searchForDevices() {
        guiNode.detachAllChildren();
        Container mainContainer = new Container();
        mainContainer.addChild(new TextField("Your Device is now discoverable"));
    }

    @Override
    protected void onDisable() {

    }

    protected void onConnectionResult(boolean success, BluetoothConnectionManager manager) {

    }
    private static class BluetoothConnectionManager {
        BluetoothConnector connector;
        BluetoothDataWriter dataWriter;
        TransferAppState transferAppState;
        public BluetoothConnectionManager(TransferAppState transferAppState) {
            this.transferAppState=transferAppState;
        }

        public void connect() {
            connector = new BluetoothConnector(this);
            dataWriter = new BluetoothDataWriter(this);
        }

        public void onConnected(boolean success) {
            transferAppState.onConnectionResult(success, this);
            try {
                dataWriter.setStreamConnection(connector.getStreamConnection());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void write(String data) {
            dataWriter.output(data);
        }
        public void onWriteFinished(boolean success) {
            try {
                dataWriter.close();
                dataWriter = new BluetoothDataWriter(this, connector.getStreamConnection());
            } catch (IOException ignored) {}

            if (!success) {
                throw new RuntimeException("Failed To write data to stream");
            }
        }
    }

    private static class BluetoothConnector extends Thread{
        private static final String myServiceName = "MyBtService";
        private final String myServiceUUID = java.util.UUID.randomUUID().toString();
        private final UUID MYSERVICEUUID_UUID = new UUID(myServiceUUID, false);
//        private UUID[] uuids = {MYSERVICEUUID_UUID};
        private RemoteDevice connectedDevice;
        private StreamConnection streamConnection;
        private StreamConnectionNotifier scn;
        private final BluetoothConnectionManager manager;
        public BluetoothConnector(BluetoothConnectionManager manager) {
            this.manager = manager;
        }

        @Override
        public void run() {
            LocalDevice localDevice;
            DiscoveryAgent discoveryAgent;
            try {
                localDevice = LocalDevice.getLocalDevice();
                localDevice.setDiscoverable(DiscoveryAgent.GIAC);
                discoveryAgent = localDevice.getDiscoveryAgent();
                String connURL = "btspp://localhost: "+ MYSERVICEUUID_UUID +";+name="+myServiceName;
                scn = (StreamConnectionNotifier)Connector.open(connURL);
                streamConnection = scn.acceptAndOpen();
                connectedDevice = RemoteDevice.getRemoteDevice(streamConnection);
                scn.close();
            } catch (IOException e) {
                manager.onConnected(false);
            }
            manager.onConnected(true);
        }

        public RemoteDevice getConnectedDevice() {
            return this.connectedDevice;
        }
        public StreamConnection getStreamConnection() {
            return this.streamConnection;
        }
        public void close() throws IOException {
            scn.close();
        }
    }

    private static class BluetoothDataWriter extends Thread {
        BluetoothConnectionManager bluetoothConnectionManager;
        StreamConnection streamConnection;
        OutputStream outputStream;
        byte[] buffer = new byte[1024];
        String toOut = "";
        BluetoothDataWriter(BluetoothConnectionManager manager) {
            bluetoothConnectionManager = manager;
        }
        BluetoothDataWriter(BluetoothConnectionManager manager, StreamConnection streamConnection) throws IOException {
            this(manager);
            setStreamConnection(streamConnection);
        }
        public void setStreamConnection(StreamConnection streamConnection) throws IOException {
            this.streamConnection = streamConnection;
            outputStream = streamConnection.openOutputStream();
        }

        public void output(String toOutput) {
            toOut = toOutput;
        }

        public void close() throws IOException {
            outputStream.close();
        }

        @Override
        public void run() {
            try {
                outputStream.write(toOut.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                bluetoothConnectionManager.onWriteFinished(false);
            }
            bluetoothConnectionManager.onWriteFinished(true);
        }
    }
}
