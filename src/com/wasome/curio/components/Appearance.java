package com.wasome.curio.components;

import com.artemis.Component;
import com.wasome.curio.sprites.AnimationState;

public class Appearance extends Component {

    private AnimationState anim;
    private int order;
    
    public Appearance(AnimationState anim, int order) {
        this.anim = anim;
        this.order = order;
    }
    
    public AnimationState getAnimation() {
        return anim;
    }
    
    public void setAnimation(AnimationState anim) {
        this.anim = anim;
        this.anim.reset();
    }
    
    public int getOrder() {
        return order;
    }

}
