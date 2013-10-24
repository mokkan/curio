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
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;

public class InputSystem extends IntervalEntitySystem
    implements InputProcessor {
    
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    
    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Creature> creatureMapper;
    @Mapper ComponentMapper<Size> sizeMapper;
    @Mapper ComponentMapper<Appearance> appearanceMapper;
    private Entity player;
    private Level level;
    private int lastDir = DIR_LEFT;

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
            Appearance appearance = appearanceMapper.get(player);
            
            int status = creature.getStatus();
            int tileW = level.getTileWidth();
            int tileH = level.getTileHeight();
            
            int tileX = (int) p.getX() / tileW;
            int tileY = (int) p.getY() / tileH;
            int tileBot = (int) (p.getY() - (size.getHeight() / 2)) / tileH;
            
            int newDir = 0;
            
            if (keycode == Keys.LEFT) {
                v.setX(-1);
                newDir = DIR_LEFT;
            }
            
            if (keycode == Keys.RIGHT) {
                v.setX(1);
                newDir = DIR_RIGHT;
            }
            
            if (newDir != 0 && lastDir != newDir) {
                creature.getAnimation(Creature.STATUS_IDLE).flip(true, false);
                creature.getAnimation(Creature.STATUS_WALKING).flip(true, false);
                creature.getAnimation(Creature.STATUS_JUMPING).flip(true, false);
            }
            
            lastDir = newDir != 0 ? newDir : lastDir;

            if (keycode == Keys.UP && level.isCellLadder(tileX, tileBot)) {
                v.setX(0);
                v.setY(1);
                p.setX(((int) p.getX() / 16) * 16 + 8);

                if (status != Creature.STATUS_CLIMBING) {
                    creature.setStatus(Creature.STATUS_CLIMBING);
                    appearance.setAnimation(creature.getCurrentAnimation());
                } else {
                    creature.getCurrentAnimation().resume();
                }
            }
            
            if (keycode == Keys.DOWN && (level.isCellLadder(tileX, tileY - 1)
                    || level.isCellLadder(tileX, tileY))) {
                
                v.setX(0);
                v.setY(-1);
                p.setX(((int) p.getX() / 16) * 16 + 8);
                
                if (status != Creature.STATUS_CLIMBING) {
                    creature.setStatus(Creature.STATUS_CLIMBING);
                    appearance.setAnimation(creature.getCurrentAnimation());
                } else {
                    creature.getCurrentAnimation().resume();
                }
            }
            
            if (keycode == Keys.SPACE && status != Creature.STATUS_JUMPING
                    && status != Creature.STATUS_CLIMBING) {
                
                creature.setStatus(Creature.STATUS_JUMPING);
                v.setY(3.0f);
                appearance.setAnimation(creature.getCurrentAnimation());
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
                    
                    creature.getCurrentAnimation().pause();
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
