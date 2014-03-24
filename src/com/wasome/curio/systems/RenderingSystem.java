package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.sprites.AnimationState;
import com.wasome.curio.sprites.Frame;

public class RenderingSystem extends EntitySystem {
    
    private @Mapper ComponentMapper<Position> positionMapper;
    private @Mapper ComponentMapper<Size> sizeMapper;
    private @Mapper ComponentMapper<Appearance> appearanceMapper;
    private OrthographicCamera cam;
    private SpriteBatch batch;

    @SuppressWarnings("unchecked")
    public RenderingSystem(OrthographicCamera cam) {
        super(Aspect.getAspectForAll(Position.class, Size.class, 
                                     Appearance.class));
        batch = new SpriteBatch();
        this.cam = cam;
    }
    
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        batch.setProjectionMatrix(cam.combined);
        
        // Render background entities
        batch.begin();
        for (int i = 0, s = entities.size(); s > i; i++) {
            Appearance appearance = appearanceMapper.get(entities.get(i));
            
            if (appearance.getOrder() == 0) {
                update(entities.get(i));
            }
        }
        batch.end();

        // Render foreground entities
        batch.begin();
        for (int i = 0, s = entities.size(); s > i; i++) {
            Appearance appearance = appearanceMapper.get(entities.get(i));
            
            if (appearance.getOrder() == 1) {
                update(entities.get(i));
            }
        }
        batch.end();
    }

    protected void update(Entity e) {
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        Appearance appearance = appearanceMapper.get(e);
        AnimationState anim = appearance.getAnimation();
        
        // Don't need to do anything if animation doesn't exist yet
        if (anim == null) {
            return;
        }
        
        anim.update(world.getDelta() / 1000);
        
        // Get the appropriate dimensions/coords for the anim (flip/no flip)
        float w = size.getWidth();
        float h = size.getHeight();
        
        if (anim.isHorizFlipped()) {
            w *= -1;
        }
        
        if (anim.isVertFlipped()) {
            h *= -1;
        }
        
        float x = pos.getX() - w / 2;
        float y = pos.getY() - h / 2;

        // Draw the current frame
        Frame currentFrame = anim.getCurrentFrame();
        batch.draw(currentFrame.getTextureRegion(), x, y, w, h);
    }
    
    @Override
    protected boolean checkProcessing() {
            return true;
    }

}
