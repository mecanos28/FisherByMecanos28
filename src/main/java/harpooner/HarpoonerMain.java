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

    Area fishingPierArea = new Area(2925, 3180, 2924, 3175, 0);
    Area karamjaPierArea =  new Area(2949, 3147, 2959, 3146, 0);
    Area sarimPierArea =  new Area(3029, 3220, 3026, 3215, 0);
    Area boatKaramja =  new Area(2962, 3143, 2952, 3139, 1);
    Area boatSarim =  new Area(3032, 3223, 3036, 3213, 1);

    public enum States {
        FISHING,
        INSIDE_BOAT_KARAMJA,
        OUTSIDE_BOAT_KARAMJA,
        INSIDE_BOAT_PORT_SARIM,
        OUTSIDE_BOAT_PORT_SARIM_BANK,
        OUTSIDE_BOAT_PORT_SARIM_PAY_FARE
    }

    @Override
    public void onStart(){
        log("Hi");
    }

    @Override
    public int onLoop() {
        States state = getCurrentHarpoonState();
        processHarpoonState(state);
        return 600;
    }

    @Override
    public void onExit() {
        log("Bye");
    }

    @Override
    public void onPaint(Graphics graphics) {

    }


    private States getCurrentHarpoonState(){
        if(!hasFullInventory() && isInKaramjaIsland() && !isInsideKaramjaBoat()){
            log("Trying to go fish...");
            return States.FISHING;
        }
        else if (!hasFullInventory() && isInKaramjaIsland() && isInsideKaramjaBoat()){
            log("Trying to leave boat...");
            return States.INSIDE_BOAT_KARAMJA;
        }
        else if (hasFullInventory() && isInKaramjaIsland() && !isInsideKaramjaBoat()){
            log("Trying to pay fare...");
            return States.OUTSIDE_BOAT_KARAMJA;
        }
        else if (hasFullInventory() && isInPortSarim() && isInsidePortSarimBoat()){
            log("Trying to leave boat...");
            return States.INSIDE_BOAT_PORT_SARIM;
        }
        else if (hasFullInventory() && isInPortSarim() && !isInsidePortSarimBoat()){
            log("Trying to bank fish...");
            return States.OUTSIDE_BOAT_PORT_SARIM_BANK;
        }
        else if (!hasFullInventory() && isInPortSarim() && !isInsidePortSarimBoat()){
            log("Trying to travel to Karamja..");
            return States.OUTSIDE_BOAT_PORT_SARIM_PAY_FARE;
        }
        return null;
    }

    public void processHarpoonState(States curentState){
        randomCameraMovement();
        switch(curentState) {
            case FISHING:
                activateFishing();
                break;
            case INSIDE_BOAT_KARAMJA:
            case INSIDE_BOAT_PORT_SARIM:
                crossPlank();
                break;
            case OUTSIDE_BOAT_KARAMJA:
                walkToKaramjaPier();
                payFareToPortSarim();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_BANK:
                depositLoot();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_PAY_FARE:
                walkToPortSarimPier();
                payFareToKaramja();
                break;
            default:
                // code block
        }

    }

    private void walkToKaramjaPier() {
        while (!karamjaPierArea.contains(getLocalPlayer())) {
            getWalking().walk(karamjaPierArea.getRandomTile());
            sleepUntil(() -> !getLocalPlayer().isMoving(), 5200);
        }
    }

    private void walkToPortSarimPier() {
        while (!sarimPierArea.contains(getLocalPlayer())) {
            getWalking().walk(sarimPierArea.getRandomTile());
            sleepUntil(() -> !getLocalPlayer().isMoving(), 5200);
        }
    }

    private void payFareToPortSarim() {
        log("Going to mainland...");
        NPC boatGuy = getBoatGuy("Pay-Fare");
        boatGuy.interact("Pay-Fare");
        log("Payed fare...");
        sleep(Calculations.random(7000, 9000));
    }

    private void payFareToKaramja() {
        log("Going to island...");
        NPC boatGuy = getBoatGuy("Pay-fare");
        boatGuy.interact("Pay-fare");
        log("Payed fare...");
        sleep(Calculations.random(7000, 9000));
    }

    private boolean isInsidePortSarimBoat() {
        return boatSarim.contains(getLocalPlayer());
    }

    private boolean isInPortSarim() {
        return getLocalPlayer().distance(sarimPierArea.getCenter()) < 60;
    }

    private boolean hasFullInventory() {
        return getInventory().isFull();
    }

    private boolean isInKaramjaIsland() {
        return getLocalPlayer().distance(karamjaPierArea.getCenter()) < 60;
    }

    private boolean isInsideKaramjaBoat() {
        return boatKaramja.contains(getLocalPlayer());
    }



    private void activateFishing() {
        if(fishingPierArea.contains(getLocalPlayer())){
            dropTunas();
            getMeSomeFish();
        }else{
            if(getWalking().walk(fishingPierArea.getRandomTile())){
                sleep(Calculations.random(3000, 5500));
            }
        }
    }

    private void dropTunas() {
        log("dropping tunas...");
        getInventory().dropAll((item) -> item != null && ("Raw tuna").equals(item.getName()) );
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
            sleep(Calculations.random(1000, 2000));
            plank = getGameObjects().closest(n -> n != null && "Gangplank".equals(n.getName()));
        }
        log("Found plank, crossing...");
        plank.interact("Cross");
        sleep(Calculations.random(1500, 4000));
    }

    private void depositLoot(){
        log("depositing loot...");
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

    private void getMeSomeFish(){
        randomCameraMovement();
        log("Trying to get some fish.");
        NPC fishingSpot = getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains("Harpoon") && Arrays.toString(n.getActions()).contains("Cage"));


        if(fishingSpot != null && fishingSpot.interact("Harpoon")){
            log("Found fishing spot: " + fishingSpot.getName() + " Coordinates: "+ fishingSpot.getGridX() + " - " + fishingSpot.getGridY());
            sleepUntil(() -> getLocalPlayer().getAnimation() == -1, Calculations.random(10000, 25000));
        }
    }

    private void randomCameraMovement() {
        if(Calculations.random(0, 9) > 4){
            getCamera().rotateTo(Calculations.random(2400), Calculations.random(getClient().getLowestPitch(), 384));
        }
    }

    private void goToKaramja() {
        while(! sarimPierArea.contains(getLocalPlayer())){
            if(getWalking().walk(sarimPierArea.getRandomTile())){
                randomCameraMovement();
                sleep(Calculations.random(3000, 6000));
            }
        }
        NPC boatGuy = getBoatGuy("Pay-fare");
        boatGuy.interact("Pay-fare");
        sleep(Calculations.random(5000, 7000));
        crossPlank();
        sleep(Calculations.random(3000, 4000));

    }
    private void goToMainland() {
        if(getLocalPlayer().distance(sarimPierArea.getRandomTile()) > Calculations.random(3, 6)) {
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





    private void bank(){
        NPC banker = getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));
        if(banker != null && banker.interact("Bank")){
            if(sleepUntil(() -> getBank().isOpen(), 9000)){
                if(getBank().depositAllExcept(item -> item != null && item.getName().contains("axe"))){
                    if(sleepUntil(() -> !hasFullInventory(), 8000)){
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


}