package com.wasome.curio.components;

import com.artemis.Component;

public class Position extends Component {
    
    private float x;
    private float y;
    
    public Position() {}
    
    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
}
