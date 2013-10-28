package com.wasome.curio.components;

import com.artemis.Component;

public class Treasure extends Component {
    
    private int value;
    
    public Treasure(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
}
