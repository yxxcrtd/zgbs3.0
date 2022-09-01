package com.joyintech.tams.position.check;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface PositionCheck {

    default boolean checkForFL0007(Object req, Map msgMap){
        return false;
    }


}
