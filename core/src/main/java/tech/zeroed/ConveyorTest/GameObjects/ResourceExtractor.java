package tech.zeroed.ConveyorTest.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.ConveyorTest.ConveyorTest;
import tech.zeroed.ConveyorTest.GameObjects.Resources.Resource;
import tech.zeroed.ConveyorTest.utilities.GridNode;

public class ResourceExtractor extends GameObject implements HasInputs, HasOutputs{

    private Array<Resource> storage;
    private static int MAX_CAPACITY = 100;
    private static int EXTRACTION_RATE = 2;

    public ResourceExtractor() {
        super(2, 1, Dir.DOWN, "Extractor");
        fillColor = Color.GREEN;
        storage = new Array<>();
    }

    public GridPoint2 getInput(){
        switch (direction) {
            case UP:    return new GridPoint2(originPosition).add(1, 0);
            case RIGHT: return new GridPoint2(originPosition).add(0, -1);
            case DOWN:  return new GridPoint2(originPosition).add(-1, 0);
            case LEFT:  return new GridPoint2(originPosition).add(0, 1);
            default:    return originPosition;
        }
    }

    public GridPoint2 getOutput(){
        switch (direction) {
            case UP:    return new GridPoint2(originPosition).add(-1, 0);
            case RIGHT: return new GridPoint2(originPosition).add(0, -1);
            case DOWN:  return new GridPoint2(originPosition).add(1, 0);
            case LEFT:  return new GridPoint2(originPosition).add(0, 1);
            default:    return originPosition;
        }
    }

    public int getAvailableStorage(){
        int itemsInStorage = 0;
        for(Resource r : storage){
            itemsInStorage+=r.quantity;
        }
        return MAX_CAPACITY - itemsInStorage;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        GridPoint2 inputPosition = getInput();
        if(!ConveyorTest.instance.grid.isValid(inputPosition)) return;// There isn't a valid node here
        GridNode<GameObject> inputNode =  ConveyorTest.instance.grid.getValue(inputPosition);
        if(!inputNode.hasEntity()) return;// Theres nothing on this node
        if(!(inputNode.entity instanceof HasOutputs)) return;// This node does not have an output

        // This is a valid input object, try and extract a resource
        HasOutputs inputObject = (HasOutputs) inputNode.entity;
        Resource collectedResources = inputObject.retrieveObjects(0, Math.min(getAvailableStorage(), EXTRACTION_RATE));
        if(collectedResources == null || collectedResources.quantity == 0) return; // No resource was returned

        // Store retrieved items
        storage.add(collectedResources);
        Gdx.app.log("ResourceExtractor", "Collected "+collectedResources.quantity+" "+collectedResources.type+". Capacity now at ["+(MAX_CAPACITY-getAvailableStorage())+"/"+MAX_CAPACITY+"]");
    }

    @Override
    public int getNumberOfInputs() {
        return 1;
    }

    @Override
    public int getFreeSpaceOnSlot(int slot) {
        return 0;
    }

    @Override
    public void inputObjectsToSlot(int slot, GameObject... object) {

    }

    @Override
    public int getNumberOfOutputs() {
        return 1;
    }

    @Override
    public int getNumberOfObjectsAvailable(int slot) {
        return 0;
    }

    @Override
    public Resource retrieveObjects(int slot, int count) {
        return null;
    }
}
