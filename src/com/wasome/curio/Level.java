package com.wasome.curio;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Level {
    
    private static final int[] renderLayers = {0, 1};
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private TiledMapTileLayer terrainLayer;
    private TiledMapTileLayer interactiveLayer;
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
    
    public void setMap(TiledMap map) {
        this.map = map;
        
        // Create the map renderer
        renderer = new OrthogonalTiledMapRenderer(map, 1);
        
        // Get the layers of the map
        MapLayers layers = map.getLayers();
        terrainLayer = (TiledMapTileLayer) layers.get("Terrain");
        interactiveLayer = (TiledMapTileLayer) layers.get("Interactive");
        
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
