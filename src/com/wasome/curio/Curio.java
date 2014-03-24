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

package com.wasome.curio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;

public class Curio extends Game {

    public static final int GAME_WIDTH = 640;
    public static final int GAME_HEIGHT = 480;
    
    @Override
    public void create() {
        TitleScreen titleScreen = new TitleScreen(this);
        setScreen(titleScreen);
    }
    
    public static void main(String[] args) {
        // Disable power of two texture requirements
        Texture.setEnforcePotImages(false);
        
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Curio";
        cfg.width = 640;
        cfg.height = 480;
        cfg.useGL20 = false;
        cfg.resizable = false;
        cfg.vSyncEnabled = true;
        new LwjglApplication(new Curio(), cfg);
    }

}
