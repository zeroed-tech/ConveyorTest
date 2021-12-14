package tech.zeroed.ConveyorTest.GameObjects;

import tech.zeroed.ConveyorTest.GameObjects.Resources.Resource;

public interface HasOutputs {
    // Returns the number of output slots this object has
    int getNumberOfOutputs();
    // Returns the number of objects available on this slot
    int getNumberOfObjectsAvailable(int slot);
    // Retrieves up to count objects from this slot
    Resource retrieveObjects(int slot, int count);
}
