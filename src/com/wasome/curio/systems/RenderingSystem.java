package com.wasome.curio.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.wasome.curio.components.Position;

public class RenderingSystem extends EntityProcessingSystem {
    
    private @Mapper ComponentMapper<Position> posMap;
    private OrthographicCamera cam;
    private ShapeRenderer shapeRenderer;

    @SuppressWarnings("unchecked")
    public RenderingSystem(OrthographicCamera cam) {
        super(Aspect.getAspectForAll(Position.class));
        shapeRenderer = new ShapeRenderer();
        this.cam = cam;
    }

    @Override
    protected void process(Entity e) {
        // Get compoments from the entity using compoment mapper
        Position pos = posMap.get(e);
        
        // Draw the entity
        // TODO: Actual rendering. Currently renders 16x16 box at position.
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(pos.getX(), pos.getY(), 16, 16);
        shapeRenderer.end();
    }

}
