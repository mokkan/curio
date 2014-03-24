/*
 * Curio - A simple puzzle platformer game.
 * Copyright (C) 2014  Michael Swiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
import com.wasome.curio.Collision;
import com.wasome.curio.GameScreen;
import com.wasome.curio.Level;
import com.wasome.curio.Resources;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Treasure;
import com.wasome.curio.components.Velocity;

public class PlayerMovementSystem extends IntervalEntitySystem {

    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private @Mapper ComponentMapper<Gravity> gravityMapper;
    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Treasure> treasureMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;

    private Level level;
    private GameScreen game;
    private AssetManager assets;

    public PlayerMovementSystem(GameScreen g, AssetManager assets, Level lvl) {
        super(Aspect.getEmpty(), 10);
        this.game = g;
        this.level = lvl;
        this.assets = assets;
    }
    
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        Entity player = world.getManager(TagManager.class).getEntity("PLAYER");
        
        if (player == null) { 
            return;
        }
        
        Creature creature = creatureMapper.get(player);
        
        processDeath(player);
        if (creature.getStatus() != Creature.STATUS_DEAD) {
            processWorldCollisions(player);
            updateStatus(player);
            processEnemyCollisions(player);
            processTreasureCollisions(player);
        }
    }
    
    private void processDeath(Entity player) {
        Creature creature = creatureMapper.get(player);
        Position pos = positionMapper.get(player);
        Velocity vel = velocityMapper.get(player);   
        
        if (creature.getStatus() == Creature.STATUS_DEAD) {
            if (pos.getY() < 0) {
                player.deleteFromWorld();
            } else {
                pos.addY(vel.getY());
            }
            return;
        }
    }
    
    private void processWorldCollisions(Entity player) {
        Position pos = positionMapper.get(player);
        Velocity vel = velocityMapper.get(player);
        
        final float dy = 0.25f; // position interval to use for updating y pos
        float oldX = pos.getX(), oldY = pos.getY();
        boolean collisionX = false, collisionY = false;
        
        pos.addX(vel.getX());
        
        if (vel.getX() < 0) {
            collisionX = checkLeftCollisions(player);
        } else if (vel.getX() > 0) {
            collisionX = checkRightCollisions(player);
        }
        
        if (collisionX) {
            pos.setX(oldX);
        }
        
        if (vel.getY() < 0) {
            for (float vy = vel.getY(); vy < 0; vy += dy) {
                pos.addY(-0.25f);
                collisionY = checkBottomCollisions(player);
            }
        } else {
            pos.addY(vel.getY());
            
            if (vel.getY() < 0) {
                collisionY = checkBottomCollisions(player);
            } else if (vel.getY() > 0) {
                collisionY = checkTopCollisions(player);
            } 
        }
        
        if (collisionY) {
            pos.setY(oldY);
            vel.setY(0);
        }
    }

    private void processEnemyCollisions(Entity player) {
        GroupManager groups = world.getManager(GroupManager.class);
        ImmutableBag<Entity> enemyEntities = groups.getEntities("ENEMY");
        
        Appearance appearance = appearanceMapper.get(player);
        Creature creature = creatureMapper.get(player);
        Gravity gravity = gravityMapper.get(player);
        Velocity vel = velocityMapper.get(player);
        
        Sound snd = assets.get(Resources.SND_DEATH, Sound.class);
        
        for (int i = 0; i < enemyEntities.size(); i++) {
            Entity enemy = enemyEntities.get(i);

            if (Collision.boxCollision(player, enemy)
                    && Collision.pixelCollision(player, enemy)
                    && creature.getStatus() != Creature.STATUS_DEAD) {
                
                snd.play();
                creature.setStatus(Creature.STATUS_DEAD);
                appearance.setAnimation(creature.getCurrentAnimation());
                vel.setY(3.0f);
                gravity.setTerminal(-10.0f);
            }
        }
    }
    
    private void processTreasureCollisions(Entity player) {
        GroupManager groups = world.getManager(GroupManager.class);
        ImmutableBag<Entity> treasureEntities = groups.getEntities("TREASURE");
        Sound snd = assets.get(Resources.SND_COLLECT, Sound.class);

        for (int i = 0; i < treasureEntities.size(); i++) {
            Entity treasure = treasureEntities.get(i);

            if (Collision.boxCollision(player, treasure)
                    && Collision.pixelCollision(player, treasure)) {
                
                int val = treasureMapper.get(treasure).getValue();
                game.setScore(game.getScore() + val);
                treasure.deleteFromWorld();
                snd.play();
            }
        }
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
            // Split these cases up to make more readable. First case is for
            // when player is jumping. Second case is test for when ladders are 
            // present (allows for climbing). Third case is when there is no
            // ladder (standard case).
            if (level.isCellSolid(x, tileBot)
                    && creature.getStatus() == Creature.STATUS_JUMPING) {

                return true;
            } else if (level.isCellSolid(x, tileBot)
                           && level.isCellLadder(x, tileBot)

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
