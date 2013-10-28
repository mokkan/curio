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
