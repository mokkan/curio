package com.wasome.curio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;

public class Curio extends Game {

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
