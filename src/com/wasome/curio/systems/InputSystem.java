package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.wasome.curio.GameScreen;
import com.wasome.curio.InventoryItem;
import com.wasome.curio.Level;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Item;
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
    @Mapper ComponentMapper<Item> itemMapper;
    private ImmutableBag<Entity> itemEntities;
    private Entity player;
    private Level level;
    private int lastDir = DIR_LEFT;
    private GameScreen game;

    public InputSystem(GameScreen game, Level level) {
        super(Aspect.getEmpty(), 50);
        this.game = game;
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
            
            itemEntities = world.getManager(GroupManager.class).getEntities("ITEM");
            
            // use item
            if (keycode == Keys.SHIFT_LEFT) {                

            }
            
            // action/grab item
            if (keycode == Keys.CONTROL_LEFT) {
                Position p1 = positionMapper.get(player);
                Size s1 = sizeMapper.get(player);
                Position p2;
                Size s2;

                for (int i = 0; i < itemEntities.size(); i++) {
                    Entity itemEntity = itemEntities.get(i);
                    p2 = positionMapper.get(itemEntity);
                    s2 = sizeMapper.get(itemEntity);
                    Appearance itemApp = appearanceMapper.get(itemEntity);

                    if (MovementSystem.checkCollision(p1, s1, p2, s2)) {
                        Item item = itemMapper.get(itemEntity);
                        String itemType = item.getType();
                        InventoryItem oldItem = game.getItem();
                        
                        if (oldItem != null) {
                            level.addItem(
                                world,
                                oldItem.getAnimationPath(),
                                oldItem.getType(),
                                (int)p2.getX() / level.getTileWidth(),
                                (int)p2.getY() / level.getTileHeight()
                            );
                        }
                        
                        game.setItem(new InventoryItem(itemType, itemApp.getAnimation().getRaw()));
                        
                        itemEntity.deleteFromWorld();
                        
                        return true;
                    }
                }
            }
            
            // drop item
            if (keycode == Keys.ALT_LEFT) {
                InventoryItem item = game.getItem();
                
                if (item == null) {
                    return true;
                }
                
                Position p1 = positionMapper.get(player);
                Size s1 = sizeMapper.get(player);
                Position p2;
                Size s2;

                for (int i = 0; i < itemEntities.size(); i++) {
                    Entity itemEntity = itemEntities.get(i);
                    p2 = positionMapper.get(itemEntity);
                    s2 = sizeMapper.get(itemEntity);
                    if (MovementSystem.checkCollision(p1, s1, p2, s2)) {
                        return true;
                    }
                }
                level.addItem(
                    world,
                    item.getAnimationPath(),
                    item.getType(),
                    (int)p1.getX() / level.getTileWidth(),
                    (int)p1.getY() / level.getTileHeight()
                );
                
                game.setItem(null);
                
            }
        }
        
        return true;
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
