package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;

public class FishHelper extends Helper {

    public FishHelper(BotMain m) {
        super(m);
    }

    public void activateHarpoonFisher() {
        if(inKaramjaFishingArea()){
            dropTunas();
            getMeSomeSwordfish();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.fishingPierArea.getRandomTile())){
                MethodProvider.sleepUntil(() -> inKaramjaFishingArea(),Calculations.random(3000, 5500));
            }
        }
    }

    public void activateLumbridgeShrimpFisher() {
        if(inLumbridgeShrimpingArea()){
            getMeSomeShrimp();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.lumbridgeShrimpArea.getRandomTile())){
                MethodProvider.sleepUntil(() -> inLumbridgeShrimpingArea(),Calculations.random(3000, 5500));
            }
        }
    }

    public void activateEdgevilleFlyFisher() {
        if(inEdgevilleFishinaREA()){
            getMeSomeTrout();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.edgeVilleFisherArea.getRandomTile())){
                MethodProvider.sleepUntil(() -> inEdgevilleFishinaREA(),Calculations.random(3000, 5500));
            }
        }
    }

    public void activateDraynorShrimpFisher() {
        if(inRimmingtonShrimpingArea()){
            getMeSomeShrimp();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.rimmingtonShrimpArea.getRandomTile())){
                MethodProvider.sleepUntil(() -> inRimmingtonShrimpingArea(),Calculations.random(3000, 5500));
            }
        }
    }

    private boolean inLumbridgeShrimpingArea() {
        return m.lumbridgeShrimpArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.lumbridgeShrimpArea.getCenter()) < 8;
    }

    private boolean inRimmingtonShrimpingArea() {
        return m.rimmingtonShrimpArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.rimmingtonShrimpArea.getCenter()) < 4;
    }

    private boolean inKaramjaFishingArea() {
        return m.fishingPierArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.fishingPierArea.getCenter()) < 4;
    }

    private boolean inEdgevilleFishinaREA() {
        return m.edgeVilleFisherArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.edgeVilleFisherArea.getCenter()) < 14;
    }

    public void dropTunas() {
        if(m.getInventory().count("Raw tuna") > 4){
            MethodProvider.log("dropping tunas...");
            m.status = "Dropping Tunas! :)";
            m.getInventory().dropAll((item) -> item != null && ("Raw tuna").equals(item.getName()) );
        }

    }

    public void getMeSomeSwordfish(){
        m.randomCameraMovement();
        MethodProvider.log("Trying to get some fish.");
        m.status = "Searching for harpooning spot...";
        NPC fishingSpot = m.getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Harpoon") && Arrays.toString(n.getActions()).contains("Cage"));

        m.findWithCamera(fishingSpot);

        if(fishingSpot != null && fishingSpot.interact("Harpoon")){
            MethodProvider.log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            m.status = "Fishing! :)";
            MethodProvider.sleepUntil(() -> m.getLocalPlayer().getAnimation() == -1, Calculations.random(20000, 25000));
        }
    }

    public void getMeSomeShrimp(){
        m.randomCameraMovement();
        MethodProvider.log("Trying to get some Shrimp.");
        m.status = "Searching for shrimping spot...";
        NPC fishingSpot = m.getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Net") || Arrays.toString(n.getActions()).contains("Small Net"));

        m.findWithCamera(fishingSpot);

        if(fishingSpot != null && fishingSpot.interact(getCorrectShrimperAction())){
            MethodProvider.log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            m.status = "Fishing! :)";
            MethodProvider.sleep(1000);
            m.moveCursor();
            if (m.getLocalPlayer().getAnimation() == 621) {
                MethodProvider.sleepUntil(() -> m.getLocalPlayer().getAnimation() == -1, 60000);
                MethodProvider.sleep(Calculations.random(1300,4900));
            }
        }
    }

    public void getMeSomeTrout(){
        m.randomCameraMovement();
        MethodProvider.log("Trying to get some trout.");
        m.status = "Searching for trout spot...";
        NPC fishingSpot = m.getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Lure") || Arrays.toString(n.getActions()).contains("Bait"));

        m.findWithCamera(fishingSpot);

        if(fishingSpot != null && fishingSpot.interact(("Lure"))){
            MethodProvider.log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            m.status = "Fishing! :)";
            MethodProvider.sleep(1000);
            m.moveCursor();
            if (m.getLocalPlayer().getAnimation() == 622) {
                MethodProvider.sleepUntil(() -> m.getLocalPlayer().getAnimation() == -1, 60000);
                MethodProvider.sleep(Calculations.random(1300,4900));
            }
        }
    }

    private String getCorrectShrimperAction() {
        return m.mode.equals("Draynor Shrimp") ? "Small Net" : "Net";
    }


}
