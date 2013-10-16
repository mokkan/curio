package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Velocity;

public class MovementSystem extends IntervalEntityProcessingSystem {

    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<Velocity> velocityMapper;
    
    @SuppressWarnings("unchecked")
    public MovementSystem() {
        super(Aspect.getAspectForAll(Velocity.class, Position.class), 10);
    }

    @Override
    protected void process(Entity e) {
        Position pos = positionMapper.get(e);
        Velocity vel = velocityMapper.get(e);
        
        pos.addX(vel.getVelX());
        pos.addY(vel.getVelY());
    }

}
