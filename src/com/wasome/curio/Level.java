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

package com.wasome.curio;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Enemy;
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Item;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Treasure;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.pathfinding.AStarMap;
import com.wasome.curio.sprites.Animation;
import com.wasome.curio.sprites.AnimationState;

public class Level {

    public static final int LAYER_TERRAIN = 0;
    public static final int LAYER_INTERACTIVE = 1;
    public static final int LAYER_ENTITIES = 2;

    private static final int[] renderLayers = {0, 1};
    private AssetManager assets;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private TiledMapTileLayer terrainLayer;
    private TiledMapTileLayer interactiveLayer;
    private TiledMapTileLayer entitiesLayer;
    private int tileWidth;
    private int tileHeight;
    private int[][] pathMap;
    private AStarMap aStarMap;
    private int totalTreasure;
    
    public Level(TiledMap map, AssetManager assetManager) {
        this.assets = assetManager;
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
    
    public void createEntities(World world) {
        if (entitiesLayer == null) {
            return;
        }

        for (int y = 0; y < entitiesLayer.getHeight(); y++) {
            for (int x = 0; x < entitiesLayer.getWidth(); x++) {
                Cell cell = entitiesLayer.getCell(x, y);

                if (cell == null) {
                    continue;
                }
                
                String animDir = "assets/sprites/";
                TiledMapTile tile = cell.getTile();
                MapProperties props = tile.getProperties();
                
                int posX = x * tileWidth;
                int posY = y * tileHeight;
                
                if (props.containsKey("player")) {
                    createPlayer(world, posX, posY);
                } else if (props.containsKey("enemy")) { 
                    createEnemy(world, posX, posY);
                } else if (props.containsKey("treasure")) {
                    int v = Integer.parseInt(props.get("treasure").toString());
                    String anim = animDir + props.get("animation").toString();
                    createTreasure(world, anim, posX, posY, v);
                } else if (props.containsKey("item")) {
                    String anim = animDir + props.get("animation").toString();
                    String type = props.get("item").toString();
                    createItem(world, anim, type, x, y);
                }
            }
        }
    }
    
    private void createPlayer(World world, int x, int y) {
        // Get animations for creature
        Animation idleAnim = assets.get(Resources.ANIM_IMP_IDLE, Animation.class);
        Animation walkAnim = assets.get(Resources.ANIM_IMP_WALK, Animation.class);
        Animation jumpAnim = assets.get(Resources.ANIM_IMP_JUMP, Animation.class);
        Animation climbAnim = assets.get(Resources.ANIM_IMP_CLIMB, Animation.class);
        Animation fallAnim = assets.get(Resources.ANIM_IMP_FALL, Animation.class);
        
        // Create creature for player and set animations
        Creature creature = new Creature();
       
        creature.setAnimation(
                Creature.STATUS_IDLE,
                new AnimationState(idleAnim, true)
        );
        
        creature.setAnimation(
                Creature.STATUS_WALKING,
                new AnimationState(walkAnim, true)
        );
        
        creature.setAnimation(
                Creature.STATUS_JUMPING,
                new AnimationState(jumpAnim, true)
        );

        creature.setAnimation(
                Creature.STATUS_CLIMBING,
                new AnimationState(climbAnim, true)
        );
        
        creature.setAnimation(
                Creature.STATUS_DEAD,
                new AnimationState(fallAnim, true)
        );
        
        // Create size and position components
        Size size = new Size(16, 16);
        Position pos = new Position(
            x + size.getWidth()/2,
            y + size.getHeight()/2
        );

        // Create player entity and add components
        Entity e = world.createEntity();
        e.addComponent(pos);
        e.addComponent(size);
        e.addComponent(new Velocity(0, 0));
        e.addComponent(new Gravity(-3.0f, -0.25f));
        e.addComponent(creature);
        e.addComponent(new Appearance(creature.getCurrentAnimation(), 1));
        
        world.getManager(TagManager.class).register("PLAYER", e);
        world.addEntity(e);
    }
    
    private void createTreasure(World world, String aniFile, int x, int y,
            int value) {

        // Get the animation
        Animation ani = assets.get(aniFile, Animation.class);
        AnimationState aniState = new AnimationState(ani, true);
        
        // Create size and position entities
        Size size = new Size(ani.getWidth(), ani.getHeight());
        Position pos = new Position(
            x + size.getWidth()/2,
            y + size.getHeight()/2
        );
        
        // Create the entity
        Entity e = world.createEntity();
        e.addComponent(pos);
        e.addComponent(size);
        e.addComponent(new Appearance(aniState, 0));
        e.addComponent(new Treasure(value));

        world.getManager(GroupManager.class).add(e, "TREASURE");
        
        world.addEntity(e);
        
        // Add to total treasure
        totalTreasure += value;
    }
    
    public void createEnemy(World world, int x, int y) {
        // Get animations for creature
        Animation idleAnim = assets.get(Resources.ANIM_GOB_IDLE, Animation.class);
        Animation walkAnim = assets.get(Resources.ANIM_GOB_WALK, Animation.class);
        Animation climbAnim = assets.get(Resources.ANIM_GOB_CLIMB, Animation.class);
        
        // Create creature for enemy
        Creature creature = new Creature();
       
        creature.setAnimation(
                Creature.STATUS_IDLE,
                new AnimationState(idleAnim, true)
        );
        
        creature.setAnimation(
                Creature.STATUS_WALKING,
                new AnimationState(walkAnim, true)
        );
        
        creature.setAnimation(
                Creature.STATUS_JUMPING,
                null
        );

        creature.setAnimation(
                Creature.STATUS_CLIMBING,
                new AnimationState(climbAnim, true)
        );
        
        creature.setAnimation(
                Creature.STATUS_DEAD,
                null
        );
        
        // Create size and position components
        Size size = new Size(16, 16);
        Position pos = new Position(
            x + size.getWidth()/2,
            y + size.getHeight()/2
        );

        // Create entity and add components
        Entity e = world.createEntity();
        e.addComponent(pos);
        e.addComponent(size);
        e.addComponent(new Velocity(0, 0));
        e.addComponent(new Gravity(-3.0f, -0.25f));
        e.addComponent(creature);
        e.addComponent(new Appearance(creature.getCurrentAnimation(), 1));
        e.addComponent(new Enemy());

        world.getManager(GroupManager.class).add(e, "ENEMY");
        
        world.addEntity(e);
    }
    
    public void createItem(World world, String aniFile, String type, int x,
            int y) {

        // Get the animation
        Animation ani = assets.get(aniFile, Animation.class);
        AnimationState aniState = new AnimationState(ani, true);
        
        // Create size and position entities
        Size size = new Size(ani.getWidth(), ani.getHeight());
        Position pos = new Position(
            (x * tileWidth) + size.getWidth()/2,
            (y * tileHeight) + size.getHeight()/2
        );
        
        // Create the entity
        Entity e = world.createEntity();
        e.addComponent(pos);
        e.addComponent(size);
        e.addComponent(new Appearance(aniState, 0));
        e.addComponent(new Item(type));

        world.getManager(GroupManager.class).add(e, "ITEM");
        
        world.addEntity(e);
    }
    
    public void setMap(TiledMap map) {
        this.map = map;
        totalTreasure = 0;
        
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
        
        // Build pathfinding map
        buildPathMap();
    }
    
    public void buildPathMap() {
        pathMap = new int[terrainLayer.getHeight()][terrainLayer.getWidth()];
        
        for (int y = 0; y < terrainLayer.getHeight(); y++) {
            for (int x = 0; x < terrainLayer.getWidth(); x++) {
                if ((isCellSolid(x, y-1) && !isCellSolid(x, y)) 
                        || isCellLadder(x, y)) {

                    pathMap[y][x] = 1;
                } else {
                    pathMap[y][x] = 0;
                }
            }
        }
        
        aStarMap = new AStarMap(pathMap);
    }
    
    public int[][] getPathMap() {
        return pathMap;
    }
    
    public AStarMap getAStarMap() {
        return aStarMap;
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
    }
    
    public boolean isCellLadder(int x, int y) {
        Cell interactiveCell = interactiveLayer.getCell(x, y);
        
        if (interactiveCell == null) {
            return false;
        }
        
        TiledMapTile interactiveTile = interactiveCell.getTile();
        return interactiveTile.getProperties().containsKey("ladder");
    }
    
    public boolean isCellDoor(int x, int y) {
        Cell interactiveCell = interactiveLayer.getCell(x, y);
        
        if (interactiveCell == null) {
            return false;
        }
        
        TiledMapTile interactiveTile = interactiveCell.getTile();
        return interactiveTile.getProperties().containsKey("door");
    }
    
    public MapProperties getTileProperties(int layerId, int x, int y) {
        TiledMapTileLayer layer;
        
        if (layerId == LAYER_TERRAIN) {
            layer = terrainLayer;
        } else if (layerId == LAYER_INTERACTIVE) {
            layer = interactiveLayer;
        } else if (layerId == LAYER_ENTITIES) {
            layer = entitiesLayer;
        } else {
            return null;
        }
        
        Cell cell = layer.getCell(x, y);
        
        if (cell == null) {
            return null;
        }
        
        return cell.getTile().getProperties();
    }
    
    public int getTotalTreasure() {
        return totalTreasure;
    }
    
}
