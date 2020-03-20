package harpooner;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.awt.*;

/**
 * Created by Computor on 7/8/2015.
 */
@ScriptManifest(category = Category.FISHING, name = "Harpooner", author = "Fernando", version = 1.0)
public class HarpoonerMain extends AbstractScript {

    Area pierArea = new Area(2925, 3180, 2924, 3175, 0);

    @Override
    public void onStart(){
        log("Hi");
    }

    @Override
    public int onLoop() {
        /**
         * Chopping trees: Time to chop some trees, our inventory isn't full. We want to fill it up.
         */
        if(!getInventory().isFull()){
            if(pierArea.contains(getLocalPlayer())){
                chopTree("Tree"); //change "Tree" to the name of your tree.
            }else{
                if(getWalking().walk(pierArea.getRandomTile())){
                    sleep(Calculations.random(3000, 5500));
                }
            }
        }

        /**
         * Banking: Time to bank our logs. Our inventory is full, we want to empty it.
         */
        if(getInventory().isFull()){ //it is time to bank
            if(bankArea.contains(getLocalPlayer())){
                bank();
            }else{
                if(getWalking().walk(bankArea.getRandomTile())){
                    sleep(Calculations.random(3000, 6000));
                }
            }
        }

        return 600;
    }

    @Override
    public void onExit() {
        log("Bye");
    }

    @Override
    public void onPaint(Graphics graphics) {

    }

    private void getMeSomeFish(String nameOfTile){
        GameObject fishingSpot = getGameObjects().closest(gameObject -> gameObject != null && gameObject.getName().equals(nameOfTile));
        if(fishingSpot != null && fishingSpot.interact("Harpoon")){
            int countFish = getInventory().count("Raw Swordfish") + getInventory().count("Raw tuna");
            sleepUntil(() -> getInventory().count("Logs") > countLog, 12000);
        }
    }

    private void bank(){
        NPC banker = getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));
        if(banker != null && banker.interact("Bank")){
            if(sleepUntil(() -> getBank().isOpen(), 9000)){
                if(getBank().depositAllExcept(item -> item != null && item.getName().contains("axe"))){
                    if(sleepUntil(() -> !getInventory().isFull(), 8000)){
                        if(getBank().close()){
                            sleepUntil(() -> !getBank().isOpen(), 8000);
                        }
                    }
                }
            }
        }
    }
}