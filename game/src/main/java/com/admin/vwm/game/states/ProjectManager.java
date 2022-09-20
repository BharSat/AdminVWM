package com.admin.vwm.game.states;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ProjectManager {
    public File file;
    public Boolean init = false;
    protected Map<String, Map<String, String>> data = new HashMap<>();
    protected Boolean platformLocationInitialized = false;

    public static ProjectManager newProject(GameAppState parent, String projectName, String pathName) {
        ProjectManager toRet = new ProjectManager();
        if (!(pathName.charAt(0)=='*')) {
            toRet.file = openFile(pathName);
            try {
                if (!toRet.file.createNewFile()) {
                    parent.projectName.setText("*File Already Exists: " + pathName);
                    toRet.init = false;
                    return toRet;
                }

                toRet.data.clear();
                Map<String, String> data = new HashMap<>();
                data.put("name", projectName);
                data.put("path", pathName);
                toRet.data.put("data", data);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return toRet;
    }
    public static ProjectManager newProject(GameAppState parent, String projectName ,String rootName, String projectFilePath) {
        return ProjectManager.newProject(parent, projectName, Paths.get(rootName, projectFilePath).toString());
    }

    public static File openFile(String filePath){
        return new File(filePath);
    }

    public static FileWriter openFileWriter(String filePath) throws IOException {
        return new FileWriter(openFile(filePath), true);
    }

    public FileWriter openFileWriter() throws IOException {
        return new FileWriter(this.file, true);
    }

    public FileWriter truncate(Boolean close) throws IOException {
        FileWriter trunc = new FileWriter(this.file, false);
        if (close) {trunc.close(); return trunc;}
        return trunc;

    }

    public void truncate() throws IOException {
        truncate(true);
    }

    public static FileReader openFileReader(String filePath) throws IOException {
        return new FileReader(openFile(filePath));
    }

    public FileReader openFileReader() throws IOException {
        return new FileReader(this.file);
    }

    public void initPlatformLocations(int sessions, int trials) {
        int session = 0;
        while (session < sessions) {
            data.put(String.valueOf(session), new HashMap<>());
            session++;
        }
        data.get("data").put("sessions", String.valueOf(sessions));
        data.get("data").put("trials", String.valueOf(trials));
        System.out.println(data);
    }

    public void setPlatformLocation(int sessionNo, int trialNo, float x1, float x2, float y1, float y2) {
        data.get(String.valueOf(sessionNo)).put(String.valueOf(trialNo), ""+x1+" "+y1+","+x2+" "+y2);
        System.out.print(data);
    }

    public void save() {
        if (!platformLocationInitialized) {return;}
        try {
            FileWriter fileWriter = openFileWriter();
            fileWriter.write();
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
