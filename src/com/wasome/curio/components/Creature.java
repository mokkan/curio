package com.wasome.curio.components;

import com.artemis.Component;
import com.wasome.curio.sprites.AnimationState;

public class Creature extends Component {
    
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_WALKING = 1;
    public static final int STATUS_JUMPING = 2;
    public static final int STATUS_CLIMBING = 3;
    public static final int STATUS_DEAD = 4;

    private int status;
    private AnimationState[] anims;
    
    public Creature() {
        status = STATUS_IDLE;
        anims = new AnimationState[5];
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setAnimation(int status, AnimationState anim) {
        anims[status] = anim;
    }
    
    public AnimationState getAnimation(int status) {
        return anims[status];
    }
    
    public AnimationState getCurrentAnimation() {
        return anims[status];
    }
    
}
