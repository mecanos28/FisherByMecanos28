package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.List;

import static org.dreambot.api.methods.MethodProvider.sleepUntil;

public class FighterHelper extends Helper {

    private String cookItemName;

    public FighterHelper(BotMain m) {
        super(m);
    }


    public FighterHelper(BotMain m, String cookItemName) {
        super(m);
        this.cookItemName = cookItemName;
    }

    public void killAndLootChickenBonesAndFeathers(){
        //Getting Chicken
        NPC chicken = m.getCloseByNPC("chicken");
        if(chicken != null && !chicken.isInCombat() && chicken.canAttack()) {
            chicken.interact("Attack");
            m.getCamera().rotateToEntity(chicken);
            MethodProvider.sleep(Calculations.random(1000, 3000));
            MethodProvider.sleepWhile(() -> m.getLocalPlayer().isInCombat(),Calculations.random(60000));
            MethodProvider.sleep(Calculations.random(2000, 3000));
        }
        lootChickenItems();
    }

    public void lootChickenItems() {
        GroundItem feather = m.getCloseByGroundItem("feather"); //Getting Feather
        if(feather != null && !m.traveler.hasFullInventory() && !m.getLocalPlayer().isInCombat()) {
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(feather);
            MethodProvider.sleep(Calculations.random(800, 900));
            feather = m.getCloseByGroundItem("feather");
        }

        GroundItem arrows = m.getCloseByGroundItem("arrow"); //Getting Arrows
        if(arrows != null && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(arrows);
            MethodProvider.sleep(Calculations.random(800, 900));
            arrows = m.getCloseByGroundItem("arrow");
        }

        GroundItem bones = m.getCloseByGroundItem("bones"); //Getting Arrows
        if(bones != null  && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(bones);
            MethodProvider.sleep(Calculations.random(800, 900));
            bones = m.getCloseByGroundItem("bones");
        }
    }

    public void activateKillAndLootCows(){
        if(inLumbrdigeCowArea()){
            killCowsAndLoot();
        }else{
            m.status = "Walking to get some cows...";
            if(m.getWalking().walk(m.lumbridgeCowArea.getRandomTile())){
                sleepUntil(() -> inLumbrdigeCowArea(),Calculations.random(3000, 5500));
            }
        }

    }


    public void activateKillAndLootGiants(){
        if(inHillGiantArea()){
            killGiantsAndLoot();
        }else{
            m.status = "Walking to get some giants...";
            if(m.getWalking().walk(m.dungeonHillGiantArea.getRandomTile())){
                sleepUntil(() -> inHillGiantArea(),Calculations.random(3000, 5500));
            }
        }

    }

    public void killCowsAndLoot() {
        NPC cow = m.getCloseByNPC("cow");
        if(cow != null && !cow.isInCombat() && cow.canAttack()) {
            cow.interact("Attack");
            m.getCamera().rotateToEntity(cow);
            MethodProvider.sleep(Calculations.random(1000, 3000));
            MethodProvider.sleepWhile(() -> m.getLocalPlayer().isInCombat(),Calculations.random(60000));
            MethodProvider.sleep(Calculations.random(2000, 3000));
        }
        lootCowItems();
    }

    public void killGiantsAndLoot() {
        NPC giant = m.getCloseByNPC("giant");
        if(giant != null && !giant.isInCombat() && giant.canAttack()) {
            m.status = "trying to attack giant...";
            giant.interact("Attack");
            m.getCamera().rotateToEntity(giant);
            MethodProvider.sleep(Calculations.random(1000, 3000));
            MethodProvider.sleepWhile(() -> m.getLocalPlayer().isInCombat(),Calculations.random(60000));
            MethodProvider.sleep(Calculations.random(2000, 3000));
        }

        lootGiantItems();
    }

    public void lootCowItems() {
        GroundItem arrows = m.getCloseByGroundItem("arrow"); //Getting Arrows
        if(arrows != null && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(arrows);
            MethodProvider.sleep(Calculations.random(800, 900));
            arrows = m.getCloseByGroundItem("arrow");
        }

//        GroundItem cowhide = m.getCloseByGroundItem("cowhide"); //Getting Feather
//        if(cowhide != null && !m.traveler.hasFullInventory() && !m.getLocalPlayer().isInCombat()) {
//            MethodProvider.sleep(Calculations.random(300, 500));
//            m.lootNearbyItem(cowhide);
//            MethodProvider.sleep(Calculations.random(800, 900));
//            cowhide = m.getCloseByGroundItem("feather");
//        }

        GroundItem bones = m.getCloseByGroundItem("bones"); //Getting Arrows
        if(bones != null  && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(bones);
            MethodProvider.sleep(Calculations.random(800, 900));
            bones = m.getCloseByGroundItem("bones");
        }
    }

    public void lootGiantItems() {
        m.status = "looting giant...";

        GroundItem arrows = m.getCloseByGroundItem("bronze arrow"); //Getting Arrows
        if(arrows != null && !m.getLocalPlayer().isInCombat()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(arrows);
            MethodProvider.sleep(Calculations.random(800, 900));
        }

        GroundItem root = m.getCloseByGroundItem("root"); //Getting Feather
        if(root != null && !m.traveler.hasFullInventory() && !m.getLocalPlayer().isInCombat()) {
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(root);
            MethodProvider.sleep(Calculations.random(800, 900));
        }

        GroundItem bones = m.getCloseByGroundItem("bones"); //Getting Arrows
        if(bones != null  && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(bones);
            MethodProvider.sleep(Calculations.random(800, 900));
        }

        GroundItem runes = m.getCloseByGroundItem("rune"); //Getting Arrows
        if(runes != null  && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(runes);
            MethodProvider.sleep(Calculations.random(800, 900));
        }

        GroundItem coins = m.getCloseByGroundItem("coins"); //Getting Arrows
        if(coins != null  && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(coins);
            MethodProvider.sleep(Calculations.random(800, 900));
        }

        GroundItem uncut = m.getCloseByGroundItem("uncut"); //Getting Arrows
        if(uncut != null  && !m.traveler.hasFullInventory()){
            MethodProvider.sleep(Calculations.random(300, 500));
            m.lootNearbyItem(uncut);
            MethodProvider.sleep(Calculations.random(800, 900));
        }
    }


    public void buryBones(){
        for (Item i: m.getInventory().all()) {
            if (i != null && i.hasAction("Bury")) {
                if (i.interact("Bury"))
                    MethodProvider.sleep(Calculations.random(800, 980));
            }
        }
    }

    public boolean inLumbrdigeCowArea() {
        return m.lumbridgeCowArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.lumbridgeCowArea.getCenter()) < 3;
    }

    public boolean inHillGiantArea() {
        return m.dungeonHillGiantArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.dungeonHillGiantArea.getCenter()) < 50;
    }

    public boolean inDungeonRoomHillGiantArea() {
        return m.hillGiantRoomArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.hillGiantRoomArea.getCenter()) < 2;
    }
}
