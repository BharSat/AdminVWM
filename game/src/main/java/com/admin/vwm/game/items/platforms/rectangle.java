package com.admin.vwm.game.items.platforms;

import com.admin.vwm.game.items.Platform;
import com.jme3.math.Vector3f;

public class rectangle implements Platform {
    Vector3f centre;
    float xLen;
    float yLen;
    public rectangle(Vector3f centre, int xLen, int yLen) {
        setLocation(centre, xLen, yLen);
    }
    public rectangle(Vector3f start, Vector3f end) {
        setLocation(start, end);
    }

    public void setLocation(Vector3f centre, int xLen, int yLen) {
        this.centre = centre;
        this.xLen = xLen;
        this.yLen = yLen;
    }
    public void setLocation(Vector3f start, Vector3f end) {
        this.centre = new Vector3f((start.x+end.x)/2, 0f, (start.z+end.z)/2);
        this.xLen = end.x-start.x;
        this.yLen = end.z-start.z;

    }

    @Override
    public String asString() {
        return "rect: "+this.centre.x+" "+this.centre.z+","+this.xLen+","+this.yLen;
    }

    @Override
    public void fromString(String toParse) {
        char c;
        for (int  i= 0, n=toParse.length();i <n;i++) {
            c = toParse.charAt(i);
        }
    }
}
