package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.wasome.curio.components.Velocity;

public class InputSystem extends IntervalEntitySystem
    implements InputProcessor {
    
    @Mapper ComponentMapper<Velocity> velocityMapper;
    private Entity player;

    public InputSystem() {
        super(Aspect.getEmpty(), 50);
    }
    
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        player = world.getManager(TagManager.class).getEntity("PLAYER");
    }

    @Override
    public boolean keyDown(int keycode) {
        if (player != null) {
            Velocity v = velocityMapper.get(player);
            
            if (keycode == Keys.W) {
                v.setVelY(1);
            }
            
            if (keycode == Keys.S) {
                v.setVelY(-1);
            }
            
            if (keycode == Keys.A) {
                v.setVelX(-1);
            }
            
            if (keycode == Keys.D) {
                v.setVelX(1);
            }

        }
        
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (player != null) {
            Velocity v = velocityMapper.get(player);
            
            if ((keycode == Keys.W && !Gdx.input.isKeyPressed(Keys.S)) ||
                    (keycode == Keys.S && !Gdx.input.isKeyPressed(Keys.W))) {
                
                v.setVelY(0);
            }
            
            if ((keycode == Keys.A && !Gdx.input.isKeyPressed(Keys.D)) ||
                    (keycode == Keys.D && !Gdx.input.isKeyPressed(Keys.A))) {
                
                v.setVelX(0);
            }
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
