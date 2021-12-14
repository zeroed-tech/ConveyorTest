package tech.zeroed.ConveyorTest.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.reflect.ArrayReflection;

public class Grid<TGridObject> {
    private int width;
    private int height;
    private float cellSize;
    private Vector2 origin;
    private TGridObject[][] gridArray;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private boolean debug;

    public void initialise(int width, int height, float cellSize, Vector2 origin, FuncMagic.Function3<Grid<TGridObject>, Integer, Integer, TGridObject> defaultValueConstructor){
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        this.origin = origin;

        gridArray = (TGridObject[][])new Object[width][height];
        for(int y = height-1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                gridArray[x][y] = defaultValueConstructor.apply(this, x, y);
            }
        }
    }

    public void enableDebug(){
        debug = true;
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void debugDraw(){
        debugDraw(camera);
    }

    public void debugDraw(Camera camera){
        if(!debug){
            Gdx.app.error("GRID", "Attempted to debugDraw without enabling debugging");
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f,1f,0f, 0.2f);

        for(int y = height-1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                shapeRenderer.line(getWorldPosition(x, y), getWorldPosition(x+1, y));
                shapeRenderer.line(getWorldPosition(x, y), getWorldPosition(x, y+1));
            }
        }
        shapeRenderer.line(getWorldPosition(0, height), getWorldPosition(width, height));
        shapeRenderer.line(getWorldPosition(width, 0), getWorldPosition(width, height));
        shapeRenderer.end();
    }

    public Vector2 getWorldPosition(GridPoint2 gridPoint){
        return getWorldPosition(gridPoint.x, gridPoint.y);
    }

    public Vector2 getWorldPosition(int x, int y){
        return getWorldPosition(new Vector2(), x, y);
    }

    public Vector2 getWorldPosition(Vector2 result, int x, int y){
        return result.set(x * cellSize, y * cellSize).add(origin);
    }

    public Vector2 getWorldPositionCenter(Vector2 cellPosition){
        return getWorldPosition(cellPosition, (int)cellPosition.x, (int)cellPosition.y).add(cellSize/2, cellSize/2);
    }

    /**
     * Converts the passed in Vector2 from world coordinates to grid coordinates, overwriting the original Vector2
     * @param worldPosition Vector2 containing world coordinates
     * @return Updated vector2 (for chaining). Value is set to -1,-1 if world position is outside the grid
     */
    public GridPoint2 getCellPosition(Vector2 worldPosition){
        worldPosition.sub(origin);
        worldPosition.set(MathUtils.floor(worldPosition.x/cellSize), MathUtils.floor(worldPosition.y/cellSize));
        GridPoint2 gridPosition = new GridPoint2((int)worldPosition.x, (int)worldPosition.y);
        if(!isValid(gridPosition))
            gridPosition.set(-1, -1);
        return gridPosition;
    }

    public GridPoint2 getCellPosition(Vector3 worldPosition){
        return getCellPosition(new Vector2(worldPosition.x, worldPosition.y));
    }

//    public Vector2 worldPositionToCellCenterWorldPosition(Vector2 worldPosition) {
//        // Convert world position to grid co-ordinates to lock it to a grid boundary
//        Vector2 cellPosition = getCellPosition(worldPosition);
//        worldPosition = getWorldPositionCenter(cellPosition);
//        return worldPosition;
//    }

    public void setValue(int x, int y, TGridObject value){
        if(isValid(x, y)) {
            gridArray[x][y] = value;
        }
    }

    public void setValue(Vector2 worldPosition, TGridObject value){
        getCellPosition(worldPosition);
        setValue((int)worldPosition.x, (int)worldPosition.y, value);
    }

    public TGridObject getValue(int x, int y){
        if(x >= 0 && y >= 0 && x < width && y < height)
            return gridArray[x][y];
        return null;
    }

    public TGridObject getValue(GridPoint2 worldPosition){
        return getValue(worldPosition.x, worldPosition.y);
    }

    public boolean isValid(GridPoint2 position){
        return isValid(position.x, position.y);
    }

    public boolean isValid(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public TGridObject[] getNeighbours(int x, int y, Class clazz){
        TGridObject[] neighbours = (TGridObject[]) ArrayReflection.newInstance(clazz, 4);
        neighbours[0] = isValid(x-1, y) ? getValue(x-1, y) : null;
        neighbours[1] = isValid(x+1, y) ? getValue(x+1, y) : null;
        neighbours[2] = isValid(x, y-1) ? getValue(x, y-1) : null;
        neighbours[3] = isValid(x, y+1) ? getValue(x, y+1) : null;
        return neighbours;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getCellSize() {
        return cellSize;
    }

    public static int GetGridDistance(int x1, int y1, int x2, int y2){
        int xDistance = Math.abs(x1 - x2);
        int yDistance = Math.abs(y1 - y2);

        return xDistance+yDistance;
    }

    public static int GetGridDistance(Vector2 pos1, Vector2 pos2){
        return GetGridDistance((int)pos1.x, (int)pos1.y, (int)pos2.x, (int)pos2.y);
    }

    public void clamp(Vector2 target) {
        target.x = (int)MathUtils.clamp(target.x, 0, getWidth()-1);
        target.y = (int)MathUtils.clamp(target.y, 0, getHeight()-1);
    }
}
