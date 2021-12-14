package tech.zeroed.ConveyorTest;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import tech.zeroed.ConveyorTest.GameObjects.*;
import tech.zeroed.ConveyorTest.utilities.AssetLoader;
import tech.zeroed.ConveyorTest.utilities.Grid;
import tech.zeroed.ConveyorTest.utilities.GridNode;

import java.util.HashMap;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ConveyorTest extends ApplicationAdapter {
    public AssetLoader loader = new AssetLoader();
    public TextureAtlas atlas;
    public Grid<GridNode<GameObject>> grid;

    public static final int CELL_SIZE = 32;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private HUD hud;

    public int placeId = 1;
    private GameObject.Dir direction = GameObject.Dir.DOWN;

    private Array<GameObject> objects;

    private float globalTick = 0;
    private float globalTickRate = 0.1f;

    private SpriteBatch spriteBatch;

    private GameObject buildGhost = null;
    private HashMap<Integer, GameObject> buildGhosts;
    private boolean canBuildOnCurrentCell = false;
    private boolean multiPartBuildInProgress = false;
    private GridPoint2 multiPartBuildStartPoint = new GridPoint2();
    private GridPoint2 multiPartBuildHoverPoint = new GridPoint2();
    private BuildMode currentBuildMode = BuildMode.RightDown;
    public static ConveyorTest instance;



    private Color blockedTint = new Color(1f,0f,0f,0.5f);
    private Color allowedTint = new Color(1f,1f,1f,0.5f);


    enum BuildMode {
        RightDown,
        LeftUp
    }

    private BuildMode nextBuildMode(BuildMode buildMode){
        switch (buildMode){
            case RightDown: return  BuildMode.LeftUp;
            case LeftUp: return BuildMode.RightDown;
            default: return buildMode;
        }
    }

    @Override
    public void create() {
        super.create();
        instance = this;
        VisUI.load();
        grid = new Grid<>();
        grid.initialise((Gdx.graphics.getWidth() - 60)/CELL_SIZE, (Gdx.graphics.getHeight() - 150)/CELL_SIZE, 32, new Vector2(-(Gdx.graphics.getWidth() - 60)/2f, -(Gdx.graphics.getHeight() - 110)/2f + 80), GridNode::new);
        grid.enableDebug();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setAutoShapeType(true);

        loader.loadAssets();
        atlas = loader.get("Images/Sprites.atlas", TextureAtlas .class);
        Stage stage = new Stage();
        objects = new Array<>();

        InputMultiplexer multiplexer = new InputMultiplexer(stage);
        Gdx.input.setInputProcessor(multiplexer);

        hud = new HUD(stage, loader);
        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);

        buildGhosts = new HashMap<>();
        buildGhosts.put(1, new ResourceNode());
        buildGhosts.put(2, new ResourceExtractor());
        buildGhosts.put(3, new ConveyorBelt());
        buildGhosts.put(4, new Nothing());

        buildGhost = buildGhosts.get(1);
    }

    private void update(){
        handleMouseInput();
        float deltaTime = Gdx.graphics.getDeltaTime();
        GameObject.animationFrameTimer += deltaTime;
        globalTick -= deltaTime;
        if(globalTick < 0) {
            for (GameObject gameObject : objects) {
                gameObject.update(deltaTime);
            }
            globalTick += globalTickRate;
        }
    }

    public void setPlaceId(int placeId){
        if(this.placeId != placeId){
            multiPartBuildInProgress = false;
            multiPartBuildStartPoint.set(-1,-1);
            multiPartBuildHoverPoint.set(-1,-1);
            this.placeId = placeId;
        }
    }

    private void handleMouseInput(){
        // Check that the mouse is actually over the grid
        Vector3 mouseWorldPosition = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        GridPoint2 mouseCellPosition = grid.getCellPosition(mouseWorldPosition);
        // Return if it's not
        if(!grid.isValid(mouseCellPosition)) return;

        // Select new building type
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) setPlaceId(1);
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) setPlaceId(2);
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) setPlaceId(3);
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) setPlaceId(4);
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
            direction = GameObject.getNextDir(direction);
            Gdx.graphics.setTitle(direction.name());
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)) currentBuildMode = nextBuildMode(currentBuildMode);

        // Retrieve the correct object ID and update its position
        buildGhost = buildGhosts.get(placeId);
        buildGhost.direction = direction;
        buildGhost.setObjectPosition(mouseCellPosition);

        if(multiPartBuildInProgress){
            multiPartBuildHoverPoint.set(mouseCellPosition);
            if(buildGhost instanceof MultiPartObject){
                MultiPartObject object = (MultiPartObject) buildGhost;
                Array<GridPoint2> gridPoints = new Array<>();
                int incrementerX = multiPartBuildStartPoint.x < multiPartBuildHoverPoint.x ? 1 : -1;
                int incrementerY = multiPartBuildStartPoint.y < multiPartBuildHoverPoint.y ? 1 : -1;
                switch (currentBuildMode){
                    case RightDown:
                        // Dragging to the right
                        for(int x = multiPartBuildStartPoint.x; x != multiPartBuildHoverPoint.x+incrementerX; x+=incrementerX){
                            gridPoints.add(new GridPoint2(x, multiPartBuildStartPoint.y));
                        }
                        for(int y = multiPartBuildStartPoint.y; y != multiPartBuildHoverPoint.y+incrementerY; y+=incrementerY){
                            gridPoints.add(new GridPoint2(multiPartBuildHoverPoint.x, y));
                        }
                        break;
                    case LeftUp:
                        for(int y = multiPartBuildStartPoint.y; y != multiPartBuildHoverPoint.y+incrementerY; y+=incrementerY){
                            gridPoints.add(new GridPoint2(multiPartBuildStartPoint.x, y));
                        }
                        for(int x = multiPartBuildStartPoint.x; x != multiPartBuildHoverPoint.x+incrementerX; x+=incrementerX){
                            gridPoints.add(new GridPoint2(x, multiPartBuildHoverPoint.y));
                        }
                        break;
                }
                object.setPoints(gridPoints.toArray(GridPoint2.class));

                boolean clear = true;
                for (GridPoint2 gridPoint : gridPoints) {
                    GridNode<GameObject> gridNode = grid.getValue(gridPoint);
                    if (gridNode != null && !gridNode.canBuild()) {
                        clear = false;
                        break;
                    }
                }

                canBuildOnCurrentCell = clear;

                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    // Finalise the build

                    if(placeId == 3){
                        ConveyorBelt belt = new ConveyorBelt();
                        belt.buildAtPoint(grid, gridPoints.toArray(GridPoint2.class));
                        objects.add(belt);
                    }
                    multiPartBuildInProgress = false;
                    multiPartBuildStartPoint.set(0,0);
                    object.clearPoints();
                }
            }
        }else {
            // Check if there is room to place the selected object or the start of a multi build
            Array<GridPoint2> gridPoints = buildGhost.getOccupiedPositionList(mouseCellPosition, direction);
            // Check that all needed grid points are clear
            boolean clear = true;
            for (GridPoint2 gridPoint : gridPoints) {
                GridNode<GameObject> gridNode = grid.getValue(gridPoint);
                if (gridNode == null || !gridNode.canBuild()) {
                    clear = false;
                    break;
                }
            }

            canBuildOnCurrentCell = clear;

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

                // If this isn't a multi part build then we can build it now
                if (!buildGhost.multipartBuild) {
                    // If the current location isn't clear then there's no point continuing
                    if (!clear) return;

                    // Node is empty, place an object
                    GameObject newObject;
                    switch (placeId) {
                        case 1:
                            newObject = new ResourceNode();
                            break;
                        case 2:
                            newObject = new ResourceExtractor();
                            break;
                        case 4:
                            newObject = new Nothing();
                            break;
                        default:
                            return;
                    }

                    newObject.buildAtPoint(grid, mouseCellPosition, direction);
                    objects.add(newObject);
                } else {
                    // Building a multipart object
                    multiPartBuildInProgress = true;
                    multiPartBuildStartPoint.set(mouseCellPosition);
                }
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update();
        super.render();

        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        for (GameObject gameObject : objects) {
            gameObject.draw(spriteBatch);
        }

        if(buildGhost != null){
            spriteBatch.setColor(canBuildOnCurrentCell ? allowedTint : blockedTint);
            buildGhost.draw(spriteBatch);
        }

        spriteBatch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        grid.debugDraw();
        shapeRenderer.begin();
        for (GameObject gameObject : objects) {
            gameObject.debugDraw(shapeRenderer);
        }

        if(buildGhost != null){
            spriteBatch.setColor(canBuildOnCurrentCell ? allowedTint : blockedTint);
            buildGhost.debugDraw(shapeRenderer);
        }

        for(int x = 0; x < grid.getWidth(); x++){
            for(int y = 0; y < grid.getHeight(); y++){
                if(grid.isValid(x,y) && grid.getValue(x,y).hasEntity()){
                    Vector2 position = grid.getWorldPosition(x,y);
                    shapeRenderer.rect(position.x, position.y, grid.getCellSize(), grid.getCellSize());
                }
            }
        }

        shapeRenderer.end();

        hud.update(Gdx.graphics.getDeltaTime());
    }
}