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

package com.wasome.curio.pathfinding;

import java.util.ArrayList;
import java.util.List;

public class AStarNode {
    
    public List<AStarNode> neighbors = new ArrayList<AStarNode>();
    public AStarNode parent;
    public int f;
    public int g;
    public int h;
    public int x;
    public int y;
    public int cost;
    
    public AStarNode(int cost, int x, int y) {
        this.cost = cost;
        this.x = x;
        this.y = y;
    }
    
}
