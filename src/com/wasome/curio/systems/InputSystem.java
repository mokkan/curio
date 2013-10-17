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
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Velocity;

public class InputSystem extends IntervalEntitySystem
    implements InputProcessor {
    
    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Creature> creatureMapper;
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
            Creature creature = creatureMapper.get(player);
            
            if (keycode == Keys.LEFT) {
                v.setX(-1);
            }
            
            if (keycode == Keys.RIGHT) {
                v.setX(1);
            }
            
            if (keycode == Keys.SPACE && creature.getStatus() != Creature.STATUS_JUMPING) {
                v.setY(3.0f);
            }
        }
        
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (player != null) {
            Velocity v = velocityMapper.get(player);

            if ((keycode == Keys.LEFT && !Gdx.input.isKeyPressed(Keys.RIGHT)) ||
                (keycode == Keys.RIGHT && !Gdx.input.isKeyPressed(Keys.LEFT))) {
                
                v.setX(0);
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
