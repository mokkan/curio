/*
 * Curio - A simple puzzle platformer game.
 * Copyright (C) 2014  Michael Swiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.wasome.curio.sprites;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;

public class Animation {
    
    private Texture sheet;
    private ArrayList<Frame> frames;
    private int width;
    private int height;
    private String path;
    
    public Animation(Texture sheet, int width, int height, String path) {
        this.sheet = sheet;
        this.width = width;
        this.height = height;
        this.path = path;
        frames = new ArrayList<Frame>();
    }
    
    public void addFrame(int x, int y, float time) {
        frames.add(new Frame(sheet, x, y, width, height, time));
    }
    
    public ArrayList<Frame> getFrames() {
        return frames;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public String getPath() {
        return path;
    }
    
}
