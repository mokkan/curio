package com.wasome.curio.components;

import com.artemis.Component;

public class Item extends Component {

    private String type;
    
    public Item(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
}
