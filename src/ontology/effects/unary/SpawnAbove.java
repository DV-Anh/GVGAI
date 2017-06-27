package ontology.effects.unary;

import core.content.InteractionContent;
import core.game.Game;
import core.vgdl.VGDLRegistry;
import core.vgdl.VGDLSprite;
import ontology.effects.Effect;
import tools.Vector2d;

import java.util.ArrayList;

import static ontology.Types.UP;

public class SpawnAbove extends Effect {

    public String stype;
    public int itype;
    public boolean stepBack;

    public SpawnAbove(InteractionContent cnt)
    {
        stepBack = false;
        this.parseParameters(cnt);
        itype = VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype);
    }

    @Override
    public void execute(VGDLSprite sprite1, VGDLSprite sprite2, Game game)
    {
        if(game.getRandomGenerator().nextDouble() >= prob) return;
        Vector2d currentPos;
        if (stepBack)
            currentPos = sprite2.getLastPosition();
        else
            currentPos = sprite2.getPosition();
        Vector2d dir = new Vector2d(0,-1).mul(game.getBlockSize());
        if (currentPos != null) {
            Vector2d nextPos = new Vector2d(currentPos).add(dir);
            game.addSprite(itype, nextPos);
        }
    }
    
    @Override
    public ArrayList<String> getEffectSprites(){
    	ArrayList<String> result = new ArrayList<String>();
    	if(stype!=null) result.add(stype);
    	
    	return result;
    }
}