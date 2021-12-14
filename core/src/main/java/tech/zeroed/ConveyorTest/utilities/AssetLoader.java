package tech.zeroed.ConveyorTest.utilities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetLoader extends AssetManager {
    public void loadAssets() {
        load("Images/SpriteSheet.atlas", TextureAtlas.class);
        load("Images/Sprites.atlas", TextureAtlas.class);

        //Load particles
        //ParticleEffectLoader.ParticleEffectParameter pep = new ParticleEffectLoader.ParticleEffectParameter();
        //pep.atlasFile = "Images/SpriteSheet.atlas";
        //load("Particles/ScanParticleEffect",ParticleEffect .class, pep);
        finishLoading();
    }
}
