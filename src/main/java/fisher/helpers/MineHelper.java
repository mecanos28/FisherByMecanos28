package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;
import static org.dreambot.api.methods.MethodProvider.sleepWhile;

public class MineHelper extends Helper {

    GameObject currentNode;

    public static final int TIN_ORE_COLOR_ID = 53;
    public static final int COPPER_ORE_COLOR_ID = 4645;

//        CLAY_COLOR_ID(6705),
//        COPPER_COLOR_ID(4645),
//        TIN_COLOR_ID(53),
//        IRON_COLOR_ID(2576),
//        SILVER_COLOR_ID(74),
//        COAL_COLOR_ID(10508),
//        GOLD_COLOR_ID(8885),
//        MITHRIL_COLOR_ID(-22239),
//        ADAMANTITE_COLOR_ID(21662),
//        RUNITE_COLOR_ID(-31437);

    public MineHelper(BotMain m) {
        super(m);
        this.currentNode = null;
    }



    public void activateLumbridgeMiner() {
        if(inLumbrdigeMiningArea()){
            getMeSomeTinAndCopper();
        }else{
            m.status = "Walking to get some ore...";
            if(m.getWalking().walk(m.lumbridgeMineArea.getRandomTile())){
                sleepUntil(() -> inLumbrdigeMiningArea(),Calculations.random(3000, 5500));
            }
        }
    }


    private boolean inLumbrdigeMiningArea() {
        return m.lumbridgeMineArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.lumbridgeMineArea.getCenter()) < 8;
    }


    public void getMeSomeTinAndCopper(){
        m.randomCameraMovement();
        MethodProvider.log("Trying to get some ore.");
        m.status = "Searching for mining node...";

        if(m.getInventory().count(item -> item != null && item.getName().toLowerCase().contains("tin")) >= 14) {
            currentNode = getRockWithOre(COPPER_ORE_COLOR_ID);
            m.log(currentNode.getName());
            m.sleep(500, 1500);
            m.status = "Mining copper second..";
        } else{
            currentNode = getRockWithOre(TIN_ORE_COLOR_ID);
            m.sleep(500, 1500);
            m.log(currentNode.getName());
            m.status = "Mining tin first..";
        }
        if(currentNode != null && currentNode.interact("Mine")){
            m.sleep(500, 1500);
            sleepWhile(() -> m.getLocalPlayer().isAnimating(), Calculations.random(10000));
        }

    }

    private Filter<GameObject> oreFilter (int colorId) {
        return gameObject -> {
            short[] color = null;
            boolean accepted = false;
            if(gameObject != null ){
                color = gameObject.getModelColors();
                if(color != null && color.length > 1){
                    accepted = color[1] == colorId;
                }
            }
            return accepted;
        };
    }

    public GameObject getRockWithOre(int id){
        return m.getGameObjects().closest(obj -> {

            short[] colours = obj.getModelColors();

            if(colours != null){
                for(short c : colours){

                    if(c == id) return true;
                }
            }
            return false;
        });
    }


}
