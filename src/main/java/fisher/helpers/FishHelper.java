package fisher.helpers;

import fisher.FisherMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;

public class FishHelper extends Helper {

    public FishHelper(FisherMain m) {
        super(m);
    }

    public void activateFishing() {
        if(m.fishingPierArea.contains(m.getLocalPlayer())){
            dropTunas();
            getMeSomeFish();
        }else{
            m.status = "Walking to get some fish...";
            if(m.getWalking().walk(m.fishingPierArea.getRandomTile())){
                m.sleep(Calculations.random(3000, 5500));
            }
        }
    }

    public void dropTunas() {
        if(m.getInventory().count("Raw tuna") > 4){
            m.log("dropping tunas...");
            m.status = "Dropping Tunas! :)";
            m.getInventory().dropAll((item) -> item != null && ("Raw tuna").equals(item.getName()) );
        }

    }

    public void getMeSomeFish(){
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
}
