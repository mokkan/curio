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
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Item;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Treasure;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.sprites.Animation;
import com.wasome.curio.sprites.AnimationState;

public class Level {
    
    public final static int LAYER_TERRAIN = 0;
    public final static int LAYER_INTERACTIVE = 1;
    public final static int LAYER_ENTITIES = 2;
    private AssetManager assetManager;
    private static final int[] renderLayers = {0, 1};
    private OrthogonalTiledMapRenderer renderer;
    private TiledMap map;
    private TiledMapTileLayer terrainLayer;
    private TiledMapTileLayer interactiveLayer;
    private TiledMapTileLayer entitiesLayer;
    private int tileWidth;
    private int tileHeight;
    
    public Level(TiledMap map, AssetManager assetManager) {
        this.assetManager = assetManager;
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
        
        Cell cell;
        TiledMapTile tile;
        MapProperties props;
        
        for (int y = 0; y < entitiesLayer.getHeight(); y++) {
            for (int x = 0; x < entitiesLayer.getWidth(); x++) {
                cell = entitiesLayer.getCell(x, y);
                if (cell == null) {
                    continue;
                }
                
                tile = cell.getTile();
                props = tile.getProperties();
                
                if (props.containsKey("player")) {
                    addPlayer(world, x * tileWidth, y * tileHeight);
                } else if (props.containsKey("treasure")) {
                    String anim = "assets/sprites/"
                                + props.get("animation").toString();
                    int v = Integer.parseInt(props.get("treasure").toString());
                    addTreasure(world, anim, x * tileWidth, y * tileHeight, v);
                } else if (props.containsKey("item")) {
                    String anim = "assets/sprites/"
                            + props.get("animation").toString();
                    String type = props.get("item").toString();
                    addItem(world, anim, type, x, y);
                }
            }
        }
    }
    
    private void addPlayer(World world, int x, int y) {
        // Create creature for player
        Creature creature = new Creature();
       
        creature.setAnimation(
                Creature.STATUS_IDLE,
                new AnimationState(
                        assetManager.get(
                            "assets/sprites/imp-idle.anim", 
                             Animation.class
                        ), true
                )
        );
        
        creature.setAnimation(
                Creature.STATUS_WALKING,
                new AnimationState(
                        assetManager.get(
                            "assets/sprites/imp-walk.anim", 
                             Animation.class
                        ), true
                )
        );
        
        creature.setAnimation(
                Creature.STATUS_JUMPING,
                new AnimationState(
                        assetManager.get(
                            "assets/sprites/imp-jump.anim", 
                             Animation.class
                        ), true
                )
        );
        
        creature.setAnimation(
                Creature.STATUS_CLIMBING,
                new AnimationState(
                        assetManager.get(
                            "assets/sprites/imp-climb.anim", 
                             Animation.class
                        ), true
                )
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
        
        world.getManager(TagManager.class).register("PLAYER", e);
        world.addEntity(e);
    }
    
    private void addTreasure(World world, String aniFile, int x, int y, int v) {
        // Get the animation
        Animation ani = assetManager.get(aniFile, Animation.class);
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
        e.addComponent(new Treasure(v));

        world.getManager(GroupManager.class).add(e, "TREASURE");
        
        world.addEntity(e);
    }
    
    public void addItem(World world, String aniFile, String t, int x, int y) {
        // Get the animation
        Animation ani = assetManager.get(aniFile, Animation.class);
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
        e.addComponent(new Item(t));

        world.getManager(GroupManager.class).add(e, "ITEM");
        
        world.addEntity(e);
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
    
}
