package com.admin.vwm.game.states;

import com.google.gson.Gson;

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
    protected Map<Integer, Map<Integer, Map<Integer, String>>> cueMap = new HashMap<>();
    protected Boolean platformLocationInitialized = false;

    public static ProjectManager newProject(String projectName, String pathName) {
        ProjectManager toRet = new ProjectManager();
        if (!(pathName.charAt(0)=='*')) {
            toRet.file = openFile(pathName);
            try {
//                if (!toRet.file.createNewFile()) {
//                    parent.projectName.setText("*File Already Exists: " + pathName);
//                    toRet.init = false;
//                    System.out.println(pathName);
//                    return toRet;
//                }
                boolean r = toRet.file.createNewFile();

                toRet.data.clear();
                toRet.data.put("data", new HashMap<>());
                toRet.data.get("data").put("name", projectName);
                toRet.data.get("data").put("path", pathName);
                System.out.println(toRet.data);

                toRet.init = true;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return toRet;
    }
    public static ProjectManager newProject(String projectName ,String rootName, String projectFilePath) {
        return ProjectManager.newProject(projectName, Paths.get(rootName, projectFilePath).toString());
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

    public void initPlatformLocations(int sessions, int trials, String arenaName) {
        if (!this.init) {return;}
        int session = 0;
        while (session < sessions) {
            data.put(String.valueOf(session), new HashMap<>());
            Map<Integer, Map<Integer, String>> a = new HashMap<>();
            int trial = 0;
            while (trial < trials) {
                a.put(trial, new HashMap<>());
                trial++;
            }
            cueMap.put(session, a);
            session++;
        }
        data.get("data").put("sessions", String.valueOf(sessions));
        data.get("data").put("trials", String.valueOf(trials));
        data.get("data").put("arena", arenaName);
        System.out.println(dataToString());
    }

    public void setDynamicData(int sessionNo, int trialNo, boolean probe,
                               float startX, float startZ,
                               float platX, float platZ, String platShape, float lengthXDiameter, float widthZDiameter,
                               int cueNo, float cueX, float cueY, float cueZ, String cueName) {
        Map<String, String> temp = new HashMap<>();
        temp.put(String.valueOf(trialNo), "probe " + boolYesNo(probe)
                + " start " + startX + " " + startZ
                + " end " + platX + " " + platZ + " " + platShape + " " + lengthXDiameter + " " + widthZDiameter);
        cueMap.get(sessionNo).get(trialNo).put(cueNo, "" + cueX + " " + cueY + " " + cueZ + " \"" + cueName + "\"");
        data.put(String.valueOf(sessionNo), temp);
    }

    public void setStaticData(float scale, float speed, String modelFormat, int noOfSession, int noOfTrials) {
        data.get("data").put("scale", String.valueOf(scale));
        data.get("data").put("speed", String.valueOf(speed));
        data.get("data").put("modelFormat", String.valueOf(modelFormat));
        data.get("data").put("sessions", String.valueOf(noOfSession));
        data.get("data").put("trials", String.valueOf(noOfTrials));
    }

    public String dataToJSON() {
        Gson gson = new Gson();
        return gson.toJson(this.data);
    }

    public String dataToString() {
        String toRet = "VWM/Virtual Water Maze Data File - 1.0.0\n";
        toRet += "#" + this.data.get("data").get("name") + ";\n";
        toRet += "#0;\n";
        toRet += "#Home \"" + this.data.get("data").get("path") + "\";\n";
        toRet += "#Constants\n#no_of_sessions " + this.data.get("data").get("sessions") + ";\n";
        toRet += "#no_of_trials " + this.data.get("data").get("trials") + ";\n;\n";

        toRet += "#Scenes\n";
        toRet += "#1 " + "def arena " + this.data.get("data").get("arena") + " plat rect 0.0 0.0 1.0 1.0 " + ";\n";
        toRet += ";\n";

        toRet += "#Sessions\n";
        for (int i=0, n = Integer.parseInt(this.data.get("data").get("sessions")); i < n; i++) {
            toRet += "#" + (i+1) + "\n";
            toRet += "\t#trials\n";
            for (int j=0, m = Integer.parseInt(this.data.get("data").get("trials")); j < m; j++) {
                toRet += "\t#" + (j+1) + " probe 0 start 0 0 0 scene 1;\n";
            }
            toRet +="\t;";
            toRet +=";\n";
        }
        toRet += ";\n";
        return toRet;
    }
    public String boolYesNo(boolean in) {
        if (in) {return "yes";}
        return "no";
    }

    public void save() {
        if (!platformLocationInitialized) {return;}
        try {
            FileWriter fileWriter = openFileWriter();
            fileWriter.write(this.dataToString());
            System.out.println("hello, " + this.dataToString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
