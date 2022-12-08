package com.admin.vwm.game.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reader {
    ProjectManager parent;
    public Reader (ProjectManager parent) {
        this.parent = parent;
    }

    public Loop stringToData(String dataString) {
        Map<String, Map<String, String>> tempData = new HashMap<>();
        String mode = "startH",
                subMode;
        int temp1 = 0,
                temp2 = 0;
        String tempStr1 = "",
                tempStr2 = "";
        List<String> tempList = new ArrayList<>();
        Loop bodyLoop = new Loop("Body", new ArrayList<>()),
                curLoop = new Loop("", new ArrayList<>(0)),
                parentLoop = bodyLoop;
        boolean slash = false, comment = false;
        for (char c: dataString.toCharArray()) {
            if (c=='/') {
                comment = slash;
                slash = !slash;
                if (comment){tempStr2=mode;mode="false";}
            } else if (comment && c=='\n') {
                comment = false;
                mode = tempStr2;
                tempStr2 = "";
            }
            switch (mode) {
                case "startH":
                    temp1++;
                    tempStr1+=c;
                    if (temp1==35) {
                        if (tempStr1.equals("VWM/Virtual Water Maze Data File - ")) {
                            mode = "startV";
                            temp1 = 0;
                            tempStr1 = "";
                        }
                        else {
                            return new Loop("Invalid Header: " + tempStr1, new ArrayList<>());
                        }
                    }
                    break;
                case "startV":
                    temp1++;
                    tempStr1+=c;
                    if (temp1==5) {
                        mode = "body";
                        temp1 = 0;
                        tempStr1 = "";
                    }
                    break;
                case "body":
                    subMode = "newA";
                    switch (subMode) {
                        case "newA":
                            switch (c) {
                                case '#':
                                    subMode = "loopStart";
                                    break;
                                case ' ':
                                    subMode = "newItem";
                                    break;
                                case '\n':
                                case '\t':
                                    break;
                                default:
                                    return new Loop("Error: Unexpected Character '" + c + "'", new ArrayList<>());
                            }
                            break;
                        case "newItem":
                            switch (c) {
                                case '#':
                                    subMode = "loopStart";
                                    break;
                                case ' ':
                                case '\n':
                                case '\t':
                                    break;
                                case ';':
                                    curLoop = parentLoop;
                                    parentLoop = parentLoop.parent();
                                default:
                                    subMode = "item";
                            }
                            break;
                        case "item":
                            if (c==' ') {
                                subMode = "newItem";
                                tempList.add(tempStr1);
                                tempStr1 = "";
                                break;
                            } else if (c==';') {
                                subMode = "newItem";
                                tempList.add(tempStr1);
                                tempStr1 = "";
                                curLoop.addArgs(tempList);
                                tempList.clear();
                                curLoop = parentLoop;
                                parentLoop = parentLoop.parent();
                            }
                            tempStr1 += c;
                            break;
                        case "loopStart":
                            if (c==' ') {
                                parentLoop = curLoop;
                                curLoop = new Loop(tempStr1, new ArrayList<>());
                                parentLoop.addChild(curLoop);
                                subMode = "newItem";
                                tempStr1 = "";
                                if (curLoop.name.equals("End")) {
                                    mode = "";
                                    break;
                                }
                            }
                            tempStr1 += c;
                    }
                    break;
                case "":
                case "false":
                    break;
                default:
                    return new Loop("Logic Error: Incorrect mode " + mode, new ArrayList<>());
            }
        }
        return bodyLoop;
    }

    public Map<String, Map<String, String>> rawToGameData (Loop mainLoop) {
        if (!mainLoop.name.equals("Body")) {
            return null;
        }
        Map<String, Map<String, String>> toRet = new HashMap<>();

        toRet.put("data", new HashMap<>());

        String mode = "start1";
        for (Loop loop: mainLoop.children) {
            switch (mode) {
                case "start1":
                    toRet.get("data").put("name", loop.name);
                    mode = "start2";
                    break;
                case "start2":
                    toRet.get("data").put("name", toRet.get("data").get("name")+loop.name);
                    mode = "start3";
                    break;
                case "start3":
                    toRet.get("data").put("path", loop.getArg(0));
                    mode = "start4";
                    break;
                case "start4":

            }
        }
        return toRet;
    }

}
