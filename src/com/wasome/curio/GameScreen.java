/*
 * Curio - A simple puzzle platformer game.
 * Copyright (C) 2014  Michael Swiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
import com.wasome.curio.systems.PlayerMovementSystem;
import com.wasome.curio.systems.RenderingSystem;

public class GameScreen implements Screen {
    
    private AssetManager assets;
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Texture levelBg;
    private World world;
    private RenderingSystem renderingSystem;
    private InputSystem inputSystem;
    private Level level;
    private int camWidth;
    private int camHeight;
    private int zoomFactor;
    private int score = 0;
    private InventoryItem item = null;
    private int levelNum = 1;
    private boolean levelComplete = false;
    private ShapeRenderer shape;
    private final Color winScreenColor = new Color(0.0f, 0.0f, 0.0f, 0.5f);
    private Curio curio;

    public GameScreen(Curio curio) {
        this.curio = curio;
        
        // Create asset manager, set loaders, and load assets
        assets = new AssetManager();
        
        assets.setLoader(
            TiledMap.class,
            new TmxMapLoader(new InternalFileHandleResolver())
        );

        assets.setLoader(
            Animation.class,
            new AnimationLoader(new InternalFileHandleResolver())
        );
        
        loadAssets();
        
        // Load first level
        loadLevel(levelNum);

        // Create camera
        int gfxWidth = Gdx.graphics.getWidth();
        int gfxHeight = Gdx.graphics.getHeight();
        
        zoomFactor = gfxHeight / Curio.GAME_HEIGHT;
        camWidth = gfxWidth - ((zoomFactor - 1) * Curio.GAME_WIDTH);
        camHeight = gfxHeight - ((zoomFactor - 1) * Curio.GAME_HEIGHT);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, camWidth, camHeight);
        
        // Create rendering objects
        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        // Initialize entity system
        initEntityWorld();
        
        // Create entities
        level.createEntities(world);
    }
    
    private void loadAssets() {
        // Load sounds
        assets.load(Resources.SND_DEATH, Sound.class);
        assets.load(Resources.SND_JUMP, Sound.class);
        assets.load(Resources.SND_PICKUP, Sound.class);
        assets.load(Resources.SND_DROP, Sound.class);
        assets.load(Resources.SND_COLLECT, Sound.class);
        assets.load(Resources.SND_DOOR, Sound.class);
        
        // Load the animations
        assets.load(Resources.ANIM_DOOR_OPEN, Animation.class);
        assets.load(Resources.ANIM_COIN, Animation.class);
        assets.load(Resources.ANIM_KEY, Animation.class);
        assets.load(Resources.ANIM_IMP_IDLE, Animation.class);
        assets.load(Resources.ANIM_IMP_WALK, Animation.class);
        assets.load(Resources.ANIM_IMP_JUMP, Animation.class);
        assets.load(Resources.ANIM_IMP_CLIMB, Animation.class);
        assets.load(Resources.ANIM_IMP_FALL, Animation.class);
        assets.load(Resources.ANIM_GOB_IDLE, Animation.class);
        assets.load(Resources.ANIM_GOB_WALK, Animation.class);
        assets.load(Resources.ANIM_GOB_CLIMB, Animation.class);
        
        // Load UI assets
        assets.load(Resources.UI_BACKGROUND, Texture.class);
        assets.load(Resources.UI_FONT, BitmapFont.class);
        assets.load(Resources.UI_DEAD, Texture.class);
        
        // Make sure all of the assets have been loaded before continuing
        assets.finishLoading();
    }
    
    private void loadLevel(int num) {
        // Load the given level file
        String levelFile = "assets/levels/level" + Integer.toString(num) + ".tmx";
        assets.load(levelFile, TiledMap.class);
        assets.finishLoading();
        level = new Level((TiledMap) assets.get(levelFile), assets);

        // Load level background
        String bgFile = level.getBackground();
        assets.load("assets/backgrounds/" + bgFile, Texture.class);
        assets.finishLoading();
        levelBg = assets.get("assets/backgrounds/" + bgFile);
    }
    
    private void initEntityWorld() {
        inputSystem = new InputSystem(this, level);
        renderingSystem = new RenderingSystem(cam);
        
        world = new World();
        
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        
        world.setSystem(renderingSystem);
        world.setSystem(inputSystem);
        world.setSystem(new PlayerMovementSystem(this, assets, level));
        world.setSystem(new GravitySystem());
        world.setSystem(new EnemyPathingSystem(level));
        world.setSystem(new EnemyMovementSystem(level));
        
        world.initialize();
        
        Gdx.input.setInputProcessor(inputSystem);
    }
    
    public void resetLevel() {
        // Reset game variables
        score = 0;
        item = null;
        levelComplete = false;
        
        // Load the level file
        loadLevel(levelNum);
        
        initEntityWorld();
        
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
        
        // Draw the UI background
        drawUiBackground();
        
        // Translate cam for UI/game
        int horizTrans = (camWidth - Curio.GAME_WIDTH) / 2;
        int vertTrans = (camHeight - Curio.GAME_HEIGHT) / 2;
        
        cam.translate(-horizTrans, -vertTrans);
        cam.update();
        
        // Draw the UI
        drawUi(delta);
        
        // Translate for drawing maps and entities
        cam.translate(-16, -48);
        cam.update();

        // Draw the game world
        drawGame();
        
        // Draw dead text if applicable
        if (world.getManager(TagManager.class).getEntity("PLAYER") == null) {
            drawDead();
        }
        
        // Undo our translates
        cam.translate(16 + horizTrans, 48 + vertTrans);
        cam.update();
        
        // Draw victory screen if applicable
        if (levelComplete) {
            drawVictory();
        }
    }
    
    private void drawUiBackground() {
        // Draw the interface background
        Texture uiBg = assets.get(Resources.UI_BACKGROUND, Texture.class);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        
        for (int y = 0; y < Gdx.graphics.getHeight(); y += uiBg.getHeight()) {
            for (int x = 0; x < Gdx.graphics.getWidth(); x += uiBg.getWidth()) {
                batch.draw(uiBg, x, y);
            }
        }
        
        batch.end();
    }
    
    private void drawUi(float delta) {
        BitmapFont font = assets.get(Resources.UI_FONT, BitmapFont.class);
        TextBounds fontBounds;
        float x = 0;
        float y = 0;
        
        // Draw the score
        String scoreStr = "Haul: $" + Integer.toString(score);
        font.getBounds(scoreStr);
        fontBounds = font.getBounds(scoreStr);
        x = (Curio.GAME_WIDTH / 2) - fontBounds.width - 16;
        y = 28;
        
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        font.draw(batch, scoreStr, x, y);
        batch.end();
        
        // Draw the item UI
        String itemStr = "Item: ";
        fontBounds = font.getBounds(itemStr);
        x = (Curio.GAME_WIDTH / 2) + 16;
        y = 28;
        
        batch.begin();
        font.draw(batch, itemStr, x, y);
        batch.end();
        
        if (item != null) {
            AnimationState itemAnim = item.getAnimation();
            itemAnim.update(delta);
            x = (Curio.GAME_WIDTH / 2) + fontBounds.width + 16;
            y = 16;

            batch.begin();
            batch.draw(itemAnim.getCurrentFrame().getTextureRegion(), x, y);
            batch.end();
        } else {
            x = (Curio.GAME_WIDTH / 2) + fontBounds.width + 16;
            y = 28;

            batch.begin();
            font.draw(batch, "(none)", x, y);
            batch.end();
        }
    }
    
    private void drawGame() {
        // Draw level background
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(levelBg, 0, 0);
        batch.end();
        
        // Draw level map
        level.render(cam);
        
        // Draw entities
        renderingSystem.process();
    }
    
    private void drawDead() {
        Texture uiDead = assets.get(Resources.UI_DEAD);
        float textX = Curio.GAME_WIDTH/2 - uiDead.getWidth()/2;
        float textY = Curio.GAME_HEIGHT/2 - uiDead.getHeight();
        batch.begin();
        batch.draw(uiDead, textX, textY);
        batch.end();
    }
    
    private void drawVictory() {
        // Draw the victory screen background box
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        shape.setProjectionMatrix(cam.combined);
        shape.setColor(winScreenColor);
        shape.begin(ShapeType.Filled);
        shape.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shape.end();
        
        Gdx.gl.glDisable(GL10.GL_BLEND);
        
        // Strings to be drawn...
        String completeTxt = "Level "
                           + Integer.toString(levelNum)
                           + " Completed!";
        
        String collectedTxt = "Treasure collected: $"
                         + score
                         + " / $"
                         + level.getTotalTreasure();
        
        String gradeTxt = "Performance: " + getLevelGrade();
        String continueTxt = "Press 'Enter' to continue...";
        
        // Render the victory strings appropriately
        float yOffset = -32;
        float spacing = 16;
        float x = 0;
        float y = 0;
        
        BitmapFont font = assets.get(Resources.UI_FONT, BitmapFont.class);
        TextBounds fontBounds;
        
        batch.begin();
        
        fontBounds = font.getBounds(completeTxt);
        x = Curio.GAME_WIDTH / 2 - fontBounds.width/2;
        y = Curio.GAME_HEIGHT / 2 - fontBounds.height/2 - yOffset;
        yOffset += fontBounds.height + spacing;
        font.draw(batch, completeTxt, x, y);
        
        fontBounds = font.getBounds(collectedTxt);
        x = Curio.GAME_WIDTH / 2 - fontBounds.width/2;
        y = Curio.GAME_HEIGHT / 2 - fontBounds.height/2 - yOffset;
        yOffset += fontBounds.height + spacing;
        font.draw(batch, collectedTxt, x, y);
        
        fontBounds = font.getBounds(gradeTxt);
        x = Curio.GAME_WIDTH / 2 - fontBounds.width/2;
        y = Curio.GAME_HEIGHT / 2 - fontBounds.height/2 - yOffset;
        yOffset += fontBounds.height + (spacing * 2);
        font.draw(batch, gradeTxt, x, y);
        
        fontBounds = font.getBounds(continueTxt);
        x = Curio.GAME_WIDTH / 2 - fontBounds.width/2;
        y = Curio.GAME_HEIGHT / 2 - fontBounds.height/2 - yOffset;
        font.draw(batch, continueTxt, x, y);
        
        batch.end();
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
        levelNum += 1;

        String levelFile = "assets/levels/level"
                         + Integer.toString(levelNum)
                         + ".tmx";

        // Check if the next level exists, if not go to title
        FileHandle fh = new FileHandle(levelFile);
        if (!fh.exists()) {
            goToTitle();
            return;
        }

        resetLevel();
    }
    
    public boolean getLevelComplete() {
        return levelComplete;
    }
    
    public void setLevelComplete(boolean levelComplete) {
        EnemyMovementSystem ems = world.getSystem(EnemyMovementSystem.class);
        if (ems != null) {
            ems.stopAnimations();
            world.deleteSystem(ems);
        }
        this.levelComplete = levelComplete;
    }
    
    public AssetManager getAssetManager() {
        return assets;
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
    
    public void goToTitle() {
        Gdx.input.setInputProcessor(null);
        curio.setScreen(new TitleScreen(curio));
    }
    
    @Override
    public void resize(int width, int height) {}
    
    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}

}
