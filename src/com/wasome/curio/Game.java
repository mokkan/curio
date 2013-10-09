package com.wasome.curio;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Game implements ApplicationListener {
	private AssetManager assetManager;
	private World world; 

    @Override
    public void create() {
    	assetManager = new AssetManager();
    	
        world = new World();
        world.initialize();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        
    }
    
    public void update() {
    	world.process();
    }
    

    @Override
    public void render() {
        update();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }
    
    public static void main(String[] args) {
        new LwjglApplication(new Game(), "Curio", 640, 480, false);
    }

}
