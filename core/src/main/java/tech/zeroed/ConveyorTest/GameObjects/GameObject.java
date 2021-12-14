package tech.zeroed.ConveyorTest.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.ConveyorTest.ConveyorTest;
import tech.zeroed.ConveyorTest.utilities.Grid;
import tech.zeroed.ConveyorTest.utilities.GridNode;

public class GameObject {
    public enum Dir {
        DOWN,
        UP,
        LEFT,
        RIGHT
    }

    public int width;
    public int height;
    public Dir direction;
    public String name;
    public Color fillColor = Color.RED;
    protected GridPoint2 originPosition;
    protected Vector2 worldPosition;
    protected Animation<TextureRegion> animation;
    public static float animationFrameTimer;
    // If true, duplicates of this object will be placed between two points
    public boolean multipartBuild;

    public GameObject(){
        this(1, 1, Dir.DOWN, "GameObject");
    }

    public static Dir getNextDir(Dir dir){
        switch (dir) {
            case UP: return Dir.RIGHT;
            case RIGHT: return Dir.DOWN;
            case DOWN: return Dir.LEFT;
            case LEFT: return Dir.UP;
            default: return dir;
        }
    }

    public int getAngleFromDir(Dir dir){
        switch (dir) {
            case UP: return 180;
            case RIGHT: return 90;
            case DOWN: return 0;
            case LEFT: return 270;
            default: return 0;
        }
    }

    public Vector2 getRotationOffset(Dir dir){
        switch (dir) {
            case DOWN: return new Vector2(0, 0);
            case LEFT: return new Vector2(0, 1);
            case UP: return new Vector2(1, 1);
            case RIGHT: return new Vector2(1, 0);
            default: return new Vector2(0, 0);
        }
    }

    public GameObject(int width, int height, Dir direction, String name){
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.name = name;
        animation = new Animation<>(1 / 8f, ConveyorTest.instance.atlas.findRegions(name), Animation.PlayMode.LOOP);
        originPosition = new GridPoint2();
        worldPosition = new Vector2();
        multipartBuild = false;
    }

    public Array<GridPoint2> getOccupiedPositionList(GridPoint2 offset, Dir dir){
        Array<GridPoint2> gridPoints = new Array<>();
        switch (dir) {
            default:
            case UP:
                for (int x = -width+1; x <= 0 ; x++) {
                    for (int y = -height+1; y <= 0; y++) {
                        gridPoints.add(new GridPoint2(offset).add(x, y));
                    }
                }
                break;
            case DOWN:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        gridPoints.add(new GridPoint2(offset).add(x, y));
                    }
                }
                break;
            case LEFT:
                for (int x = 0; x < height; x++) {
                    for (int y = -width+1; y <= 0; y++) {
                        gridPoints.add(new GridPoint2(offset).add(x, y));
                    }
                }
                break;
            case RIGHT:
                for (int x = -height+1; x <= 0 ; x++) {
                    for (int y = 0; y < width; y++) {
                        gridPoints.add(new GridPoint2(offset).add(x, y));
                    }
                }
                break;
        }
        return gridPoints;
    }

    // Set direction first
    public void setObjectPosition(GridPoint2 origin){
        this.originPosition.set(origin);
        Vector2 offset = getRotationOffset(direction);
        offset.x *= ConveyorTest.CELL_SIZE;
        offset.y *= ConveyorTest.CELL_SIZE;

        Vector2 worldPos = ConveyorTest.instance.grid.getWorldPosition(originPosition);
        worldPos.add(offset);
        worldPosition.set(worldPos);
    }

    public void buildAtPoint(Grid<GridNode<GameObject>> grid, GridPoint2 point, Dir direction){
        this.direction = direction;

        Array<GridPoint2> gridPoints = getOccupiedPositionList(point, direction);
        Gdx.app.log("Positions", gridPoints.toString());

        for (GridPoint2 gridPoint : gridPoints) {
            GridNode<GameObject> node = grid.getValue(gridPoint);
            node.setEntity(this);
            Gdx.app.log("Placed", node+" "+direction);
        }

        setObjectPosition(point);
        Gdx.app.log("SetOrigin", originPosition.toString());
        Gdx.app.log("Offset", getRotationOffset(direction).toString());
    }

    public void update(float deltaTime){
    }

    public void draw(SpriteBatch spriteBatch){
        TextureRegion texture = animation.getKeyFrame(animationFrameTimer);
        spriteBatch.draw(texture, worldPosition.x, worldPosition.y, 0, 0, texture.getRegionWidth(), texture.getRegionHeight(), 1, 1, getAngleFromDir(direction));
    }

    public void debugDraw(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(worldPosition.x, worldPosition.y, 2);
    }

    @Override
    public String toString() {
        return name;
    }
}
