package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;

import static org.dreambot.api.methods.MethodProvider.log;
import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class TravelHelper extends Helper {

    public TravelHelper(BotMain m) {
        super(m);
    }


    public void walkToKaramjaPier() {
        while (!m.karamjaPierArea.contains(m.getLocalPlayer())) {
            m.status = "Walking to Karamja Pier...";
            m.getWalking().walk(m.karamjaPierArea.getRandomTile());
            sleepUntil(() -> !m.getLocalPlayer().isMoving(), 5200);
        }
    }

    public void walkToPortSarimPier() {
        while (!m.sarimPierArea.contains(m.getLocalPlayer())) {
            m.status = "Walking to Port Sarim Pier...";
            m.getWalking().walk(m.sarimPierArea.getRandomTile());
            sleepUntil(() -> !m.getLocalPlayer().isMoving(), 5200);
        }
    }

    public void walkToGeneralStore() {
        while (!m.generalStoreRimmingtonArea.contains(m.getLocalPlayer())) {
            m.status = "Walking to General Store...";
            m.getWalking().walk(m.generalStoreRimmingtonArea.getRandomTile());
            sleepUntil(() -> !m.getLocalPlayer().isMoving(), 5200);
        }
    }

    public void walkToArea(Area area) {
        while (!area.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(area.getCenter()) < 4) {
            m.status = "Walking...";
            m.getWalking().walk(area.getRandomTile());
            sleepUntil(() -> !m.getLocalPlayer().isMoving(), 5200);
        }
    }

    public void payFareToPortSarim() {
        log("Going to mainland...");
        m.status = "Paying fare to Port Sarim...";
        NPC boatGuy = getGuyByActionName("Pay-Fare");
        m.findWithCamera(boatGuy);
        boatGuy.interact("Pay-Fare");
        log("Payed fare...");
        m.sleep(Calculations.random(7000, 9000));
    }

    public void payFareToKaramja() {
        log("Going to island...");
        m.status = "Paying fare to Karamja...";
        NPC boatGuy = getGuyByActionName("Pay-fare");
        m.findWithCamera(boatGuy);
        boatGuy.interact("Pay-fare");
        log("Payed fare...");
        m.sleep(Calculations.random(7000, 9000));
    }

    public NPC getGuyByActionName(String actionName) {
        m.status = "Searching for guy...";
        NPC guy = m.getNpcs().closest(
                n -> n != null && Arrays.toString(n.getActions()).contains(actionName));
        while (guy == null) {
            log("trying to get guy...");
            m.sleep(Calculations.random(1000, 4000));
            guy = m.getNpcs().closest(
                    n -> n != null && Arrays.toString(n.getActions()).contains(actionName));
        }
        m.status = "Found : " + guy.getName() + "! :D";
        log("Found guy: "+ guy.getName());
        return guy;
    }

    public boolean isInsidePortSarimBoat() {
        return m.boatSarimArea.contains(m.getLocalPlayer());
    }

    public boolean isInPortSarim() { return m.getLocalPlayer().distance(m.sarimPierArea.getCenter()) < 60; }

    public boolean hasFullInventory() {
        return m.getInventory().isFull();
    }

    public boolean hasItemInInventory(String item) {
        return m.getInventory().contains(item);
    }

    public boolean isInKaramjaIsland() {
        return m.getLocalPlayer().distance(m.karamjaPierArea.getCenter()) < 60;
    }

    public boolean isInsideKaramjaBoat() {
        return m.boatKaramjaArea.contains(m.getLocalPlayer());
    }

    public boolean isInGeneralStore() { return m.generalStoreRimmingtonArea.contains(m.getLocalPlayer()); }

    public boolean isInGroundLevel() { return m.getLocalPlayer().getZ() == 0; }
    public boolean isInSecondLevel() { return m.getLocalPlayer().getZ() == 1; }
    public boolean isInThirdLevel() { return m.getLocalPlayer().getZ() == 2; }

    public void interactWithStaircase(String action){
        m.status = "Interacting with staircase..." + " " + action;
        GameObject stairs = m.getGameObjects().closest(n -> n != null && "Staircase".equals(n.getName()));
        while (stairs == null) {
            m.sleep(Calculations.random(1000, 2000));
            stairs = m.getGameObjects().closest(n -> n != null && "Staircase".equals(n.getName()));
        }
        m.findWithCamera(stairs);
        stairs.interact(action);
        m.sleep(Calculations.random(1500, 3000));

    }

    public void crossPlank() {
        m.status = "Crossing Gangplank!";
        GameObject plank = m.getGameObjects().closest(n -> n != null && "Gangplank".equals(n.getName()));
        while (plank == null) {
            m.sleep(Calculations.random(1000, 2000));
            plank = m.getGameObjects().closest(n -> n != null && "Gangplank".equals(n.getName()));
        }
        log("Found plank, crossing...");
        m.findWithCamera(plank);
        plank.interact("Cross");
        m.sleep(Calculations.random(1500, 4000));
    }

    public void depositKaramjaLoot(){
        log("depositing loot...");
        m.randomCameraMovement();

        while(!m.getDepositBox().open()){
            m.status = "Walking to deposit box...";
            m.randomCameraMovement();
            m.sleep(Calculations.random(1000, 2000));
        }
        if (m.getDepositBox().isOpen()) {
            m.status = "Depositing fish...";
            m.getDepositBox().depositAll("Raw tuna");
            m.getDepositBox().depositAll("Raw swordfish");
            m.getDepositBox().depositAll(item -> item != null && !item.getName().equals("Coins") && !item.getName().equals("Harpoon"));
        }
        m.getDepositBox().close();

        m.sleep(Calculations.random(1000, 4000));
    }

    public void depositAllInventory(){
        log("Banking.");
        m.randomCameraMovement();

        while(!m.getBank().open()){
            m.status = "Opening Bank";
            m.randomCameraMovement();
            m.sleep(Calculations.random(1000, 2000));
        }
        if (m.getBank().isOpen()) {
            m.status = "Depositing all inventory...";
            m.getBank().depositAllItems();
        }
        if(Calculations.random(1, 10) > 4) m.getBank().close();
        m.sleep(Calculations.random(1000, 4000));
    }

    public void getAllFromBank(String item){
        log("Banking.");
        m.randomCameraMovement();

        while(!m.getBank().open()){
            m.status = "Opening Bank";
            m.randomCameraMovement();
            m.sleep(Calculations.random(1000, 2000));
        }
        if (m.getBank().isOpen()) {
            m.status = "Withdrawing all " + item;
            m.getBank().withdrawAll(item);
        }
        if(Calculations.random(1, 10) > 4) m.getBank().close();
        m.sleep(Calculations.random(1000, 4000));
    }

    public void bankShrimp(){
        log("Banking.");
        m.randomCameraMovement();

        while(!m.getBank().open()){
            m.status = "Opening Bank";
            m.randomCameraMovement();
            m.sleep(Calculations.random(1000, 2000));
        }
        if (m.getBank().isOpen()) {
            m.status = "Depositing fish...";
            m.getBank().depositAll("Raw shrimp");
            m.getBank().depositAll("Raw anchovies");
            m.getBank().depositAll(item -> item != null && !item.getName().equals("Small fishing net"));
        }
        if(Calculations.random(1, 10) > 4) m.getBank().close();
        m.sleep(Calculations.random(1000, 4000));
    }


}
