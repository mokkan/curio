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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TitleScreen implements Screen {

    private int camWidth;
    private int camHeight;
    private int zoomFactor;
    private OrthographicCamera cam;
    private Texture background;
    private Texture foreground;
    private SpriteBatch batch;
    private Curio curio;
    
    public TitleScreen(Curio game) {
        curio = game;
        
        // Create sprite batch
        batch = new SpriteBatch();
        
        // Load textures
        background = new Texture(Gdx.files.internal("assets/ui/background.png"));
        foreground = new Texture(Gdx.files.internal("assets/ui/title.png"));
        
        // Create camera
        int gfxWidth = Gdx.graphics.getWidth();
        int gfxHeight = Gdx.graphics.getHeight();

        zoomFactor = gfxHeight / Curio.GAME_HEIGHT;
        camWidth = gfxWidth - ((zoomFactor - 1) * Curio.GAME_WIDTH);
        camHeight = gfxHeight - ((zoomFactor - 1) * Curio.GAME_HEIGHT);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, camWidth, camHeight);
    }

    private void update() {
        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            curio.setScreen(new GameScreen(curio));
        }
        
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }
    
    @Override
    public void render(float delta) {
        update();
        
        // Clear the screen
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        // Draw the interface background
        int uiBgWidth = background.getWidth();
        int uiBgHeight = background.getHeight();
        
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        
        for (int y = 0; y < Gdx.graphics.getHeight(); y += uiBgHeight) {
            for (int x = 0; x < Gdx.graphics.getWidth(); x += uiBgWidth) {
                batch.draw(background, x, y);
            }
        }
        
        batch.end();
        
        // Draw the UI
        int horizTrans = (camWidth - Curio.GAME_WIDTH) / 2;
        int vertTrans = (camHeight - Curio.GAME_HEIGHT) / 2;
        
        float textX = Curio.GAME_WIDTH/2 - foreground.getWidth()/2;
        float textY = Curio.GAME_HEIGHT/2 - foreground.getHeight()/2;

        cam.translate(-horizTrans, -vertTrans);
        cam.update();
        
        batch.setProjectionMatrix(cam.combined);

        batch.begin();
        batch.draw(foreground, textX, textY);
        batch.end();
        
        // Undo our translates
        cam.translate(horizTrans, vertTrans);
        cam.update();
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
