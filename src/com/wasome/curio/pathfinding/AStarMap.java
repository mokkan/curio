package com.wasome.curio.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Code built on top of http://stackoverflow.com/a/5602061
public class AStarMap {
    
    private int w;
    private int h;
    private AStarNode[][] nodes;
    
    public AStarMap(int[][] pathMap) {
        h = pathMap.length;
        w = pathMap[0].length;
        nodes = new AStarNode[h][w];
        
        // Build nodes matrix
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (pathMap[y][x] != 0) {
                    nodes[y][x] = new AStarNode(pathMap[y][x], x, y);
                } else {
                    nodes[y][x] = null;
                }
            }
        }
        
        // Build neighbors
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (nodes[y][x] == null) {
                    continue;
                }
                addAsNeighborTo(nodes[y][x], x-1, y);
                addAsNeighborTo(nodes[y][x], x+1, y);
                addAsNeighborTo(nodes[y][x], x, y-1);
                addAsNeighborTo(nodes[y][x], x, y+1);
            }
        }
    }
    
    private void addAsNeighborTo(AStarNode node, int x, int y) {
        if (x < 0 || x >= w || y < 0 || y >= h || nodes[y][x] == null) { 
            return;
        }
        
        node.neighbors.add(nodes[y][x]);
    }
    
    public List<AStarNode> getPath(int x1, int y1, int x2, int y2) {
        // abort if target is unreachable
        if (nodes[y2][x2] == null) {
            return null;
        }
        
        AStarNode start = nodes[y1][x1];
        AStarNode goal = nodes[y2][x2];
        
        Set<AStarNode> open = new HashSet<AStarNode>();
        Set<AStarNode> closed = new HashSet<AStarNode>();
        
        start.g = 0;
        start.h = distHeuristic(start, goal);
        start.f = start.h;
        
        open.add(start);
        
        while (true) {
            AStarNode current = null;
            
            if (open.size() == 0) {
                return null;
            }
            
            for (AStarNode node: open) {
                if(current == null || node.f < current.f) {
                    current = node;
                }
            }
            
            if (current == goal) {
                break;
            }
            
            open.remove(current);
            closed.add(current);
            
            for (AStarNode neighbor : current.neighbors) {
                if (neighbor == null) {
                    continue;
                }
                
                int nextG = current.g + neighbor.cost;
                
                if (nextG < neighbor.g) {
                    open.remove(neighbor);
                    closed.remove(neighbor);
                }
                
                if (!open.contains(neighbor) && !closed.contains(neighbor)) {
                    neighbor.g = nextG;
                    neighbor.h = distHeuristic(neighbor, goal);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
                    open.add(neighbor);
                }
            }
        }
        
        ArrayList<AStarNode> pathNodes = new ArrayList<AStarNode>();
        AStarNode current = goal;
        while (current.parent != null) {
            pathNodes.add(current);
            current = current.parent;
        }
        pathNodes.add(start);
        
        // reset node parents
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (nodes[y][x] == null) {
                    continue;
                }
                nodes[y][x].parent = null;
            }
        }
        
        Collections.reverse(pathNodes);
        
        return pathNodes;
    }
    
    private int distHeuristic(AStarNode start, AStarNode goal) {
        return Math.abs(start.x - goal.x) + Math.abs(start.y - goal.y);
    }
}
