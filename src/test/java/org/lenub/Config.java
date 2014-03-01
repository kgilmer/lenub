package org.lenub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final public class Config {
    private final int mySetting;
    
    @JsonCreator
    public Config(@JsonProperty("mySetting") int mySetting) {
        this.mySetting = mySetting;
    }
    
    public int getMySetting() {
        return mySetting;
    }
}
