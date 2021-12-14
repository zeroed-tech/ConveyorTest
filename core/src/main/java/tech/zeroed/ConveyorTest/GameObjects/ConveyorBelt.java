package tech.zeroed.ConveyorTest.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.ConveyorTest.ConveyorTest;
import tech.zeroed.ConveyorTest.GameObjects.Resources.Resource;
import tech.zeroed.ConveyorTest.utilities.Grid;
import tech.zeroed.ConveyorTest.utilities.GridNode;

public class ConveyorBelt extends GameObject implements MultiPartObject {
    /*
    Conveyor has an array of grid points representing the next link in the chain
    Items can only be added to the tail of the array (array[0]) and retrieved from the head(array[n])
    Each grid square can hold 1 item
    Items are moved in order from the head to the tail
     */

    Array<GridPoint2> conveyorPoints;

    public static int MAX_CAPACITY_PER_SEGMENT = 5;

    public ConveyorBelt() {
        super(1, 1, Dir.DOWN, "ConveyorBelt");
        multipartBuild = true;
        conveyorPoints = new Array<>();
    }

    public GridPoint2 getInput(){
        switch (direction) {
            case UP:    return new GridPoint2(conveyorPoints.get(0)).add(1, 0);
            case RIGHT: return new GridPoint2(conveyorPoints.get(0)).add(0, -1);
            case DOWN:  return new GridPoint2(conveyorPoints.get(0)).add(-1, 0);
            case LEFT:  return new GridPoint2(conveyorPoints.get(0)).add(0, 1);
            default:    return conveyorPoints.get(0);
        }
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        TextureRegion texture = animation.getKeyFrame(animationFrameTimer);
        int texWidth = texture.getRegionWidth();
        int texHeight = texture.getRegionHeight();

        Vector2 position = new Vector2();
        if(conveyorPoints.size == 0){
            spriteBatch.draw(texture, worldPosition.x, worldPosition.y, 0, 0, texWidth, texHeight, 1, 1, getAngleFromDir(direction));
            return;
        }
        for(GridPoint2 gridPoint : conveyorPoints){
            ConveyorTest.instance.grid.getWorldPosition(position, gridPoint.x, gridPoint.y);
            spriteBatch.draw(texture, position.x, position.y, 0, 0, texWidth, texHeight, 1, 1, getAngleFromDir(direction));
        }
    }

    @Override
    public void update(float deltaTime) {
        GridPoint2 inputPosition = getInput();
        if(!ConveyorTest.instance.grid.isValid(inputPosition)) return;// There isn't a valid node here
        GridNode<GameObject> inputNode =  ConveyorTest.instance.grid.getValue(inputPosition);
        if(!inputNode.hasEntity()) return;// Theres nothing on this node
        if(!(inputNode.entity instanceof HasOutputs)) return;// This node does not have an output

        // This is a valid input object, try and extract a resource
        HasOutputs inputObject = (HasOutputs) inputNode.entity;
        Resource collectedResources = inputObject.retrieveObjects(0, Math.min(getAvailableStorage(), MAX_CAPACITY_PER_SEGMENT));
        if(collectedResources == null || collectedResources.quantity == 0) return; // No resource was returned

        // Store retrieved items
        storage.add(collectedResources);
    }

    @Override
    public void setPoints(GridPoint2... points) {
        conveyorPoints.clear();
        conveyorPoints.addAll(points);
    }

    @Override
    public void clearPoints() {
        conveyorPoints.clear();
    }

    @Override
    public void buildAtPoint(Grid<GridNode<GameObject>> grid, GridPoint2... points) {
        setPoints(points);


        for (GridPoint2 gridPoint : points) {
            GridNode<GameObject> node = grid.getValue(gridPoint);
            node.setEntity(this);
            Gdx.app.log("Placed", node+" "+direction);
        }

        setObjectPosition(points[0]);
    }
}
