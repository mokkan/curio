package com.wasome.curio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Curio extends Game {

    private GameScreen gameScreen;
    
    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }
    
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Curio";
        cfg.width = 640;
        cfg.height = 480;
        cfg.useGL20 = false;
        cfg.resizable = false;
        new LwjglApplication(new Curio(), cfg);
    }

}
