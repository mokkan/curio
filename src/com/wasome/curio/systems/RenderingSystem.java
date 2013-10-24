package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wasome.curio.components.Appearance;
import com.wasome.curio.components.Position;
import com.wasome.curio.components.Size;
import com.wasome.curio.sprites.AnimationState;

public class RenderingSystem extends EntityProcessingSystem {
    
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
    protected void process(Entity e) {
        // Get compoments from the entity using compoment mapper
        Position pos = positionMapper.get(e);
        Size size = sizeMapper.get(e);
        Appearance appearance = appearanceMapper.get(e);
        AnimationState anim = appearance.getAnimation();
        
        if (anim == null) {
            return;
        }
        
        anim.update(world.getDelta() / 1000);
        
        float halfW = size.getWidth() / 2;
        float halfH = size.getHeight() / 2;
        
        float x = pos.getX() - halfW;
        float y = pos.getY() - halfH;
        
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(anim.getCurrentFrame(), x, y); 
        batch.end();
    }

}
