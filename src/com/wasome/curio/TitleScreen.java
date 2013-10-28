package com.wasome.curio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TitleScreen implements Screen {

    final protected static int gameWidth = 640;
    final protected static int gameHeight = 480;
    private int camWidth;
    private int camHeight;
    private int zoomFactor;
    private int uiBgWidth;
    private int uiBgHeight;
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
        
        uiBgWidth = background.getWidth();
        uiBgHeight = background.getHeight();
        
        // Create camera
        zoomFactor = Gdx.graphics.getHeight() /  gameHeight;
        camWidth = Gdx.graphics.getWidth() - ((zoomFactor - 1) * gameWidth);
        camHeight = Gdx.graphics.getHeight() - ((zoomFactor - 1) * gameHeight);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, camWidth, camHeight);
    }
    
    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            curio.setScreen(new GameScreen(curio));
            return;
        }
        
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }
        
        // Draw the interface background
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        
        for (int y = 0; y < Gdx.graphics.getHeight(); y += uiBgHeight) {
            for (int x = 0; x < Gdx.graphics.getWidth(); x += uiBgWidth) {
                batch.draw(background, x, y);
            }
        }
        
        batch.end();
        
        // Draw the UI
        int horizTrans = (camWidth - gameWidth) / 2;
        int vertTrans = (camHeight - gameHeight) / 2;
        
        cam.translate(-horizTrans, -vertTrans);
        cam.update();
        
        batch.setProjectionMatrix(cam.combined);
        
        float textX = gameWidth/2 - foreground.getWidth()/2;
        float textY = gameHeight/2 - foreground.getHeight()/2;
        batch.begin();
        batch.draw(foreground, textX, textY);
        batch.end();
        
        // Undo our translates
        cam.translate(horizTrans, vertTrans);
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
