package com.wasome.curio.components;

import com.artemis.Component;

public class Size extends Component {
    
    private float w;
    private float h;
    
    public Size() {}
    
    public Size(float w, float h) {
        this.w = w;
        this.h = h;
    }
    
    public float getWidth() {
        return w;
    }
    
    public float getHeight() {
        return h;
    }
    
    public void setWidth(float w) {
        this.w = w;
    }
    
    public void setHeight(float h) {
        this.h = h;
    }
    
}
