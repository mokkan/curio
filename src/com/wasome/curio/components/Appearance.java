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

package com.wasome.curio.components;

import com.artemis.Component;
import com.wasome.curio.sprites.AnimationState;

public class Appearance extends Component {

    private AnimationState anim;
    private int order;
    
    public Appearance(AnimationState anim, int order) {
        this.anim = anim;
        this.order = order;
    }
    
    public AnimationState getAnimation() {
        return anim;
    }
    
    public void setAnimation(AnimationState anim) {
        this.anim = anim;
        this.anim.reset();
    }
    
    public int getOrder() {
        return order;
    }

}
