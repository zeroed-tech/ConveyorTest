package tech.zeroed.ConveyorTest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.*;
import tech.zeroed.ConveyorTest.utilities.AssetLoader;

public class HUD {
    private Stage stage;
    private AssetLoader assetManager;


    private VisTable mainTable;

    private Label coreStatus;
    private VisTable subtable;

    private boolean buildingBarShown = false;

    public HUD(Stage stage, AssetLoader assetManager) {
        this.stage = stage;
        this.assetManager = assetManager;

        mainTable = new VisTable();

        // Space for middle of the screen
        mainTable.add().grow().row();
        //mainTable.add().height(100).right().row();
        subtable = new VisTable();
        mainTable.add(subtable).growX().row();

        // Create bottom bar
        mainTable.add(createBottomBar()).growX().bottom().row();
        mainTable.setFillParent(true);
        //mainTable.debugAll();
        stage.addActor(mainTable);
    }

    private TextureRegionDrawable loadDrawableFromAtlas(String drawableName){
        return new TextureRegionDrawable(assetManager.get("Images/Sprites.atlas", TextureAtlas.class).findRegion(drawableName));
    }

    private VisImageButton createButton(String buttonImage, String tooltip, ChangeListener changeListener){
        TextureRegionDrawable btnImage = loadDrawableFromAtlas(buttonImage);
        if(btnImage.getRegion() == null) {
            Gdx.app.log("HUD", "Image texture failed to load");
            return null;
        }
        VisImageButton button = new VisImageButton(btnImage);
        button.addListener(changeListener);
        new Tooltip.Builder(tooltip).target(button).build();
        return button;
    }

    private void setBuildType(int typeId){
        ConveyorTest.instance.setPlaceId(typeId);
    }

    private VisTable createBottomBar() {
        VisTable bar = new VisTable();
        float buttonWidth = 100;
        float buttonHeight = 100;

        bar.add(createButton("Resource", "Create Resource", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setBuildType(1);
            }
        })).width(buttonWidth).height(buttonHeight).uniformX().pad(5);

        bar.add(createButton("Extractor", "Create Resource Extractor", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setBuildType(2);
            }
        })).width(buttonWidth).height(buttonHeight).uniformX().pad(5);


        bar.add(createButton("ConveyorBelt", "Create ConveyorBelt", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setBuildType(3);
            }
        })).width(buttonWidth).height(buttonHeight).uniformX().pad(5);

//        bar.add(createButton("Resource", "TODO", new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//            }
//        })).width(buttonWidth).height(buttonHeight).expandX().uniformX().pad(5);


        // Set background
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0x3f3f3fAA);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        bgPixmap.dispose();
        bar.add().expandX();

        bar.setBackground(textureRegionDrawableBg);
        return bar;
    }

    public void update(float delta){
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }
}
