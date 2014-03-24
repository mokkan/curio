package com.wasome.curio.systems;

import java.util.List;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.Level;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Enemy;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.components.Velocity;
import com.wasome.curio.pathfinding.AStarMap;
import com.wasome.curio.pathfinding.AStarNode;

public class EnemyPathingSystem extends IntervalEntityProcessingSystem {

    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Velocity> velocityMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Creature> creatureMapper;
    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private @Mapper ComponentMapper<Enemy> enemyMapper;
    private Level level;
    private int tgtX = -1;
    private int tgtY = -1;
    
    @SuppressWarnings("unchecked")
    public EnemyPathingSystem(Level level) {
        super(Aspect.getAspectForAll(Enemy.class), 100);
        this.level = level;
    }

    @Override
    protected void process(Entity e) {
        Entity player = world.getManager(TagManager.class).getEntity("PLAYER");
        Enemy enemy = enemyMapper.get(e);
        
        if (player == null) {
            enemy.setPath(null);
            return;
        }
        
        AStarMap map = level.getAStarMap();
        Position pos = positionMapper.get(e);
        Position playerPos = positionMapper.get(player);

        int playerTileX = (int) playerPos.getX() / level.getTileWidth();
        int playerTileY = (int) playerPos.getY() / level.getTileHeight();
        
        if (tgtX < 0 || tgtY < 0) {
            tgtX = playerTileX;
            tgtY = playerTileY;
        }
        
        int tileX = (int) pos.getX() / level.getTileWidth();
        int tileY = (int) pos.getY() / level.getTileHeight();

        List<AStarNode> path = map.getPath(
            tileX,
            tileY,
            playerTileX,
            playerTileY
        );
        
        if (path != null) {
            tgtX = playerTileX;
            tgtY = playerTileY;
        }
        
        if (path == null) {
            path = map.getPath(tileX, tileY, tgtX, tgtY);
        }
        
        enemy.setPath(path);
    }

}
