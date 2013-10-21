package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;

public class MovementSystem extends IntervalEntityProcessingSystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private TiledMapTileLayer terrainLayer;
    private TiledMapTileLayer interactiveLayer;
    private int tileWidth;
    private int tileHeight;
    
    
    @SuppressWarnings("unchecked")
    public MovementSystem(TiledMap map) {
        super(Aspect.getAspectForAll(Velocity.class, Position.class), 10);
        
        // Get the layers of the map
        MapLayers layers = map.getLayers();
        terrainLayer = (TiledMapTileLayer) layers.get("Terrain");
        interactiveLayer = (TiledMapTileLayer) layers.get("Interactive");
        
        // Get the tile dimensions
        tileWidth = (int) terrainLayer.getTileWidth();
        tileHeight = (int) terrainLayer.getTileHeight();
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

        int tileL = (int) (bbx1 / tileWidth);
        int tileBot = (int) (bby1 / tileHeight);
        int tileTop = (int) ((bby2 - 1) / tileHeight);
        
        for (int y = tileBot; y <= tileTop; y++) {

            if (isCellSolid(tileL, y)) {
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

        int tileR = (int) ((bbx2 - 1) / tileWidth);
        int tileBot = (int) (bby1 / tileHeight);
        int tileTop = (int) ((bby2 - 1) / tileHeight);
        
        for (int y = tileBot; y <= tileTop; y++) {
            if (isCellSolid(tileR, y)) {
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
        
        int tileL = (int) (bbx1 / tileWidth);
        int tileR = (int) ((bbx2 - 1) / tileWidth);
        int tileBot = (int) (bby1 / tileHeight);
        
        for (int x = tileL; x <= tileR; x++) {
            if (isCellSolid(x, tileBot)) {
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
        
        int tileL = (int) (bbx1 / tileWidth);
        int tileR = (int) ((bbx2 - 1) / tileWidth);
        int tileTop = (int) ((bby2 - 1) / tileHeight);
        
        for (int x = tileL; x <= tileR; x++) {
            if (isCellSolid(x, tileTop)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isCellSolid(int x, int y) {
        Cell terrainCell = terrainLayer.getCell(x, y);
        Cell interactiveCell = interactiveLayer.getCell(x, y);
        
        // If there's a ladder, the tile is not considered solid
        if (interactiveCell != null) {
            TiledMapTile interactiveTile = interactiveCell.getTile();
            if (interactiveTile.getProperties().containsKey("ladder")) {
                return false;
            }
        }
        
        // If we get here, there was no ladder, so check if terrain is solid
        if (terrainCell != null) {
            TiledMapTile terrainTile = terrainCell.getTile();
            return terrainTile.getProperties().containsKey("solid");
        }
        
        return false;
    }

}
