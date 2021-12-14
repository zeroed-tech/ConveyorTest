package tech.zeroed.ConveyorTest.GameObjects;

import com.badlogic.gdx.graphics.Color;

public class Nothing extends GameObject{
    public Nothing() {
        super(2, 2, Dir.DOWN, "Nothing");
        fillColor = Color.BLUE;
    }
}
