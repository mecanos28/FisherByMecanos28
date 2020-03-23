package fisher.helpers;

import fisher.FisherMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;

public class FishHelper extends Helper {

    public FishHelper(FisherMain m) {
        super(m);
    }

    public void activateHarpoonFisher() {
        if(inKaramjaFishingArea()){
            dropTunas();
            getMeSomeSwordfish();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.fishingPierArea.getRandomTile())){
                m.sleep(Calculations.random(3000, 5500));
            }
        }
    }

    public void activateLumbridgeShrimpFisher() {
        if(inLumbridgeShrimpingArea()){
            getMeSomeShrimp();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.lumbridgeShrimpArea.getRandomTile())){
                m.sleep(Calculations.random(3000, 5500));
            }
        }
    }

    public void activateDraynorShrimpFisher() {
        if(inRimmingtonShrimpingArea()){
            getMeSomeShrimp();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.rimmingtonShrimpArea.getRandomTile())){
                m.sleep(Calculations.random(3000, 5500));
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
        return m.fishingPierArea.contains(m.getLocalPlayer());
    }

    public void dropTunas() {
        if(m.getInventory().count("Raw tuna") > 4){
            m.log("dropping tunas...");
            m.status = "Dropping Tunas! :)";
            m.getInventory().dropAll((item) -> item != null && ("Raw tuna").equals(item.getName()) );
        }

    }

    public void getMeSomeSwordfish(){
        m.randomCameraMovement();
        m.log("Trying to get some fish.");
        m.status = "Searching for harpooning spot...";
        NPC fishingSpot = m.getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Harpoon") && Arrays.toString(n.getActions()).contains("Cage"));


        if(fishingSpot != null && fishingSpot.interact("Harpoon")){
            m.log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            m.status = "Fishing! :)";
            m.sleepUntil(() -> m.getLocalPlayer().getAnimation() == -1, Calculations.random(20000, 25000));
        }
    }

    public void getMeSomeShrimp(){
        m.randomCameraMovement();
        m.log("Trying to get some Shrimp.");
        m.status = "Searching for shrimping spot...";
        NPC fishingSpot = m.getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Net") || Arrays.toString(n.getActions()).contains("Small Net"));

        if(fishingSpot != null && fishingSpot.interact(getCorrectShrimperAction())){
            m.log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            m.status = "Fishing! :)";
            m.sleep(1000);
            m.moveCursorOffScreen();
            if (m.getLocalPlayer().getAnimation() == 621) {
                m.sleepUntil(() -> m.getLocalPlayer().getAnimation() == -1, 60000);
            }
        }
    }

    private String getCorrectShrimperAction() {
        return m.mode.equals("Draynor Shrimp") ? "Small Net" : "Net";
    }
}
