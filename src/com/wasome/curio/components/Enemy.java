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
