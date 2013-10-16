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
