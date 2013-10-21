package com.wasome.curio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;

public class Curio extends Game {

    private GameScreen gameScreen;
    
    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }
    
    public static void main(String[] args) {
        // Disable power of two texture requirements
        Texture.setEnforcePotImages(false);
        
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Curio";
        cfg.width = 1280;
        cfg.height = 960;
        cfg.useGL20 = false;
        cfg.resizable = false;
        new LwjglApplication(new Curio(), cfg);
    }

}
