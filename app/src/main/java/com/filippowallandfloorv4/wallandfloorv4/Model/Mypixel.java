package com.filippowallandfloorv4.wallandfloorv4.Model;

import android.graphics.Color;

/**
 * Created by Filippo on 17/12/2015.
 */
public class Mypixel {
    public int x;
    public int y;
    private boolean visited = false;
    private int color;

    public Mypixel(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
