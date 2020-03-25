package fisher;

import fisher.helpers.*;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.MessageListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;

import java.awt.*;


/*
Author: mecanos28
Run Gradle Scripts updateDreambot, buildScript and runDreambot
 */
@ScriptManifest(category = Category.FISHING, name = "Harpooner", author = "Mecanos28", version = 1.0)
public class BotMain extends AbstractScript implements MessageListener {

    public final Area fishingPierArea = new Area(2925, 3180, 2924, 3175, 0);
    public final Area karamjaPierArea =  new Area(2949, 3147, 2959, 3146, 0);
    public final Area sarimPierArea =  new Area(3029, 3220, 3026, 3215, 0);
    public final Area boatKaramjaArea =  new Area(2962, 3143, 2952, 3139, 1);
    public final Area boatSarimArea =  new Area(3032, 3223, 3036, 3213, 1);
    public final Area generalStoreRimmingtonArea =  new Area(2947, 3212, 2949, 3218, 0);

    public final Area lumbridgeShrimpArea =  new Area(3243, 3154, 3240, 3150, 0);
    public final Area lumbridgeFirstFloorStairsArea =  new Area(3206, 3208, 3206, 3209, 0);
    public final Area lumbridgeThirdFloorStairsArea =  new Area(3205, 3209, 3206, 3209, 2);

    public final Area rimmingtonShrimpArea =  new Area(3087, 3227, 3087, 3230, 0);

    public final Area lumbridgeCookingArea =  new Area(3207, 3212, 3211, 3216, 0);



    private Timer t = new Timer();

    public TravelHelper traveler;
    public FishHelper fisher;
    public CoinsHelper moneyMaker;
    public CookHelper cookHelper;

    private KaramjaHarpooner karamjaHarpooner;
    private LumbridgeShrimper lumbridgeShrimper;
    private DraynorShrimper draynorShrimper;
    private LumbridgeCooker lumbridgeCooker;


    public int fishCatched;
    public int levelsGained;
    public String status;
    public String mode;

    public boolean isShouldStart() {
        return shouldStart;
    }

    public void setShouldStart(boolean shouldStart) {
        this.shouldStart = shouldStart;
    }

    private boolean shouldStart;
    private FisherGUI gui;



    private void initGUI(){
        gui = new FisherGUI(this);
        gui.setVisible(true);
    }

    private void processMode(String mode){
        processDialogue();
        antiban();
        switch (mode){
            case ("Lumbridge Shrimp"):
                LumbridgeShrimper.ShrimperStates shrimpingState = lumbridgeShrimper.getCurrentShriperState();
                lumbridgeShrimper.processShrimpState(shrimpingState);
                break;
            case ("Karamja Harpoon"):
                KaramjaHarpooner.HarpooningStates harpooningState = karamjaHarpooner.getCurrentHarpoonState();
                karamjaHarpooner.processHarpoonState(harpooningState);
                break;
            case ("Draynor Shrimp"):
                DraynorShrimper.RimmingtonShrimperStates draynorShrimperState = draynorShrimper.getCurrentShriperState();
                draynorShrimper.processShrimpState(draynorShrimperState);
                break;
            case("Lumbridge Cooker"):
                LumbridgeCooker.CookerStates lumbridgeCookerState = lumbridgeCooker.getCurrentLumbridgeCookerState();
                lumbridgeCooker.processCurrentLumbridgeCookerState(lumbridgeCookerState);

            default:
        }
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
        cookHelper =  new CookHelper(this, "Raw shrimp");

        karamjaHarpooner = new KaramjaHarpooner(this);
        lumbridgeShrimper = new LumbridgeShrimper(this);
        draynorShrimper = new DraynorShrimper(this);
        lumbridgeCooker = new LumbridgeCooker(this);
    }

    @Override
    public int onLoop() {
        mode = gui.getCurrentMode();
        if(shouldStart){
            log("Executing new loop :D");
            processMode(mode);
        }
        return 200;
    }

    @Override
    public void onExit() {
        log("Bye, thanks for testing this Karamja harpoon script.");
    }

    @Override
    public void onPaint(Graphics graphics) {

        Color color1 = new Color(255, 103, 244, 41);
        Color color2 = new Color(0, 0, 0);

        BasicStroke stroke1 = new BasicStroke(1);

        Font font1 = new Font("TimesRoman", 0, 12);
        Font font2 = new Font("TimesRoman", 0, 11);

        Graphics2D g = (Graphics2D) graphics;
        g.setColor(color1);
        g.fillRoundRect(15, 15, 190, 120, 5, 5);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(15, 15, 190, 120, 5, 55);
        g.setFont(font1);
        g.drawString(gui.getCurrentMode() +" Fisher by Mecanos28", 28, 30);
        g.setFont(font2);
        g.drawString("Time Running: " + t.formatTime(), 29, 50);
        g.drawString("Fish catched:" + fishCatched, 29, 65);
        g.drawString("Levels gained: " + levelsGained + " | Current level: " + getSkillTracker().getStartLevel(Skill.FISHING), 29, 80);
        g.drawString("Fishing XP/H: " + getSkillTracker().getGainedExperiencePerHour(Skill.FISHING), 29, 95);
        g.drawString("Status: " + status, 29, 110);

    }

    @Override
    public void onGameMessage(Message message) {
        if(message.getMessage() != null && (message.getMessage().toLowerCase().contains("you catch a") || message.getMessage().toLowerCase().contains("you catch"))){
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


    public void randomCameraMovement() {
        int r = Calculations.random(1, 100);
        switch (r) {
            case 1:
                getCamera().rotateToPitch(Calculations.random(333, 399));
                break;
            case 2:
                getCamera().rotateToYaw(Calculations.random(1420, 1700));
                break;
            case 3:
                getCamera().rotateToYaw(Calculations.random(455, 700));
                break;
            default:
        }
    }

    public void moveCursor() {
        int r = Calculations.random(1, 5);
        switch (r) {
            case 2:
            case 3:
                moveCursorOutside();
                break;
            case 4:
            case 5:
                getMouse().move(getClient().getCanvasImage().getRaster().getBounds());
                break;
        }
    }

    private void moveCursorOutside() {
        if (getMouse().isMouseInScreen()) {
            getMouse().moveMouseOutsideScreen();
        }
    }

    public void processDialogue() {
        if (getDialogues().inDialogue()) {
            getDialogues().clickContinue();
        }
    }

    private void antiban() {
        switch (Calculations.random(0, 25)) {
            case 25:
                moveCursorOutside();
                status = "sleeping...";
                sleep(Calculations.random(110000, 245000));
                break;
            case 24:
                moveCursorOutside();
                status = "sleeping...";
                sleep(Calculations.random(110000, 245000));
                break;
            case 23:
                getTabs().open(Tab.SKILLS);
                sleep(Calculations.random(5000,14000));
                getTabs().open(Tab.INVENTORY);
            case 22:
                getTabs().open(Tab.EQUIPMENT);
                sleep(Calculations.random(5000, 14000));
                getTabs().open(Tab.INVENTORY);

        }
    }


    public void findWithCamera(NPC npc) {
        if (!npc.isOnScreen()) {
            status = "Rotating camera...";
            log("object is not on screen, rotating camera...");
            sleep(Calculations.random(0, 1500));
            getCamera().rotateToTile(npc.getTile().getRandomizedTile());
            sleep(Calculations.random(0, 1500));
        }
    }

    public void findWithCamera(GameObject object) {
        if (!object.isOnScreen()) {
            status = "Rotating camera...";
            log("object is not on screen, rotating camera...");
            sleep(Calculations.random(0, 1500));
            getCamera().rotateToTile(object.getTile().getRandomizedTile());
            sleep(Calculations.random(0, 1500));
        }
    }

    public void findWithCamera(Entity entity) {
        if (!entity.isOnScreen()) {
            status = "Rotating camera...";
            log("object is not on screen, rotating camera...");
            sleep(Calculations.random(0, 1500));
            getCamera().rotateToTile(entity.getTile().getRandomizedTile());
            sleep(Calculations.random(0, 1500));
        }
    }






    private void testCode(){
    }


}