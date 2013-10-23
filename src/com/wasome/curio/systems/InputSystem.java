package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.wasome.curio.Level;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;

public class InputSystem extends IntervalEntitySystem
    implements InputProcessor {
    
    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Creature> creatureMapper;
    @Mapper ComponentMapper<Size> sizeMapper;
    private Entity player;
    private Level level;

    public InputSystem(Level level) {
        super(Aspect.getEmpty(), 50);
        this.level = level;
    }
    
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        player = world.getManager(TagManager.class).getEntity("PLAYER");
    }

    @Override
    public boolean keyDown(int keycode) {
        if (player != null) {
            Position p = positionMapper.get(player);
            Velocity v = velocityMapper.get(player);
            Creature creature = creatureMapper.get(player);
            Size size = sizeMapper.get(player);
            
            int status = creature.getStatus();
            int tileW = level.getTileWidth();
            int tileH = level.getTileHeight();
            
            int tileX = (int) p.getX() / tileW;
            int tileY = (int) p.getY() / tileH;
            int tileBot = (int) (p.getY() - (size.getHeight() / 2)) / tileH;
            
            if (keycode == Keys.LEFT) {
                v.setX(-1);
            }
            
            if (keycode == Keys.RIGHT) {
                v.setX(1);
            }

            if (keycode == Keys.UP && level.isCellLadder(tileX, tileBot)) {
                v.setX(0);
                v.setY(1);
                p.setX(((int) p.getX() / 16) * 16 + 8);
                creature.setStatus(Creature.STATUS_CLIMBING);
            }
            
            if (keycode == Keys.DOWN && (level.isCellLadder(tileX, tileY - 1)
                    || level.isCellLadder(tileX, tileY))) {
                
                v.setX(0);
                v.setY(-1);
                p.setX(((int) p.getX() / 16) * 16 + 8);
                creature.setStatus(Creature.STATUS_CLIMBING);
            }
            
            if (keycode == Keys.SPACE && status != Creature.STATUS_JUMPING
                    && status != Creature.STATUS_CLIMBING) {
                
                creature.setStatus(Creature.STATUS_JUMPING);
                v.setY(3.0f);
            }
        }
        
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (player != null) {
            Velocity v = velocityMapper.get(player);
            Creature creature = creatureMapper.get(player);

            if ((keycode == Keys.LEFT && !Gdx.input.isKeyPressed(Keys.RIGHT)) ||
                (keycode == Keys.RIGHT && !Gdx.input.isKeyPressed(Keys.LEFT))) {
                
                v.setX(0);
            }
            
            if (((keycode == Keys.UP && !Gdx.input.isKeyPressed(Keys.DOWN)) ||
                (keycode == Keys.DOWN && !Gdx.input.isKeyPressed(Keys.UP)))
                && creature.getStatus() == Creature.STATUS_CLIMBING) {
                    
                    v.setY(0);
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
