package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.Level;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;

public class MovementSystem extends IntervalEntityProcessingSystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private Level level;
    
    
    @SuppressWarnings("unchecked")
    public MovementSystem(Level level) {
        super(Aspect.getAspectForAll(Velocity.class, Position.class), 10);
        this.level = level;
    }

    @Override
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
