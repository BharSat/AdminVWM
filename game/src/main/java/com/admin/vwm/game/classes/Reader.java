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

    public String stringToData(String dataString) {
        Map<String, Map<String, String>> tempData = new HashMap<>();
        String mode = "startH";
        String subMode;
        int temp1 = 0;
        int temp2 = 0;
        String tempStr1 = "";
        String tempStr2 = "";
        List<String> tempList = new ArrayList<>();
        for (char c: dataString.toCharArray()) {
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
                            return "Invalid Header: " + tempStr1;
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
                                    return "Error: Unexpected Character '" + c + "'";
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
                                default:
                                    subMode = "item";
                            }
                            break;
                        case "item":
                            if (c==' ') {
                                subMode = "newItem:";
                                tempList.add(tempStr1);
                                tempStr1 = "";
                                break;
                            }
                            tempStr1 += c;
                    }
                    break;
                default:
                    return "Logic Error: Incorrect mode " + mode;
            }
        }
        return "";
    }

}
