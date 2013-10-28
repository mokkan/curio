package com.wasome.curio.components;

import com.artemis.Component;

public class Gravity extends Component {
    
    private float terminal;
    private float accel;
    
    public Gravity() {}
    
    public Gravity(float terminal, float accel) {
        this.terminal = terminal;
        this.accel = accel;
    }
    
    public float getTerminal() {
        return terminal;
    }
    
    public void setTerminal(float terminal) {
        this.terminal = terminal;
    }
    
    public float getAccel() {
        return accel;
    }
    
    public void setAccel(float accel) {
        this.accel = accel;
    }
    
}
