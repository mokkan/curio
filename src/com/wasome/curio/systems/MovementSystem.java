package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;

public class MovementSystem extends IntervalEntityProcessingSystem {

    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Size> sizeMapper;
    TiledMap map;
    
    @SuppressWarnings("unchecked")
    public MovementSystem(TiledMap map) {
        super(Aspect.getAspectForAll(Velocity.class, Position.class), 10);
        this.map = map;
    }

    @Override
    protected void process(Entity e) {
        Position pos = positionMapper.get(e);
        Velocity vel = velocityMapper.get(e);
        
        float oldX = pos.getX(), oldY = pos.getY();
        boolean collisionX = false, collisionY = false;
        
        pos.addX(vel.getX());
        pos.addY(vel.getY());
        
        if (vel.getX() < 0) {
            collisionX = checkLeftCollisions(e);
        } else if (vel.getX() > 0) {
            collisionX = checkRightCollisions(e);
        }
        
        if (collisionX) {
            pos.setX(oldX);
        }
        
        if (vel.getY() < 0) {
            collisionY = checkBottomCollisions(e);
        } else if (vel.getY() > 0) {
            collisionY = checkTopCollisions(e);
        }
        
        if (collisionY) {
            pos.setY(oldY);
        }
    }
    
    private boolean checkLeftCollisions(Entity e) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / layer.getTileWidth());
        int tileBot = (int) (bby1 / layer.getTileHeight());
        int tileTop = (int) ((bby2 - 1) / layer.getTileHeight());
        
        for (int y = tileBot; y <= tileTop; y++) {

            if (isCellCollidable(layer.getCell(tileL, y))) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkRightCollisions(Entity e) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);

        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);

        int tileR = (int) ((bbx2 - 1) / layer.getTileWidth());
        int tileBot = (int) (bby1 / layer.getTileHeight());
        int tileTop = (int) ((bby2 - 1) / layer.getTileHeight());
        
        for (int y = tileBot; y <= tileTop; y++) {
            if (isCellCollidable(layer.getCell(tileR, y))) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkBottomCollisions(Entity e) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby1 = pos.getY() - (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / layer.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / layer.getTileWidth());
        int tileBot = (int) (bby1 / layer.getTileHeight());
        
        for (int x = tileL; x <= tileR; x++) {
            if (isCellCollidable(layer.getCell(x, tileBot))) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkTopCollisions(Entity e) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        
        float bbx1 = pos.getX() - (size.getWidth() / 2);
        float bbx2 = pos.getX() + (size.getWidth() / 2);
        float bby2 = pos.getY() + (size.getHeight() / 2);
        
        int tileL = (int) (bbx1 / layer.getTileWidth());
        int tileR = (int) ((bbx2 - 1) / layer.getTileWidth());
        int tileTop = (int) ((bby2 - 1) / layer.getTileHeight());
        
        for (int x = tileL; x <= tileR; x++) {
            if (isCellCollidable(layer.getCell(x, tileTop))) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isCellCollidable(Cell cell) {
        if (cell == null) {
            return false;
        }
        
        TiledMapTile tile = cell.getTile();
        
        return tile.getProperties().containsKey("collidable");
    }

}
