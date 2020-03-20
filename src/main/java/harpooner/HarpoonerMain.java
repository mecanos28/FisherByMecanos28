package harpooner;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by Computor on 7/8/2015.
 */
@ScriptManifest(category = Category.FISHING, name = "Harpooner", author = "Fernando", version = 1.0)
public class HarpoonerMain extends AbstractScript {

    Area pierArea = new Area(2925, 3180, 2924, 3175, 0);
    Area boatIslandArea =  new Area(2953, 3147, 2955, 3147, 0);
    Area boatMainlandArea =  new Area(3029, 3220, 3026, 3215, 0);

    @Override
    public void onStart(){
        log("Hi");
    }

    @Override
    public int onLoop() {
        /**
         * Chopping trees: Time to chop some trees, our inventory isn't full. We want to fill it up.
         */
        //testCode();

        if(!getInventory().isFull()){
            if(pierArea.contains(getLocalPlayer())){
                getMeSomeFish("Fishing spot"); //change "Tree" to the name of your tree.
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
            if(boatIslandArea.contains(getLocalPlayer())){
                goToMainland();
                depositLoot();
                goToKaramja();
            }else{
                if(getWalking().walk(boatIslandArea.getRandomTile())){
                    sleep(Calculations.random(3000, 6000));
                }
            }



        }

        return 600;
    }

    private void goToMainland() {
        NPC boatGuy = getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Pay-fare"));
        boatGuy.interact("Pay-Fare");
        sleep(Calculations.random(3000, 6000));
        GameObject plank = getGameObjects().closest(n -> n != null && n.getID() == 2084);
        plank.interact();
    }

    private void goToKaramja() {
        while(! boatMainlandArea.contains(getLocalPlayer())){
            if(getWalking().walk(boatMainlandArea.getRandomTile())){
                randomCameraMovement();
                sleep(Calculations.random(3000, 6000));
            }
        }
        NPC boatGuy = getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Pay-fare"));
        boatGuy.interact("Pay-Fare");
        sleep(Calculations.random(5000, 7000));
        GameObject plank = getGameObjects().closest(n -> n != null && n.getID() == 2082);
        plank.interact();

    }

    @Override
    public void onExit() {
        log("Bye");
    }

    @Override
    public void onPaint(Graphics graphics) {

    }

    private void getMeSomeFish(String nameOfTile){
        log("Trying to get some fish.");
        NPC fishingSpot = getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Harpoon") && Arrays.toString(n.getActions()).contains("Cage"));


        if(fishingSpot != null && fishingSpot.interact("Harpoon")){
            log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            sleepUntil(() -> getLocalPlayer().isStandingStill(), Calculations.random(14000, 31000));
            randomCameraMovement();
        }
    }

    private void randomCameraMovement() {
        if(Calculations.random(0, 9) > 4){
            getCamera().rotateTo(Calculations.random(2400), Calculations.random(getClient().getLowestPitch(), 384));
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

    private void testCode(){
        goToKaramja();
    }

    private void depositLoot(){
        randomCameraMovement();
        getDepositBox().open();
        if(getDepositBox().isOpen()){
            getDepositBox().depositAll("Raw tuna");
            getDepositBox().depositAll("Raw swordfish");
        }
        getDepositBox().close();
    }
}