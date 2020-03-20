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

        //testCode();

        if(!getInventory().isFull()){
            randomCameraMovement();
            if(pierArea.contains(getLocalPlayer())){
                dropTunas();
                getMeSomeFish(); //change "Tree" to the name of your tree.
            }else{
                if(getWalking().walk(pierArea.getRandomTile())){
                    sleep(Calculations.random(3000, 5500));
                }
            }
        }


        if(getInventory().isFull()){ //it is time to bank
            if(boatIslandArea.contains(getLocalPlayer()) || boatMainlandArea.contains(getLocalPlayer())){
                goToMainland();
                goToKaramja();
            }
            else{
                if(getWalking().walk(boatIslandArea.getRandomTile())){
                    sleep(Calculations.random(3000, 6000));
                }
            }



        }

        return 600;
    }

    private void dropTunas() {
        log("dropping tunas...");
        getInventory().dropAll((item) -> item != null && ("Raw tuna").equals(item.getName()) );
    }

    private void goToMainland() {
        if(getLocalPlayer().distance(boatMainlandArea.getRandomTile()) > Calculations.random(3, 6)) {
            log("going to mainland...");
            NPC boatGuy = getBoatGuy("Pay-Fare");
            boatGuy.interact("Pay-Fare");
            log("payed fare...");
            sleep(Calculations.random(7000, 9000));
            crossPlank();
            sleep(Calculations.random(1500, 4000));
        }
        log("depositing loot...");
        depositLoot();
    }

    private NPC getBoatGuy(String actionName) {
        NPC boatGuy = getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains(actionName));
        while (boatGuy == null) {
            log("trying to get boat guy...");
            sleep(Calculations.random(1000, 4000));
            boatGuy = getNpcs().closest(
                    n -> n != null && Arrays.toString(n.getActions()).contains(actionName));
        }
        log("boat guy: "+ boatGuy.getName());
        return boatGuy;
    }

    private void crossPlank() {
        GameObject plank = getGameObjects().closest(n -> n != null && "Gangplank".equals(n.getName()));
        while (plank == null) {
            sleep(Calculations.random(1000, 4000));
            plank = getGameObjects().closest(n -> n != null && "Gangplank".equals(n.getName()));
        }
        log("found plank, crossing...");
        plank.interact("Cross");
    }

    private void goToKaramja() {
        while(! boatMainlandArea.contains(getLocalPlayer())){
            if(getWalking().walk(boatMainlandArea.getRandomTile())){
                randomCameraMovement();
                sleep(Calculations.random(3000, 6000));
            }
        }
        NPC boatGuy = getBoatGuy("Pay-fare");
        boatGuy.interact("Pay-fare");
        sleep(Calculations.random(5000, 7000));
        crossPlank();
        sleep(Calculations.random(1500, 4000));

    }

    @Override
    public void onExit() {
        log("Bye");
    }

    @Override
    public void onPaint(Graphics graphics) {

    }

    private void getMeSomeFish(){
        randomCameraMovement();
        log("Trying to get some fish.");
        NPC fishingSpot = getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Harpoon") && Arrays.toString(n.getActions()).contains("Cage"));


        if(fishingSpot != null && fishingSpot.interact("Harpoon")){
            log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            sleepUntil(() -> getLocalPlayer().isStandingStill(), Calculations.random(60000, 90000));
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
        crossPlank();
    }

    private void depositLoot(){
        randomCameraMovement();

        while(!getDepositBox().open()){
            randomCameraMovement();
            sleep(Calculations.random(1000, 2000));
        }
        if (getDepositBox().isOpen()) {
            getDepositBox().depositAll("Raw tuna");
            getDepositBox().depositAll("Raw swordfish");
            getDepositBox().depositAll(item -> item != null && !item.getName().equals("Coins") && !item.getName().equals("Harpoon"));
        }
        getDepositBox().close();

        sleep(Calculations.random(1000, 4000));
    }
}