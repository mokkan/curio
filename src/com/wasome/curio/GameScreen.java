package com.wasome.curio;

import com.artemis.World;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen implements Screen {
    
    private AssetManager assetManager;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;
    private World world;

    public GameScreen(Curio game) {
        assetManager = new AssetManager();
        
        // Set the tile map loader for the asset manager
        assetManager.setLoader(
                TiledMap.class,
                new TmxMapLoader(new InternalFileHandleResolver())
        );
        
        // Load level 1
        assetManager.load("assets/levels/level1.tmx", TiledMap.class);

        // Create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);
        camera.translate(-16, -48);
        camera.update();
        
        world = new World();
        world.initialize();
    }
    
    public void update() {
        world.process();
    }
    

    @Override
    public void render(float delta) {
        // Make sure assets are loaded
        if (!assetManager.update()) {
            return;
        }
        
        if (mapRenderer == null) {
            System.out.println("Test");
            map = assetManager.get("assets/levels/level1.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
            mapRenderer.setView(camera);
        }
        
        update();
        
        mapRenderer.render();
    }
    
    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void show() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        
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

}
