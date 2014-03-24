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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapProperties;
import com.wasome.curio.Collision;
import com.wasome.curio.GameScreen;
import com.wasome.curio.InventoryItem;
import com.wasome.curio.Level;
import com.wasome.curio.Resources;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Item;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.sprites.Animation;
import com.wasome.curio.sprites.AnimationState;

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

    private AssetManager assets;
    private Entity player;
    private Level level;
    private GameScreen game;
    private Sound jumpSnd;
    private Sound itemPickupSnd;
    private Sound itemDropSnd;
    private Sound doorSnd;

    public InputSystem(GameScreen game, Level level) {
        super(Aspect.getEmpty(), 50);
        this.game = game;
        this.level = level;
        this.assets = game.getAssetManager();
        
        jumpSnd = assets.get(Resources.SND_JUMP, Sound.class);
        itemPickupSnd = assets.get(Resources.SND_PICKUP, Sound.class);
        itemDropSnd = assets.get(Resources.SND_DROP, Sound.class);
        doorSnd = assets.get(Resources.SND_DOOR, Sound.class);
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        player = world.getManager(TagManager.class).getEntity("PLAYER");
    }

    @Override
    public boolean keyDown(int keycode) {
        // Game/UI keys
        switch (keycode) {
        case Keys.BACKSPACE:    game.goToTitle();  break;
        case Keys.R:            game.resetLevel(); break;
        case Keys.EQUALS:       game.nextLevel();  break;
        case Keys.ENTER:        finishLevel();     break;
        }
        
        // If the player doesn't exist don't continue
        if (player == null) {
            return true;
        }
        
        // If the player is dead, don't continue
        Creature creature = creatureMapper.get(player);
        if (creature.getStatus() == Creature.STATUS_DEAD) {
            return true;
        }
        
        // If the level has been completed, don't continue
        if (game.getLevelComplete()) {
            return true;
        }
        
        // Player keys
        switch (keycode) {
        case Keys.LEFT:         moveLeft();        break;
        case Keys.RIGHT:        moveRight();       break;
        case Keys.UP:           moveUp();          break;
        case Keys.DOWN:         moveDown();        break;
        case Keys.SPACE:        jump();            break;
        case Keys.SHIFT_LEFT:   useItem();         break;
        case Keys.CONTROL_LEFT: pickUpItem();      break;
        case Keys.ALT_LEFT:     dropItem();        break;
        }

        return true;
    }
    
    private void finishLevel() {
        if (game.getLevelComplete()) {
            game.nextLevel();
        }
    }
    
    private void moveLeft() {
        Creature creature = creatureMapper.get(player);
        creature.getAnimation(Creature.STATUS_IDLE).flip(false, false);
        creature.getAnimation(Creature.STATUS_WALKING).flip(false, false);
        creature.getAnimation(Creature.STATUS_JUMPING).flip(false, false);
        
        Velocity vel = velocityMapper.get(player);
        vel.setX(-1);
    }
    
    private void moveRight() {
        Creature creature = creatureMapper.get(player);
        creature.getAnimation(Creature.STATUS_IDLE).flip(true, false);
        creature.getAnimation(Creature.STATUS_WALKING).flip(true, false);
        creature.getAnimation(Creature.STATUS_JUMPING).flip(true, false);
        
        Velocity vel = velocityMapper.get(player);
        vel.setX(1);
    }
    
    private void moveUp() {
        Appearance appearance = appearanceMapper.get(player);
        Creature creature = creatureMapper.get(player);
        Position pos = positionMapper.get(player);
        Size size = sizeMapper.get(player);
        Velocity vel = velocityMapper.get(player);
        
        int status = creature.getStatus();
        int tileW = level.getTileWidth();
        int tileH = level.getTileHeight();
        int tileX = (int) pos.getX() / tileW;
        int tileBot = (int) (pos.getY() - (size.getHeight() / 2)) / tileH;
        
        if (level.isCellLadder(tileX, tileBot)) {
            vel.setX(0);
            vel.setY(1);
            pos.setX(((int) pos.getX() / 16) * 16 + 8);

            if (status != Creature.STATUS_CLIMBING) {
                creature.setStatus(Creature.STATUS_CLIMBING);
                appearance.setAnimation(creature.getCurrentAnimation());
            } else {
                creature.getCurrentAnimation().resume();
            }
        }
    }
    
    private void moveDown() {
        Appearance appearance = appearanceMapper.get(player);
        Creature creature = creatureMapper.get(player);
        Position pos = positionMapper.get(player);
        Velocity vel = velocityMapper.get(player);
        
        int status = creature.getStatus();
        int tileW = level.getTileWidth();
        int tileH = level.getTileHeight();
        int tileX = (int) pos.getX() / tileW;
        int tileY = (int) pos.getY() / tileH;

        if (level.isCellLadder(tileX, tileY - 1) 
                || level.isCellLadder(tileX, tileY)) {
            
            vel.setX(0);
            vel.setY(-1);
            pos.setX(((int) pos.getX() / 16) * 16 + 8);
            
            if (status != Creature.STATUS_CLIMBING) {
                creature.setStatus(Creature.STATUS_CLIMBING);
                appearance.setAnimation(creature.getCurrentAnimation());
            } else {
                creature.getCurrentAnimation().resume();
            }
        }
    }
    
    private void jump() {
        Appearance appearance = appearanceMapper.get(player);
        Creature creature = creatureMapper.get(player);
        Velocity vel = velocityMapper.get(player);
        
        int status = creature.getStatus();
        
        if (status != Creature.STATUS_JUMPING
                && status != Creature.STATUS_CLIMBING) {
            
            creature.setStatus(Creature.STATUS_JUMPING);
            vel.setY(3.0f);
            appearance.setAnimation(creature.getCurrentAnimation());
            jumpSnd.play();
        }
    }
    
    private void useItem() {
        InventoryItem item = game.getItem();
        
        if (item == null) {
            return;
        }
        
        Position pos = positionMapper.get(player);
        
        int tileW = level.getTileWidth();
        int tileH = level.getTileHeight();
        int tileX = (int) pos.getX() / tileW;
        int tileY = (int) pos.getY() / tileH;

        if (item.getType().equals("key")) {
            if (level.isCellDoor(tileX, tileY) && !game.getLevelComplete()) {
                MapProperties props = level.getTileProperties(
                    Level.LAYER_INTERACTIVE,
                    tileX, tileY
                );
                
                String animFile = "assets/sprites/"
                                + props.get("animation").toString();
                
                Animation anim = assets.get(animFile, Animation.class);
                AnimationState animState = new AnimationState(anim, false);

                int x = (tileX * tileW) + tileW/2;
                int y = (tileY * tileH) + tileH/2;
                
                Entity e = world.createEntity();
                e.addComponent(new Position(x, y));
                e.addComponent(new Size(tileW, tileH));
                e.addComponent(new Appearance(animState, 0));
                world.addEntity(e);
                
                doorSnd.play();
                
                game.setLevelComplete(true);
            }
        }
    }
    
    private void pickUpItem() {
        GroupManager groups = world.getManager(GroupManager.class);
        ImmutableBag<Entity> itemEntities = groups.getEntities("ITEM");

        for (int i = 0; i < itemEntities.size(); i++) {
            Entity itemEntity = itemEntities.get(i);
            Appearance itemApp = appearanceMapper.get(itemEntity);
            Position itemPos = positionMapper.get(itemEntity);

            if (Collision.boxCollision(player, itemEntity)) {
                Item item = itemMapper.get(itemEntity);
                String itemType = item.getType();
                InventoryItem oldItem = game.getItem();
                
                if (oldItem != null) {
                    level.createItem(
                        world,
                        oldItem.getAnimationPath(),
                        oldItem.getType(),
                        (int)itemPos.getX() / level.getTileWidth(),
                        (int)itemPos.getY() / level.getTileHeight()
                    );
                }
                
                Animation rawAnim = itemApp.getAnimation().getRaw();
                game.setItem(new InventoryItem(itemType, rawAnim));
                
                itemPickupSnd.play();
                
                itemEntity.deleteFromWorld();
                
                break;
            }
        }
    }
    
    private void dropItem() {
        InventoryItem item = game.getItem();
        
        if (item == null) {
            return;
        }
        
        GroupManager groups = world.getManager(GroupManager.class);
        ImmutableBag<Entity> itemEntities = groups.getEntities("ITEM");

        for (int i = 0; i < itemEntities.size(); i++) {
            Entity itemEntity = itemEntities.get(i);

            if (Collision.boxCollision(player, itemEntity)) {
                return;
            }
        }
        
        Position playerPos = positionMapper.get(player);
        
        level.createItem(
            world,
            item.getAnimationPath(),
            item.getType(),
            (int)playerPos.getX() / level.getTileWidth(),
            (int)playerPos.getY() / level.getTileHeight()
        );
        
        game.setItem(null);
        
        itemDropSnd.play();
    }

    @Override
    public boolean keyUp(int keycode) {
        if (player == null) return true;
       
        Velocity v = velocityMapper.get(player);
        Creature creature = creatureMapper.get(player);

        if ((keycode == Keys.LEFT && !Gdx.input.isKeyPressed(Keys.RIGHT)) ||
                (keycode == Keys.RIGHT && !Gdx.input.isKeyPressed(Keys.LEFT))) {
            
            v.setX(0);
        }
        
        if (((keycode == Keys.UP && !Gdx.input.isKeyPressed(Keys.DOWN))
                || (keycode == Keys.DOWN && !Gdx.input.isKeyPressed(Keys.UP)))
                && creature.getStatus() == Creature.STATUS_CLIMBING) {
                
                creature.getCurrentAnimation().pause();
                v.setY(0);
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int ptr, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int ptr, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int ptr) {
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
