package fisher;

import fisher.helpers.CoinsHelper;
import fisher.helpers.FishHelper;
import fisher.helpers.FisherGUI;
import fisher.helpers.TravelHelper;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.swing.*;
import java.awt.*;

@ScriptManifest(category = Category.FISHING, name = "Harpooner", author = "Fernando", version = 1.0)
public class FisherMain extends AbstractScript implements MessageListener {

    public final Area fishingPierArea = new Area(2925, 3180, 2924, 3175, 0);
    public final Area karamjaPierArea =  new Area(2949, 3147, 2959, 3146, 0);
    public final Area sarimPierArea =  new Area(3029, 3220, 3026, 3215, 0);
    public final Area boatKaramja =  new Area(2962, 3143, 2952, 3139, 1);
    public final Area boatSarim =  new Area(3032, 3223, 3036, 3213, 1);
    public final Area generalStore =  new Area(2947, 3212, 2949, 3218, 0);

    private Timer t = new Timer();

    public TravelHelper traveler;
    public FishHelper fisher;
    public CoinsHelper moneyMaker;


    public int fishCatched;
    public int swordfishCatched;
    public int levelsGained;
    public String status;

    public boolean isShouldStart() {
        return shouldStart;
    }

    public void setShouldStart(boolean shouldStart) {
        this.shouldStart = shouldStart;
    }

    private boolean shouldStart;
    private FisherGUI gui;

    public enum States {
        FISHING,
        INSIDE_BOAT_KARAMJA,
        OUTSIDE_BOAT_KARAMJA,
        INSIDE_BOAT_PORT_SARIM,
        OUTSIDE_BOAT_PORT_SARIM_BANK,
        OUTSIDE_BOAT_PORT_SARIM_SELL,
        OUTSIDE_BOAT_PORT_SARIM_PAY_FARE,
        IN_GENERAL_STORE_WITH_MONEY,
        IN_GENERAL_STORE_WITHOUT_MONEY,
    }

    private void initGUI(){
        gui = new FisherGUI(this);
        gui.setVisible(true);
    }

    @Override
    public void onStart(){
        log("Hi, thanks for testing this Fishing script.");
        initGUI();

        getSkillTracker().resetAll();
        getSkillTracker().start();
        fishCatched = 0;
        status = "Starting script...";
        shouldStart = false;


        traveler = new TravelHelper(this);
        fisher = new FishHelper(this);
        moneyMaker = new CoinsHelper(this);
    }

    @Override
    public int onLoop() {
        if(shouldStart){
            States state = getCurrentHarpoonState();
            processHarpoonState(state);
        }
        return 200;
    }

    @Override
    public void onExit() {
        log("Bye, thanks for testing this Karamja harpoon script.");
    }

    @Override
    public void onPaint(Graphics graphics) {

        Color color1 = new Color(130, 128, 255);
        Color color2 = new Color(0, 0, 0);

        BasicStroke stroke1 = new BasicStroke(1);

        Font font1 = new Font("TimesRoman", 0, 12);
        Font font2 = new Font("TimesRoman", 0, 11);

        Graphics2D g = (Graphics2D) graphics;
        g.setColor(color1);
        g.fillRoundRect(15, 15, 160, 120, 5, 5);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(15, 15, 160, 120, 5, 55);
        g.setFont(font1);
        g.drawString("Karamja Fisher by Fernando", 28, 30);
        g.setFont(font2);
        g.drawString("Time Running: " + t.formatTime(), 29, 50);
        g.drawString("Fish catched:" + fishCatched, 29, 65);
        g.drawString("Levels gained: " + levelsGained + " | Current level: " + getSkillTracker().getStartLevel(Skill.FISHING), 29, 80);
        g.drawString("Fishing XP/H: " + getSkillTracker().getGainedExperiencePerHour(Skill.FISHING), 29, 95);
        g.drawString("Status: " + status, 29, 110);

    }

    @Override
    public void onGameMessage(Message message) {
        if(message.getMessage() != null && (message.getMessage().toLowerCase().contains("you catch a"))){
            fishCatched++;
        }
        if(message.getMessage() != null && (message.getMessage().toLowerCase().contains("advanced your fishing level"))){
            levelsGained++;
        }
    }

    @Override
    public void onPlayerMessage(Message message) {

    }

    @Override
    public void onTradeMessage(Message message) {

    }

    @Override
    public void onPrivateInMessage(Message message) {

    }

    @Override
    public void onPrivateOutMessage(Message message) {

    }


    private States getCurrentHarpoonState(){
        if(!traveler.hasFullInventory() && traveler.isInKaramjaIsland() && !traveler.isInsideKaramjaBoat()){
            log("Trying to go fish...");
            status = "Trying to go fish...";
            return States.FISHING;
        }
        else if (!traveler.hasFullInventory() && traveler.isInKaramjaIsland() && traveler.isInsideKaramjaBoat()){
            log("Trying to leave boat...");
            status = "Trying to leave boat...";
            return States.INSIDE_BOAT_KARAMJA;
        }
        else if (traveler.hasFullInventory() && traveler.isInKaramjaIsland() && !traveler.isInsideKaramjaBoat()){
            log("Trying to pay fare...");
            status = "Trying to pay fare...";
            return States.OUTSIDE_BOAT_KARAMJA;
        }
        else if (traveler.hasFullInventory() && traveler.isInPortSarim() && traveler.isInsidePortSarimBoat()){
            log("Trying to leave boat...");
            status = "Trying to leave boat...";
            return States.INSIDE_BOAT_PORT_SARIM;
        }
        else if (traveler.hasFullInventory() && traveler.isInPortSarim() && !traveler.isInsidePortSarimBoat() && moneyMaker.haveCoinsForNextTravel()){
            log("Trying to bank fish...");
            status = "Trying to bank fish...";
            return States.OUTSIDE_BOAT_PORT_SARIM_BANK;
        }
        else if (traveler.hasFullInventory() && traveler.isInPortSarim() && !traveler.isInsidePortSarimBoat() && !moneyMaker.haveCoinsForNextTravel()){
            log("No Coins... Selling fish...");
            status = "Trying to sell fish...";
            return States.OUTSIDE_BOAT_PORT_SARIM_SELL;
        }
        else if (!traveler.hasFullInventory() && traveler.isInPortSarim() && !traveler.isInsidePortSarimBoat()){
            log("Trying to travel to Karamja...");
            status = "Trying to travel to Karamja...";
            return States.OUTSIDE_BOAT_PORT_SARIM_PAY_FARE;
        }
        else if (!traveler.hasFullInventory() && traveler.isInGeneralStore() && moneyMaker.haveCoinsForNextTravel() ){
            log("In general store with new money... Going to Karamja.");
            status = "New money... Going to Karamja.";
            return States.IN_GENERAL_STORE_WITH_MONEY;
        }
        else if (!traveler.hasFullInventory() && traveler.isInGeneralStore() && !moneyMaker.haveCoinsForNextTravel() ){
            log("In general store with new money... Going to Karamja.");
            status = "New money... Going to Karamja.";
            return States.IN_GENERAL_STORE_WITHOUT_MONEY;
        }
        return null;
    }

    public void processHarpoonState(States curentState){
        randomCameraMovement();
        switch(curentState) {
            case FISHING:
                fisher.activateFishing();
                break;
            case INSIDE_BOAT_KARAMJA:
            case INSIDE_BOAT_PORT_SARIM:
                traveler.crossPlank();
                break;
            case OUTSIDE_BOAT_KARAMJA:
                traveler.walkToKaramjaPier();
                traveler.payFareToPortSarim();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_BANK:
                traveler.depositLoot();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_PAY_FARE:
            case IN_GENERAL_STORE_WITH_MONEY:
                traveler.walkToPortSarimPier();
                traveler.payFareToKaramja();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_SELL:
                traveler.walkToGeneralStore();
                break;
            case IN_GENERAL_STORE_WITHOUT_MONEY:
                moneyMaker.sellFish();
                break;
            default:
                log("Program has bugged out. :(");
        }

    }

    public void randomCameraMovement() {
        if(Calculations.random(0, 9) > 4){
            getCamera().rotateTo(Calculations.random(2400), Calculations.random(getClient().getLowestPitch(), 384));
        }
    }


    private void testCode(){
    }


}