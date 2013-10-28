package com.wasome.curio.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Frame {

    private TextureRegion region;
    private float time;
    
    public Frame(Texture sheet, int x, int y, int w, int h, float time) {
        this.time = time;
        
        region = new TextureRegion(sheet, x, y, w, h);
    }
    
    public TextureRegion getTextureRegion() {
        return region;
    }
    
    public float getTime() {
        return time;
    }
    
}
