package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.Level;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Enemy;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.pathfinding.AStarNode;

public class EnemyMovementSystem extends IntervalEntityProcessingSystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private @Mapper ComponentMapper<Enemy> enemyMapper;
    private Level level;
    
    @SuppressWarnings("unchecked")
    public EnemyMovementSystem(Level level) {
        super(Aspect.getAspectForAll(Enemy.class), 20);
        this.level = level;
    }
    
    @Override
    protected void process(Entity e) {
        Size size = sizeMapper.get(e);
        Position pos = positionMapper.get(e);
        Velocity vel = velocityMapper.get(e);
        Enemy enemy = enemyMapper.get(e);
        Creature creature = creatureMapper.get(e);
        Appearance appearance = appearanceMapper.get(e);
        
        AStarNode n = enemy.getTargetNode();
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);
        
        int tileX = (int) pos.getX() / level.getTileWidth();
        int tileY = (int) pos.getY() / level.getTileHeight();
        
        if (n == null) {
            boolean stillWorkingX = false;
            boolean stillWorkingY = false;
            
            if (vel.getX() > 0 && bbx1 < (tileX * level.getTileWidth())) {
                stillWorkingX = true;
            }
            
            if (vel.getX() < 0 && bbx2 > ((tileX + 1) * level.getTileWidth())) {
                stillWorkingX = true;
            }
            
            if (vel.getY() > 0 && bby1 < (tileY * level.getTileHeight())) {
                stillWorkingY = true;
            }
            
            if (vel.getY() < 0 && bby2 > ((tileY + 1) * level.getTileHeight())) {
                stillWorkingY = true;
            }
            
            if (stillWorkingX) {
                pos.addX(vel.getX());
            }
            
            if (stillWorkingY) {
                pos.addY(vel.getY());
            }
            
            if (stillWorkingX || stillWorkingY) {
                return;
            }
            
            pos.setX(tileX * level.getTileWidth() + size.getWidth()/2);
            
            vel.setX(0);
            vel.setY(0);
            
            int newStatus = creature.getStatus();
            
            if (creature.getStatus() == Creature.STATUS_CLIMBING
                    && level.isCellLadder(tileX, tileY)) {
                creature.getCurrentAnimation().pause();
            } else {
                newStatus = Creature.STATUS_IDLE;
            }
            
            if (newStatus != creature.getStatus()) {
                creature.setStatus(newStatus);
                appearance.setAnimation(creature.getCurrentAnimation());
            }
            
            return;
        }
        
        if (vel.getX() < 0) {
            creature.getAnimation(Creature.STATUS_IDLE).flip(false, false);
            creature.getAnimation(Creature.STATUS_WALKING).flip(false, false);
        } else if (vel.getX() > 0) {
            creature.getAnimation(Creature.STATUS_IDLE).flip(true, false);
            creature.getAnimation(Creature.STATUS_WALKING).flip(true, false);
        }
        
        if (tileX == n.x && tileY == n.y) {
            boolean update = false;
            
            if (vel.getX() < 0 && bbx1 <= n.x * level.getTileWidth()) {
                update = true;
            } else if (vel.getX() > 0 && bbx2 >= (n.x + 1) * level.getTileWidth()) {
                update = true;
            } else if (vel.getY() < 0 && bby1 <= n.y * level.getTileHeight()) {
                update = true;
            } else if (vel.getY() > 0 && bby2 >= (n.y + 1) * level.getTileHeight()) {
                update = true;
            }
            
            if (update) {
                enemy.setTarget(enemy.getTarget() + 1);
                n = enemy.getTargetNode();
            }
        }
        
        int newStatus = creature.getStatus();

        if (n != null) {
            if (bbx1 < n.x * level.getTileWidth()) {
                vel.setX(1);
                vel.setY(0);
                newStatus = Creature.STATUS_WALKING;
            } else if (bbx2 > (n.x + 1) * level.getTileWidth()) {
                vel.setX(-1);
                vel.setY(0);
                newStatus = Creature.STATUS_WALKING;
            } else if (tileY < n.y) {
                vel.setX(0);
                vel.setY(1);
                newStatus = Creature.STATUS_CLIMBING;
                pos.setX(tileX * level.getTileWidth() + size.getWidth()/2);
                creature.getCurrentAnimation().resume();
            } else if (tileY > n.y) {
                vel.setX(0);
                vel.setY(-1);
                newStatus = Creature.STATUS_CLIMBING;
                pos.setX(tileX * level.getTileWidth() + size.getWidth()/2);
                creature.getCurrentAnimation().resume();
            }
        }

        if (newStatus != creature.getStatus()) {
            pos.setY(tileY * level.getTileHeight() + size.getHeight()/2);
            creature.setStatus(newStatus);
            appearance.setAnimation(creature.getCurrentAnimation());
        }
        
        pos.addX(vel.getX());
        pos.addY(vel.getY());
    }

}
