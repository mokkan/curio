package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.wasome.curio.GameScreen;
import com.wasome.curio.Level;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Treasure;
import com.wasome.curio.components.Velocity;

public class MovementSystem extends IntervalEntitySystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private @Mapper ComponentMapper<Treasure> treasureMapper;
    private ImmutableBag<Entity> treasureEntities;
    private Level level;
    private Entity player;
    private GameScreen game;
    private AssetManager assetManager;
    
    @SuppressWarnings("unchecked")
    public MovementSystem(GameScreen game, AssetManager asm, Level level) {
        super(Aspect.getAspectForAll(Velocity.class, Position.class), 10);
        this.game = game;
        this.level = level;
        this.assetManager = asm;
    }
    
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        player = world.getManager(TagManager.class).getEntity("PLAYER");
        
        for (int i = 0, s = entities.size(); s > i; i++) {
            process(entities.get(i));
        }
    }

    protected void process(Entity e) {
        Position pos = positionMapper.get(e);
        Velocity vel = velocityMapper.get(e);        

        float oldX = pos.getX(), oldY = pos.getY();
        boolean collisionX = false, collisionY = false;
        
        pos.addX(vel.getX());
        
        if (vel.getX() < 0) {
            collisionX = checkLeftCollisions(e);
        } else if (vel.getX() > 0) {
            collisionX = checkRightCollisions(e);
        }
        
        if (collisionX) {
            pos.setX(oldX);
        }
        
        pos.addY(vel.getY());
        
        if (vel.getY() < 0) {
            collisionY = checkBottomCollisions(e);
        } else if (vel.getY() > 0) {
            collisionY = checkTopCollisions(e);
        }
        
        if (collisionY) {
            pos.setY(oldY);
            vel.setY(0);
        }
        
        // Set the status of the creature
        updateStatus(e);
        
        // Run player-specific code
        if (e.getId() == player.getId()) {
            processPlayer(e);
        }
    }
    
    void processPlayer(Entity e) {
        treasureEntities = world.getManager(GroupManager.class).getEntities("TREASURE");
        Position p1 = positionMapper.get(e);
        Size s1 = sizeMapper.get(e);
        Position p2;
        Size s2;
        int val;
        
        for (int i = 0; i < treasureEntities.size(); i++) {
            Entity treasure = treasureEntities.get(i);
            p2 = positionMapper.get(treasure);
            s2 = sizeMapper.get(treasure);

            if (checkCollision(p1, s1, p2, s2)) {
                val = treasureMapper.get(treasure).getValue();
                game.setScore(game.getScore() + val);
                treasure.deleteFromWorld();
                Sound snd = assetManager.get("assets/sounds/collect.wav", Sound.class);
                snd.play();
            }
        }
    }
    
    public boolean checkCollision(Position p1, Size s1, Position p2, Size s2) {
        float x1 = p1.getX() - s1.getWidth()/2;
        float y1 = p1.getY() - s1.getHeight()/2;
        float w1 = s1.getWidth();
        float h1 = s1.getHeight();
        
        float x2 = p2.getX() - s2.getWidth()/2;
        float y2 = p2.getY() - s2.getHeight()/2;
        float w2 = s2.getWidth();
        float h2 = s2.getHeight();
        
        return (Math.abs(x1 - x2) * 2 < (w1 + w2)) &&
                (Math.abs(y1 - y2) * 2 < (h1 + h2));
    }
    
    private void updateStatus(Entity e) {
        Position pos = positionMapper.get(e);
        Velocity vel = velocityMapper.get(e);
        Size size = sizeMapper.get(e);
        Creature creature = creatureMapper.get(e);
        Appearance appearance = appearanceMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);

        int tileX = (int) pos.getX() / level.getTileWidth();
        int tileL = (int) (bbx1 / level.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / level.getTileWidth());
        int tileBot = (int) (bby1 / level.getTileHeight());
        
        int status = creature.getStatus();

        // Check for idle/walking status
        if ((level.isCellSolid(tileL, tileBot-1)
                || level.isCellSolid(tileR, tileBot-1))
                && vel.getY() == 0 && status != Creature.STATUS_CLIMBING) {
            
            if (vel.getX() == 0) {
                creature.setStatus(Creature.STATUS_IDLE);
            } else {
                creature.setStatus(Creature.STATUS_WALKING);
            }
        }
        
        // Check for jumping status
        if ((status == Creature.STATUS_CLIMBING && vel.getX() != 0)
                || (status != Creature.STATUS_CLIMBING && vel.getY() != 0)) {
            
            creature.setStatus(Creature.STATUS_JUMPING);
        }
        
        // Check for transition between climbing and idle
        if (status == Creature.STATUS_CLIMBING
                && !level.isCellLadder(tileX, tileBot)) {
            
            creature.setStatus(Creature.STATUS_IDLE);
            pos.setY((tileBot+1) * level.getTileHeight() - size.getHeight()/2);
            vel.setY(0);
        }
        
        // Update appearance
        if (status != creature.getStatus()) {
            appearance.setAnimation(creature.getCurrentAnimation());
        }
    }
    
    private boolean checkLeftCollisions(Entity e) {
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);

        int tileL = (int) (bbx1 / level.getTileWidth());
        int tileBot = (int) (bby1 / level.getTileHeight());
        int tileTop = (int) ((bby2 - 1) / level.getTileHeight());
        
        for (int y = tileBot; y <= tileTop; y++) {
            if (level.isCellSolid(tileL, y)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkRightCollisions(Entity e) {
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);

        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);

        int tileR = (int) ((bbx2 - 1) / level.getTileWidth());
        int tileBot = (int) (bby1 / level.getTileHeight());
        int tileTop = (int) ((bby2 - 1) / level.getTileHeight());
        
        for (int y = tileBot; y <= tileTop; y++) {
            if (level.isCellSolid(tileR, y)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkBottomCollisions(Entity e) {
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        Creature creature = creatureMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / level.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / level.getTileWidth());
        int tileBot = (int) (bby1 / level.getTileHeight());
        
        for (int x = tileL; x <= tileR; x++) {
            
            // Split these cases up to make more readable. Top case is test for
            // when ladders are present (allows for climbing). Second case is
            // when there is no ladder (standard case).
            if (level.isCellSolid(x, tileBot) && level.isCellLadder(x, tileBot)
                    && bby1 >= (tileBot+1) * level.getTileHeight() - 1
                    && creature.getStatus() != Creature.STATUS_CLIMBING) {
                
                return true;
            } else if (level.isCellSolid(x, tileBot)
                    && !level.isCellLadder(x, tileBot)) {
                
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkTopCollisions(Entity e) {
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        Creature creature = creatureMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / level.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / level.getTileWidth());
        int tileTop = (int) ((bby2 - 1) / level.getTileHeight());
        
        for (int x = tileL; x <= tileR; x++) {
            if (level.isCellSolid(x, tileTop)
                    && !(level.isCellLadder(x, tileTop)
                         && creature.getStatus() == Creature.STATUS_CLIMBING)) {
                
                return true;
            }
        }
        
        return false;
    }

}
