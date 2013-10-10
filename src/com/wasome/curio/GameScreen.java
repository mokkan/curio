package com.wasome.curio;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen implements Screen {
    
    private AssetManager assetManager;
    private OrthographicCamera cam;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;
    private World world;
    private int camWidth;
    private int camHeight;
    private int zoomFactor;
    final protected static int gameWidth = 640;
    final protected static int gameHeight = 480;

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
        zoomFactor = Gdx.graphics.getHeight() /  gameHeight;
        camWidth = Gdx.graphics.getWidth() - ((zoomFactor - 1) * gameWidth);
        camHeight = Gdx.graphics.getHeight() - ((zoomFactor - 1) * gameHeight);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, camWidth, camHeight);

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
            map = assetManager.get("assets/levels/level1.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        }
        
        update();
        
        // Draw screen background
        // ... TODO
        
        // Draw the UI
        int horizTrans = (camWidth - gameWidth) / 2;
        int vertTrans = (camHeight - gameHeight) / 2;
        
        cam.translate(-horizTrans, -vertTrans);
        cam.update();
        
        // Draw the map and entities
        cam.translate(-16, -48);
        cam.update();
        
        mapRenderer.setView(cam);
        mapRenderer.render();
        
        // Undo our translates
        cam.translate(16 + horizTrans, 48 + vertTrans);
        cam.update();
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
