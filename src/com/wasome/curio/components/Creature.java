package com.wasome.curio.components;

import com.artemis.Component;

public class Creature extends Component {
    
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_WALKING = 2;
    public static final int STATUS_JUMPING = 3;
    public static final int STATUS_CLIMBING = 4;

    private int status;
    
    public Creature() {
        status = STATUS_IDLE;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
}
