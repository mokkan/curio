package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Velocity;

public class GravitySystem extends IntervalEntityProcessingSystem {

    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Gravity> gravityMapper;
    
    @SuppressWarnings("unchecked")
    public GravitySystem() {
        super(Aspect.getAspectForAll(Velocity.class, Gravity.class), 10);
    }
    
    @Override
    protected void process(Entity e) {
        Velocity v = velocityMapper.get(e);
        Gravity g = gravityMapper.get(e);
        
        if (v.getY() > g.getTerminal()) {
            v.setY(v.getY() + g.getAccel());
        }
    }

}
