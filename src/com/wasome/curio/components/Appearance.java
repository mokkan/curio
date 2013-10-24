package com.wasome.curio.components;

import com.artemis.Component;
import com.wasome.curio.sprites.AnimationState;

public class Appearance extends Component {

    private AnimationState anim;
    
    public Appearance(AnimationState anim) {
        this.anim = anim;
    }
    
    public AnimationState getAnimation() {
        return anim;
    }
    
    public void setAnimation(AnimationState anim) {
        this.anim = anim;
        this.anim.reset();
    }

}
