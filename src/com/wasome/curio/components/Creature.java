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

public class Creature extends Component {
    
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_WALKING = 1;
    public static final int STATUS_JUMPING = 2;
    public static final int STATUS_CLIMBING = 3;
    public static final int STATUS_DEAD = 4;

    private int status;
    private AnimationState[] anims;
    
    public Creature() {
        status = STATUS_IDLE;
        anims = new AnimationState[5];
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setAnimation(int status, AnimationState anim) {
        anims[status] = anim;
    }
    
    public AnimationState getAnimation(int status) {
        return anims[status];
    }
    
    public AnimationState getCurrentAnimation() {
        return anims[status];
    }
    
}
