package tech.zeroed.ConveyorTest.GameObjects;

public interface HasInputs {
    // Return the number of input slots this object has
    int getNumberOfInputs();
    // Return the number of items the specified slot has room for
    int getFreeSpaceOnSlot(int slot);
    // Add a number of game objects to the specified slot
    void inputObjectsToSlot(int slot, GameObject...object);
}
