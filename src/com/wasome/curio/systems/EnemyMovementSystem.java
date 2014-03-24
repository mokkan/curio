package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
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
        
        int tileWidth = level.getTileWidth();
        int tileHeight = level.getTileHeight();
        int tileX = (int) pos.getX() / tileWidth;
        int tileY = (int) pos.getY() / tileHeight;
        
        if (n == null) {
            boolean stillWorkingX = false;
            boolean stillWorkingY = false;
            
            if (vel.getX() > 0 && bbx1 < tileX * tileWidth) {
                stillWorkingX = true;
            }
            
            if (vel.getX() < 0 && bbx2 > (tileX+1) * tileWidth) {
                stillWorkingX = true;
            }
            
            if (vel.getY() > 0 && bby1 < tileY * tileHeight) {
                stillWorkingY = true;
            }
            
            if (vel.getY() < 0 && bby2 > (tileY+1) * tileHeight) {
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
            
            pos.setX(tileX * tileWidth + size.getWidth()/2);
            
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
            
            if (vel.getX() < 0 && bbx1 <= n.x * tileWidth) {
                update = true;
            } else if (vel.getX() > 0 && bbx2 >= (n.x + 1) * tileWidth) {
                update = true;
            } else if (vel.getY() < 0 && bby1 <= n.y * tileHeight) {
                update = true;
            } else if (vel.getY() > 0 && bby2 >= (n.y + 1) * tileHeight) {
                update = true;
            }
            
            if (update) {
                enemy.setTarget(enemy.getTarget() + 1);
                n = enemy.getTargetNode();
            }
        }
        
        int newStatus = creature.getStatus();

        if (n != null) {
            if (bbx1 < n.x * tileWidth) {
                vel.setX(1);
                vel.setY(0);
                newStatus = Creature.STATUS_WALKING;
            } else if (bbx2 > (n.x + 1) * tileWidth) {
                vel.setX(-1);
                vel.setY(0);
                newStatus = Creature.STATUS_WALKING;
            } else if (tileY < n.y) {
                vel.setX(0);
                vel.setY(1);
                newStatus = Creature.STATUS_CLIMBING;
                pos.setX(tileX * tileWidth + size.getWidth()/2);
                creature.getCurrentAnimation().resume();
            } else if (tileY > n.y) {
                vel.setX(0);
                vel.setY(-1);
                newStatus = Creature.STATUS_CLIMBING;
                pos.setX(tileX * tileWidth + size.getWidth()/2);
                creature.getCurrentAnimation().resume();
            }
        }

        if (newStatus != creature.getStatus()) {
            pos.setY(tileY * tileHeight + size.getHeight()/2);
            creature.setStatus(newStatus);
            appearance.setAnimation(creature.getCurrentAnimation());
        }
        
        pos.addX(vel.getX());
        pos.addY(vel.getY());
    }
    
    public void stopAnimations() {
        GroupManager groups = world.getManager(GroupManager.class);
        ImmutableBag<Entity> enemies = groups.getEntities("ENEMY");
        for (int i = 0; i < enemies.size(); i++) {
            Entity enemy = enemies.get(i);
            Appearance appearance = appearanceMapper.get(enemy);
            appearance.getAnimation().pause();
        }
    }

}
