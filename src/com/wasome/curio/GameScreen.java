package com.wasome.curio;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.wasome.curio.sprites.Animation;
import com.wasome.curio.sprites.AnimationLoader;
import com.wasome.curio.systems.GravitySystem;
import com.wasome.curio.systems.InputSystem;
import com.wasome.curio.systems.MovementSystem;
import com.wasome.curio.systems.RenderingSystem;

public class GameScreen implements Screen {
    
    private AssetManager assetManager;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private BitmapFont font;
    private TextBounds fontBounds;
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
    private int score = 0;
    final protected static int gameWidth = 640;
    final protected static int gameHeight = 480;

    public GameScreen(Curio game) {
        assetManager = new AssetManager();
        
        // Set the tile map loader for the asset manager
        assetManager.setLoader(
                TiledMap.class,
                new TmxMapLoader(new InternalFileHandleResolver())
        );
        
        // Set the animation loader
        assetManager.setLoader(
                Animation.class,
                new AnimationLoader(new InternalFileHandleResolver())
        );
        
        // Set the font loader
        assetManager.setLoader(
                BitmapFont.class,
                new BitmapFontLoader(new InternalFileHandleResolver())
        );
        
        // Load the font
        String fontFile = "assets/fonts/yacimiento.fnt";
        assetManager.load(fontFile, BitmapFont.class);
        assetManager.finishLoading();
        font = assetManager.get(fontFile, BitmapFont.class);

        // Load the animations
        assetManager.load("assets/sprites/coin.anim", Animation.class);
        assetManager.load("assets/sprites/imp-idle.anim", Animation.class);
        assetManager.load("assets/sprites/imp-walk.anim", Animation.class);
        assetManager.load("assets/sprites/imp-jump.anim", Animation.class);
        assetManager.load("assets/sprites/imp-climb.anim", Animation.class);
        assetManager.finishLoading();
        
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
        level = new Level((TiledMap) assetManager.get(levelFile), assetManager);

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
        inputSystem = new InputSystem(level);
        renderingSystem = new RenderingSystem(cam);
        
        world = new World();
        
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        
        world.setSystem(renderingSystem);
        world.setSystem(inputSystem);
        world.setSystem(new MovementSystem(this, level));
        world.setSystem(new GravitySystem());
        
        world.initialize();
        
        Gdx.input.setInputProcessor(inputSystem);
        
        // Create entities
        level.createEntities(world);
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
        
        String scoreStr = "Haul: $" + Integer.toString(score);
        font.getBounds(scoreStr);
        fontBounds = font.getBounds(scoreStr);
        
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, scoreStr, (gameWidth / 2) - fontBounds.width - 16, 32);
        batch.end();
        
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
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
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
