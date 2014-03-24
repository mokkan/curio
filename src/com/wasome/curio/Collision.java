package com.wasome.curio;

import com.artemis.Entity;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.sprites.AnimationState;
import com.wasome.curio.sprites.Frame;

public class Collision {
    
    public static boolean pixelCollision(Entity e1, Entity e2) {
        Position pos1 = e1.getComponent(Position.class);
        Position pos2 = e2.getComponent(Position.class);
        
        AnimationState anim1 = e1.getComponent(Appearance.class).getAnimation();
        AnimationState anim2 = e2.getComponent(Appearance.class).getAnimation();

        Frame f1 = anim1.getCurrentFrame();
        Frame f2 = anim2.getCurrentFrame();

        int dx = (int) -(pos2.getX() - pos1.getX());
        int dy = (int) (pos2.getY() - pos1.getY());

        int w1 = f1.getTextureRegion().getRegionWidth();
        int h1 = f1.getTextureRegion().getRegionHeight();
        int w2 = f1.getTextureRegion().getRegionWidth();
        int h2 = f1.getTextureRegion().getRegionHeight();

        boolean[][] m1 = f1.getCollisionMap();
        boolean[][] m2 = f2.getCollisionMap();

        for (int py1 = 0; py1 < h1; py1++) {
            for (int px1 = 0; px1 < w1; px1++) {
                int px2 = px1 + dx;
                int py2 = py1 + dy;
                
                if (px2 >= w2 || px2 < 0 || py2 >= h2 || py2 < 0) {
                    continue;
                }
                
                if (m1[py1][px1] && m2[py2][px2]) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean boxCollision(Entity e1, Entity e2) {
        Position p1 = e1.getComponent(Position.class);
        Position p2 = e2.getComponent(Position.class);
        
        Size s1 = e1.getComponent(Size.class);
        Size s2 = e2.getComponent(Size.class);
        
        float x1 = p1.getX() - s1.getWidth()/2;
        float y1 = p1.getY() - s1.getHeight()/2;
        float w1 = s1.getWidth();
        float h1 = s1.getHeight();
        
        float x2 = p2.getX() - s2.getWidth()/2;
        float y2 = p2.getY() - s2.getHeight()/2;
        float w2 = s2.getWidth();
        float h2 = s2.getHeight();

        return (Math.abs(x1 - x2) * 2 < (w1 + w2)) &&
                (Math.abs(y1 - y2) * 2 < (h1 + h2));
    }
    
}
