package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.Level;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;

public class MovementSystem extends IntervalEntityProcessingSystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
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
        Creature creature = creatureMapper.get(e);
        
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
        
        if (vel.getY() != 0) {
            creature.setStatus(Creature.STATUS_JUMPING);
        } else {
            if (vel.getX() != 0) {
                creature.setStatus(Creature.STATUS_WALKING);
            } else {
                creature.setStatus(Creature.STATUS_IDLE);
            }
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
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / level.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / level.getTileWidth());
        int tileBot = (int) (bby1 / level.getTileHeight());
        
        for (int x = tileL; x <= tileR; x++) {
            if (level.isCellSolid(x, tileBot)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkTopCollisions(Entity e) {
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / level.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / level.getTileWidth());
        int tileTop = (int) ((bby2 - 1) / level.getTileHeight());
        
        for (int x = tileL; x <= tileR; x++) {
            if (level.isCellSolid(x, tileTop)) {
                return true;
            }
        }
        
        return false;
    }

}
