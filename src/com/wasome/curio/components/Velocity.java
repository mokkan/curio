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

public class Velocity extends Component {

    private float vx;
    private float vy;
    
    public Velocity() {}
    
    public Velocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }
    
    public float getX() {
        return vx;
    }
    
    public float getY() {
        return vy;
    }
    
    public void setX(float vx) {
        this.vx = vx;
    }
    
    public void setY(float vy) {
        this.vy = vy;
    }
    
}
