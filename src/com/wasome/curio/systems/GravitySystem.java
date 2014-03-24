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
import com.artemis.systems.IntervalEntityProcessingSystem;
import com.wasome.curio.components.Creature;
import com.wasome.curio.components.Gravity;
import com.wasome.curio.components.Velocity;

public class GravitySystem extends IntervalEntityProcessingSystem {

    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Gravity> gravityMapper;
    @Mapper ComponentMapper<Creature> creatureMapper;
    
    @SuppressWarnings("unchecked")
    public GravitySystem() {
        super(Aspect.getAspectForAll(Velocity.class, Gravity.class), 10);
    }
    
    @Override
    protected void process(Entity e) {
        Creature creature = creatureMapper.get(e);
        Velocity v = velocityMapper.get(e);
        Gravity g = gravityMapper.get(e);
        
        if (creature.getStatus() == Creature.STATUS_CLIMBING) {
            return;
        }
        
        if (v.getY() > g.getTerminal()) {
            v.setY(v.getY() + g.getAccel());
        }
    }

}
