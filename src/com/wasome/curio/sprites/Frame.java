/*
 * Curio - A simple puzzle platformer game.
 * Copyright (C) 2014  Michael Swiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.wasome.curio.sprites;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Frame {

    private TextureRegion region;
    private float time;
    private boolean[][] collisionMap;
    
    public Frame(Texture sheet, int x, int y, int w, int h, float time) {
        this.time = time;
        
        region = new TextureRegion(sheet, x, y, w, h);
        
        // Extract pixmap from the texture to build collision map from
        region.getTexture().getTextureData().prepare();
        Pixmap pixels = region.getTexture().getTextureData().consumePixmap();
        
        collisionMap = new boolean[h][w];
        
        // Build the collision mask (alpha != 0 is collidable)
        for (int py = 0; py < h; py++) {
            for (int px = 0; px < w; px++) {
                int pixel = pixels.getPixel(x + px, y + py);

                if ((pixel & 0x000000FF) == 0) {
                    collisionMap[py][px] = false;
                } else {
                    collisionMap[py][px] = true;
                }
            }
        }
        
        // Dispose pixmap if necessary
        if (region.getTexture().getTextureData().disposePixmap()) {
            pixels.dispose();
        }
    }
    
    public TextureRegion getTextureRegion() {
        return region;
    }
    
    public float getTime() {
        return time;
    }
    
    public boolean[][] getCollisionMap() {
        return collisionMap;
    }
    
}
