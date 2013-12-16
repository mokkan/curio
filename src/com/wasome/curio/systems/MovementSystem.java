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
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Treasure;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.sprites.Frame;

public class MovementSystem extends IntervalEntitySystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private @Mapper ComponentMapper<Treasure> treasureMapper;
    private @Mapper ComponentMapper<Gravity> gravityMapper;
    private ImmutableBag<Entity> treasureEntities;
    private ImmutableBag<Entity> enemyEntities;
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
        enemyEntities = world.getManager(GroupManager.class).getEntities("ENEMY");
        treasureEntities = world.getManager(GroupManager.class).getEntities("TREASURE");
        
        for (int i = 0, s = entities.size(); s > i; i++) {
            process(entities.get(i));
        }
    }

    protected void process(Entity e) {
        if (player == null || e.getId() != player.getId()) {
            return;
        }
        
        Creature creature = creatureMapper.get(e);
        Position pos = positionMapper.get(e);
        Velocity vel = velocityMapper.get(e);   
        
        if (creature.getStatus() == Creature.STATUS_DEAD) {
            if (pos.getY() < 0) {
                player.deleteFromWorld();
            } else {
                pos.addY(vel.getY());
            }
            return;
        }

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
        
        if (vel.getY() < 0) {
            for (float vy = vel.getY(); vy < 0; vy += 0.25f) {
                pos.addY(-0.25f);
                collisionY = checkBottomCollisions(e);
            }
        } else {
            pos.addY(vel.getY());
            
            if (vel.getY() < 0) {
                collisionY = checkBottomCollisions(e);
            } else if (vel.getY() > 0) {
                collisionY = checkTopCollisions(e);
            } 
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
        Appearance appearance = appearanceMapper.get(e);
        Velocity vel = velocityMapper.get(e);
        Gravity gravity = gravityMapper.get(e);
        Creature creature = creatureMapper.get(e);
        Position p1 = positionMapper.get(e);
        Size s1 = sizeMapper.get(e);
        Position p2;
        Size s2;
        int val;
        
        for (int i = 0; i < treasureEntities.size(); i++) {
            Entity treasure = treasureEntities.get(i);
            p2 = positionMapper.get(treasure);
            s2 = sizeMapper.get(treasure);

            if (checkCollision(p1, s1, p2, s2)
                    && checkEntityCollision(player, treasure)) {
                
                val = treasureMapper.get(treasure).getValue();
                game.setScore(game.getScore() + val);
                treasure.deleteFromWorld();
                assetManager.get("assets/sounds/collect.wav", Sound.class).play();
            }
        }
        
        for (int i = 0; i < enemyEntities.size(); i++) {
            Entity enemy = enemyEntities.get(i);
            p2 = positionMapper.get(enemy);
            s2 = sizeMapper.get(enemy);

            if (checkCollision(p1, s1, p2, s2)
                    && checkEntityCollision(player, enemy)
                    && creature.getStatus() != Creature.STATUS_DEAD) {
                
                assetManager.get("assets/sounds/creature.wav", Sound.class).play();
                creature.setStatus(Creature.STATUS_DEAD);
                appearance.setAnimation(creature.getCurrentAnimation());
                vel.setY(3.0f);
                gravity.setTerminal(-10.0f);
            }
        }
    }
    
    public boolean checkEntityCollision(Entity e1, Entity e2) {
        Position pos1 = positionMapper.get(e1);
        Position pos2 = positionMapper.get(e2);
        Frame f1 = appearanceMapper.get(e1).getAnimation().getCurrentFrame();
        Frame f2 = appearanceMapper.get(e2).getAnimation().getCurrentFrame();
        int dx = (int) -(pos2.getX() - pos1.getX());
        int dy = (int) (pos2.getY() - pos1.getY());
        int w1 = f1.getTextureRegion().getRegionWidth();
        int h1 = f1.getTextureRegion().getRegionHeight();
        int w2 = f1.getTextureRegion().getRegionWidth();
        int h2 = f1.getTextureRegion().getRegionHeight();
        boolean[][] m1 = f1.getCollisionMap();
        boolean[][] m2 = f2.getCollisionMap();

        for (int py1 = 0; py1 < h1; py1++) {
            for (int px1 = 0; px1 < w1; px1++) {
                int px2 = px1 + dx;
                int py2 = py1 + dy;
                
                if (px2 >= w2 || px2 < 0 || py2 >= h2 || py2 < 0) {
                    continue;
                }
                
                if (m1[py1][px1] && m2[py2][px2]) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean checkCollision(Position p1, Size s1, Position p2, Size s2) {
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
        
        if (e.getId() != player.getId()) {
            System.out.println(creature.getStatus());
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
            
            // Split these cases up to make more readable. First case is for
            // when player is jumping. Second case is test for when ladders are 
            // present (allows for climbing). Third case is when there is no
            // ladder (standard case).
            if (level.isCellSolid(x, tileBot) && creature.getStatus() == Creature.STATUS_JUMPING) {
                return true;
            } else
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
