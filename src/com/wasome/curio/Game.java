package com.wasome.curio;

import com.artemis.World;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Game implements ApplicationListener {
	private AssetManager assetManager;
	private TiledMap map;
	private World world; 

    @Override
    public void create() {
    	assetManager = new AssetManager();
    	
    	// Set the tile map loader for the asset manager
    	assetManager.setLoader(
                TiledMap.class,
                new TmxMapLoader(new InternalFileHandleResolver())
        );
    	
    	// Load level 1
    	assetManager.load("assets/levels/level1.tmx", TiledMap.class);
    	
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
        // Make sure assets are loaded
        if (!assetManager.update()) {
            return;
        }
        
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
