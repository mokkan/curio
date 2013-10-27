package com.wasome.curio.sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationState {
    
    private Animation anim;
    private Frame[] frames;
    private int currentFrame = 0;
    private float time = 0;
    private boolean reversed = false;
    private boolean paused = false;
    private boolean loop;
    
    public AnimationState(Animation anim, boolean loop) {
        
        this.anim = anim;
        this.loop = loop;
        this.reversed = false;
        
        frames = anim.getFrames().toArray(new Frame[anim.getFrames().size()]);
        time = frames[0].getTime();
    }
    
    public void flip(boolean hflip, boolean vflip) {
        for (int i = 0; i < frames.length; i++) {
            frames[i].getTextureRegion().flip(hflip, vflip);
        }
    }
    
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
        this.currentFrame = frames.length - 1;
    }
    
    public void pause() {
        paused = true;
    }
    
    public void resume() {
        paused = false;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void reset() {
        currentFrame = 0;
    }
    
    public void update(float dt) {
        if (paused) {
            return;
        }
        
        while (dt > time) {
            dt -= time;
            
            if (reversed) {
                if (currentFrame == 0 && loop) {
                    currentFrame = frames.length - 1;
                } else if (currentFrame != 0) {
                    currentFrame--;
                }
            } else {
                if (currentFrame == frames.length - 1 && loop) {
                    currentFrame = 0;
                } else if (currentFrame != frames.length - 1) {
                    currentFrame++;
                }
            }
            
            time = frames[currentFrame].getTime();
        }
        
        time -= dt;
    }
    
    public TextureRegion getCurrentFrame() {
        return anim.getFrames().get(currentFrame).getTextureRegion();
    }
    
    public String getPath() {
        return anim.getPath();
    }
    
    public Animation getRaw() {
        return anim;
    }
    
}
