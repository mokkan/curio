package com.wasome.curio;

import com.wasome.curio.sprites.Animation;
import com.wasome.curio.sprites.AnimationState;

public class InventoryItem {
    
    private String type;
    private AnimationState anim;
    
    public InventoryItem(String type, Animation anim) {
        this.type = type;
        this.anim = new AnimationState(anim, false, false, false);
    }
    
    public String getType() {
        return type;
    }
    
    public String getAnimationPath() {
        return anim.getPath();
    }
    
    public AnimationState getAnimation() {
        return anim;
    }
    
}
