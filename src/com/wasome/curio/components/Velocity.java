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
    
    public float getVelX() {
        return vx;
    }
    
    public float getVelY() {
        return vy;
    }
    
    public void setVelX(float vx) {
        this.vx = vx;
    }
    
    public void setVelY(float vy) {
        this.vy = vy;
    }
    
}
