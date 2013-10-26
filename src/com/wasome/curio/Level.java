package com.wasome.curio;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class Level {
    
    private static final int[] renderLayers = {0, 1};
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private TiledMapTileLayer terrainLayer;
    private TiledMapTileLayer interactiveLayer;
    private TiledMapTileLayer entitiesLayer;
    private int tileWidth;
    private int tileHeight;
    
    public Level(TiledMap map) {
        setMap(map);
    }
    
    public int getTileWidth() {
        return tileWidth;
    }
    
    public int getTileHeight() {
        return tileHeight;
    }
    
    public String getBackground() {
        return map.getProperties().get("background").toString();
    }
    
    public Vector2 getPlayerSpawn() {
        if (entitiesLayer == null) {
            return null;
        }
        
        Cell cell;
        TiledMapTile tile;
        
        for (int y = 0; y < entitiesLayer.getHeight(); y++) {
            for (int x = 0; x < entitiesLayer.getWidth(); x++) {
                cell = entitiesLayer.getCell(x, y);
                if (cell == null) {
                    continue;
                }
                
                tile = cell.getTile();
                
                if (tile.getProperties().containsKey("player")) {
                    return new Vector2(x * tileWidth, y * tileHeight);
                }
            }
        }
        
        return null;
    }
    
    public void setMap(TiledMap map) {
        this.map = map;
        
        // Create the map renderer
        renderer = new OrthogonalTiledMapRenderer(map, 1);
        
        // Get the layers of the map
        MapLayers layers = map.getLayers();
        terrainLayer = (TiledMapTileLayer) layers.get("Terrain");
        interactiveLayer = (TiledMapTileLayer) layers.get("Interactive");
        entitiesLayer = (TiledMapTileLayer) layers.get("Entities");
        
        // Get the tile dimensions
        tileWidth = (int) terrainLayer.getTileWidth();
        tileHeight = (int) terrainLayer.getTileHeight();
    }
    
    public void render(OrthographicCamera cam) {
        renderer.setView(cam);
        renderer.render(renderLayers);
    }
    
    public boolean isCellSolid(int x, int y) {
        
        Cell terrainCell = terrainLayer.getCell(x, y);
        
        if (terrainCell == null) {
            return false;
        }
        
        TiledMapTile interactiveCell = terrainCell.getTile();
        return interactiveCell.getProperties().containsKey("solid");
        /*

        // If there's a ladder, the tile is not considered solid
        if (isCellLadder(x, y)) {
            return false;
        }
        
        // If we get here, there was no ladder, so check if terrain is solid
        if (terrainCell != null) {
            TiledMapTile terrainTile = terrainCell.getTile();
            return terrainTile.getProperties().containsKey("solid");
        }
        
        return false;
        */
    }
    
    public boolean isCellLadder(int x, int y) {
        Cell interactiveCell = interactiveLayer.getCell(x, y);
        
        if (interactiveCell == null) {
            return false;
        }
        
        TiledMapTile interactiveTile = interactiveCell.getTile();
        return interactiveTile.getProperties().containsKey("ladder");
    }
    
}
