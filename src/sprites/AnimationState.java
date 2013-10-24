package sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationState {
    
    private Animation anim;
    private Frame[] frames;
    private int currentFrame = 0;
    private float time = 0;
    private boolean reversed = false;
    
    public AnimationState(Animation anim, boolean hflip, boolean vflip,
            boolean reversed) {
        
        this.anim = anim;
        this.reversed = reversed;
        
        frames = anim.getFrames().toArray(new Frame[anim.getFrames().size()]);
        time = frames[0].getTime();
        
        for (int i = 0; i < frames.length; i++) {
            frames[i].getTextureRegion().flip(hflip, vflip);
        }
    }
    
    public void update(float dt) {
        while (dt > time) {
            dt -= time;
            
            if (reversed) {
                if (currentFrame == 0) {
                    currentFrame = frames.length - 1;
                } else {
                    currentFrame--;
                }
            } else {
                if (currentFrame == frames.length - 1) {
                    currentFrame = 0;
                } else {
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
    
}
