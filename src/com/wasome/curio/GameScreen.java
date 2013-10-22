package com.wasome.curio;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.systems.GravitySystem;
import com.wasome.curio.systems.InputSystem;
import com.wasome.curio.systems.MovementSystem;
import com.wasome.curio.systems.RenderingSystem;

public class GameScreen implements Screen {
    
    private AssetManager assetManager;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Texture uiBg;
    private Texture levelBg;
    private World world;
    private RenderingSystem renderingSystem;
    private InputSystem inputSystem;
    private Level level;
    private int uiBgWidth;
    private int uiBgHeight;
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
        
        // Load interface background
        String uiBgFile = "assets/ui/background.png";
        assetManager.load(uiBgFile, Texture.class);
        assetManager.finishLoading();
        uiBg = assetManager.get(uiBgFile, Texture.class);
        uiBgWidth = uiBg.getWidth();
        uiBgHeight = uiBg.getHeight();
        
        // Load level 1
        String levelFile = "assets/levels/level1.tmx";
        assetManager.load(levelFile, TiledMap.class);
        assetManager.finishLoading();
        level = new Level((TiledMap) assetManager.get(levelFile));

        // Load background
        String bgFile = level.getBackground();
        assetManager.load("assets/backgrounds/" + bgFile, Texture.class);
        assetManager.finishLoading();
        levelBg = assetManager.get("assets/backgrounds/" + bgFile);

        // Create camera
        zoomFactor = Gdx.graphics.getHeight() /  gameHeight;
        camWidth = Gdx.graphics.getWidth() - ((zoomFactor - 1) * gameWidth);
        camHeight = Gdx.graphics.getHeight() - ((zoomFactor - 1) * gameHeight);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, camWidth, camHeight);
        
        // Create sprite batch
        batch = new SpriteBatch();

        // Set up entity system
        inputSystem = new InputSystem();
        renderingSystem = new RenderingSystem(cam);
        
        world = new World();
        
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        
        world.setSystem(renderingSystem);
        world.setSystem(inputSystem);
        world.setSystem(new MovementSystem(level));
        world.setSystem(new GravitySystem());
        
        world.initialize();
        
        initPlayer();
        
        Gdx.input.setInputProcessor(inputSystem);
    }
    
    private void initPlayer() {
        Entity e = world.createEntity();
        e.addComponent(new Position(136, 136));
        e.addComponent(new Size(16, 16));
        e.addComponent(new Velocity(0, 0));
        e.addComponent(new Gravity(-3.0f, -0.25f));
        e.addComponent(new Creature());
        
        world.getManager(TagManager.class).register("PLAYER", e);
        world.addEntity(e);
    }

    @Override
    public void render(float delta) {
        // Process and draw entities 
        world.setDelta(delta * 1000);
        world.process();

        // Clear the screen
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        // Draw the interface background
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        
        for (int y = 0; y < Gdx.graphics.getHeight(); y += uiBgHeight) {
            for (int x = 0; x < Gdx.graphics.getWidth(); x += uiBgWidth) {
                batch.draw(uiBg, x, y);
            }
        }
        
        batch.end();
        
        // Draw the UI
        int horizTrans = (camWidth - gameWidth) / 2;
        int vertTrans = (camHeight - gameHeight) / 2;
        
        cam.translate(-horizTrans, -vertTrans);
        cam.update();
        
        // Translate for drawing maps and entities
        cam.translate(-16, -48);
        cam.update();

        // Draw level background
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(levelBg, 0, 0);
        batch.end();
        
        // Draw level map
        level.render(cam);
        
        // Draw entities
        renderingSystem.process();
        
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
