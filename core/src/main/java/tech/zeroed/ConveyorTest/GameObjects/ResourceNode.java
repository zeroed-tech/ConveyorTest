package tech.zeroed.ConveyorTest.GameObjects;

import com.badlogic.gdx.graphics.Color;
import tech.zeroed.ConveyorTest.GameObjects.Resources.Resource;

public class ResourceNode extends GameObject implements HasOutputs{

    public ResourceNode() {
        super(1, 1, Dir.DOWN, "Resource");
        fillColor = Color.BLUE;
    }

    @Override
    public int getNumberOfOutputs() {
        return 1;
    }

    @Override
    public int getNumberOfObjectsAvailable(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public Resource retrieveObjects(int slot, int count) {
        return new Resource(Resource.Type.Slime, count);
    }
}
