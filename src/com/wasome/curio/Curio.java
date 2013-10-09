package com.wasome.curio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Curio extends Game {

    private GameScreen gameScreen;
    
    @Override
    public void create() {
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }
    
    public static void main(String[] args) {
        new LwjglApplication(new Curio(), "Curio", 640, 480, false);
    }

}
