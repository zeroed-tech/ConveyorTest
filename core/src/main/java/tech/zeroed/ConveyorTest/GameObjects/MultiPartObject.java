package tech.zeroed.ConveyorTest.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.ConveyorTest.utilities.Grid;
import tech.zeroed.ConveyorTest.utilities.GridNode;

public interface MultiPartObject {
    void setPoints(GridPoint2...points);
    void clearPoints();
    void buildAtPoint(Grid<GridNode<GameObject>> grid, GridPoint2...points);
}
