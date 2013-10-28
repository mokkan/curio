package com.wasome.curio;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.wasome.curio.sprites.Animation;
import com.wasome.curio.sprites.AnimationLoader;
import com.wasome.curio.sprites.AnimationState;
import com.wasome.curio.systems.EnemyMovementSystem;
import com.wasome.curio.systems.EnemyPathingSystem;
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
    private Texture textDead;
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
    private InventoryItem item = null;
    final protected static int gameWidth = 640;
    final protected static int gameHeight = 480;
    private int levelNum = 1;
    private boolean levelComplete = false;
    private ShapeRenderer shape;
    private Color winScreenColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);

    public GameScreen(Curio game) {
        // Create shape renderer
        shape = new ShapeRenderer();
        
        // Create asset manager
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

        // Load sounds
        assetManager.load("assets/sounds/creature.wav", Sound.class);
        assetManager.load("assets/sounds/jump.wav", Sound.class);
        assetManager.load("assets/sounds/collect.wav", Sound.class);
        assetManager.load("assets/sounds/item-pickup.wav", Sound.class);
        assetManager.load("assets/sounds/item-drop.wav", Sound.class);
        assetManager.load("assets/sounds/door-open.wav", Sound.class);
        assetManager.finishLoading();
        
        // Load the font
        String fontFile = "assets/fonts/yacimiento.fnt";
        assetManager.load(fontFile, BitmapFont.class);
        assetManager.finishLoading();
        font = assetManager.get(fontFile, BitmapFont.class);

        // Load the animations
        assetManager.load("assets/sprites/door-open.anim", Animation.class);
        assetManager.load("assets/sprites/coin.anim", Animation.class);
        assetManager.load("assets/sprites/key.anim", Animation.class);
        assetManager.load("assets/sprites/imp-idle.anim", Animation.class);
        assetManager.load("assets/sprites/imp-walk.anim", Animation.class);
        assetManager.load("assets/sprites/imp-jump.anim", Animation.class);
        assetManager.load("assets/sprites/imp-climb.anim", Animation.class);
        assetManager.load("assets/sprites/imp-fall.anim", Animation.class);
        assetManager.load("assets/sprites/enemy-idle.anim", Animation.class);
        assetManager.load("assets/sprites/enemy-walk.anim", Animation.class);
        assetManager.load("assets/sprites/enemy-climb.anim", Animation.class);
        assetManager.finishLoading();
        
        // Load interface background
        String uiBgFile = "assets/ui/background.png";
        assetManager.load(uiBgFile, Texture.class);
        assetManager.finishLoading();
        uiBg = assetManager.get(uiBgFile, Texture.class);
        uiBgWidth = uiBg.getWidth();
        uiBgHeight = uiBg.getHeight();
        
        // Load level 1
        String levelFile = "assets/levels/level" + Integer.toString(levelNum) + ".tmx";
        assetManager.load(levelFile, TiledMap.class);
        assetManager.finishLoading();
        level = new Level((TiledMap) assetManager.get(levelFile), assetManager);

        // Load background
        String bgFile = level.getBackground();
        assetManager.load("assets/backgrounds/" + bgFile, Texture.class);
        assetManager.finishLoading();
        levelBg = assetManager.get("assets/backgrounds/" + bgFile);
        
        // Load died text
        String deadFile = "assets/sprites/dead-text.png";
        assetManager.load(deadFile, Texture.class);
        assetManager.finishLoading();
        textDead = assetManager.get(deadFile);

        // Create camera
        zoomFactor = Gdx.graphics.getHeight() /  gameHeight;
        camWidth = Gdx.graphics.getWidth() - ((zoomFactor - 1) * gameWidth);
        camHeight = Gdx.graphics.getHeight() - ((zoomFactor - 1) * gameHeight);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, camWidth, camHeight);
        
        // Create sprite batch
        batch = new SpriteBatch();

        // Set up entity system
        inputSystem = new InputSystem(this, level);
        renderingSystem = new RenderingSystem(cam);
        
        world = new World();
        
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        
        world.setSystem(renderingSystem);
        world.setSystem(inputSystem);
        world.setSystem(new MovementSystem(this, assetManager, level));
        world.setSystem(new GravitySystem());
        world.setSystem(new EnemyPathingSystem(level));
        world.setSystem(new EnemyMovementSystem(level));
        
        world.initialize();
        
        Gdx.input.setInputProcessor(inputSystem);
        
        // Create entities
        level.createEntities(world);
    }
    
    public void resetLevel() {
        item = null;
        levelComplete = false;
        String levelFile = "assets/levels/level" + Integer.toString(levelNum) + ".tmx";
        level = new Level((TiledMap) assetManager.get(levelFile), assetManager);
        
        String bgFile = level.getBackground();
        assetManager.load("assets/backgrounds/" + bgFile, Texture.class);
        assetManager.finishLoading();
        levelBg = assetManager.get("assets/backgrounds/" + bgFile);
        
        inputSystem = new InputSystem(this, level);
        renderingSystem = new RenderingSystem(cam);
        
        world = new World();
        
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        
        world.setSystem(renderingSystem);
        world.setSystem(inputSystem);
        world.setSystem(new MovementSystem(this, assetManager, level));
        world.setSystem(new GravitySystem());
        world.setSystem(new EnemyPathingSystem(level));
        world.setSystem(new EnemyMovementSystem(level));
        
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
        font.draw(batch, scoreStr, (gameWidth / 2) - fontBounds.width - 16, 28);
        batch.end();
        
        String itemStr = "Item: ";
        fontBounds = font.getBounds(itemStr);
        
        batch.begin();
        font.draw(batch, itemStr, (gameWidth / 2) + 16, 28);
        batch.end();
        
        if (item != null) {
            AnimationState itemAnim = item.getAnimation();
            itemAnim.update(delta);
            batch.begin();
            batch.draw(itemAnim.getCurrentFrame(), (gameWidth / 2) + fontBounds.width + 16, 16);
            batch.end();
        } else {
            batch.begin();
            font.draw(batch, "(none)", (gameWidth / 2) + fontBounds.width + 16, 28);
            batch.end();
        }
        
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
        
        // Draw dead text if applicable
        if (world.getManager(TagManager.class).getEntity("PLAYER") == null) {
            float textX = gameWidth/2 - textDead.getWidth()/2;
            float textY = gameHeight/2 - textDead.getHeight();
            batch.begin();
            batch.draw(textDead, textX, textY);
            batch.end();
        }
        
        // Undo our translates
        cam.translate(16 + horizTrans, 48 + vertTrans);
        cam.update();
        
        // Draw victory screen if applicable
        if (levelComplete) {
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            shape.setProjectionMatrix(cam.combined);
            shape.setColor(winScreenColor);
            shape.begin(ShapeType.Filled);
            shape.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shape.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);
            
            String completeTxt = "Level " + Integer.toString(levelNum)
                               + " Completed!";
            
            String collected = "Treasure collected: $"
                             + score + " / $" + level.getTotalTreasure();
            
            String levelGrade = "Performance: " + getLevelGrade();
            
            String continueTxt = "Press 'Enter' to continue...";
            
            float yOffset = -32;
            
            batch.begin();
            
            fontBounds = font.getBounds(completeTxt);
            font.draw(batch, completeTxt, (gameWidth / 2) - fontBounds.width/2, gameHeight/2 - fontBounds.height/2 - yOffset);
            yOffset += fontBounds.height + 16;
            
            fontBounds = font.getBounds(collected);
            font.draw(batch, collected, (gameWidth / 2) - fontBounds.width/2, gameHeight/2 - fontBounds.height/2 - yOffset);
            yOffset += fontBounds.height + 16;
            
            fontBounds = font.getBounds(levelGrade);
            font.draw(batch, levelGrade, (gameWidth / 2) - fontBounds.width/2, gameHeight/2 - fontBounds.height/2 - yOffset);
            yOffset += fontBounds.height + 32;
            
            fontBounds = font.getBounds(continueTxt);
            font.draw(batch, continueTxt, (gameWidth / 2) - fontBounds.width/2, gameHeight/2 - fontBounds.height/2 - yOffset);
            
            batch.end();
        }
    }
    
    private String getLevelGrade() {
        float ratio = (float) score / level.getTotalTreasure();

        if (ratio > 0.99) {
            return "A++ SUPERSTAR";
        } else if (ratio > 0.97) {
            return "A+";
        } else if (ratio > 0.93) {
            return "A";
        } else if (ratio > 0.90) {
            return "A-";
        } else if (ratio > 0.87) {
            return "B+";
        } else if (ratio > 0.83) {
            return "B";
        } else if (ratio > 0.80) {
            return "B-";
        } else if (ratio > 0.77) {
            return "C+";
        } else if (ratio > 0.73) {
            return "C";
        } else if (ratio > 0.70) {
            return "C-";
        } else if (ratio > 0.67) {
            return "D+";
        } else if (ratio > 0.63) {
            return "D";
        } else if (ratio > 0.60) {
            return "D-";
        } else {
            return "F";
        }
    }
    
    public void nextLevel() {
        score = 0;
        levelNum += 1;
        String levelFile = "assets/levels/level" + Integer.toString(levelNum) + ".tmx";
        FileHandle fh = new FileHandle(levelFile);
        if (!fh.exists()) {
            levelNum -= 1;
            return;
        }
        
        assetManager.load(levelFile, TiledMap.class);
        assetManager.finishLoading();
        resetLevel();
    }
    
    public boolean getLevelComplete() {
        return levelComplete;
    }
    
    public void setLevelComplete(boolean levelComplete) {
        EnemyMovementSystem es = world.getSystem(EnemyMovementSystem.class);
        if (es != null) {
            es.stopAnimations();
            world.deleteSystem(es);
        }
        this.levelComplete = levelComplete;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public InventoryItem getItem() {
        return item;
    }
    
    public void setItem(InventoryItem item) {
        this.item = item;
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
