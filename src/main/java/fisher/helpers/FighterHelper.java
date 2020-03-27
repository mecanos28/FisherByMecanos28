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


    public void buryBones(){
        for (Item i: m.getInventory().all()) {
            if (i != null && i.hasAction("Bury")) {
                if (i.interact("Bury"))
                    MethodProvider.sleep(Calculations.random(750, 980));
            }
        }
    }
}
