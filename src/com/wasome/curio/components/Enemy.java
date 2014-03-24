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

import java.util.ArrayList;
import java.util.List;

import com.artemis.Component;
import com.wasome.curio.pathfinding.AStarNode;

public class Enemy extends Component {
    
    private List<AStarNode> path = new ArrayList<AStarNode>();
    private int tgtNode = 1;
    
    public List<AStarNode> getPath() {
        return path;
    }
    
    public void setPath(List<AStarNode> path) {
        this.path = path;
        tgtNode = 1;
    }
    
    public AStarNode getTargetNode() {
        if (path == null || tgtNode >= path.size()) {
            return null;
        }
        return path.get(tgtNode);
    }
    
    public int getTarget() {
        return tgtNode;
    }
    
    public void setTarget(int target) {
        tgtNode = target;
    }
    
}
