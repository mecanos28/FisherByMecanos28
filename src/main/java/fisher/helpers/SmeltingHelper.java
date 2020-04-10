package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.List;

public class SmeltingHelper extends Helper {

    public String currentOreName;


    public SmeltingHelper(BotMain m, String oreName) {
        super(m);
        currentOreName = oreName;
    }

    public void activateFaladorSmelterTinCopper() {
        if(inFaladorSmeltingArea()){
            smelt();
        }else{
            m.status = "Walking to furnace...";
            if(m.getWalking().walk(m.faladorFurnaceArea.getRandomTile())){
                MethodProvider.sleepUntil(() -> inFaladorSmeltingArea(),Calculations.random(3000, 5500));
            }
        }
    }

    public void smelt() {
        m.status = "Trying to click cooking spot";
        if(!m.getTabs().isOpen(Tab.INVENTORY)){
            m.getTabs().open(Tab.INVENTORY);
        }
        Entity furnace = getCloseByFurnace();
        makeInteractiveWidgetShow(furnace);
        m.status = "Trying to click smelting action";
        WidgetChild smeltBronze = m.getWidgets().getWidgetChild(270, 14);
        if(smeltBronze != null && smeltBronze.interact()) {
            MethodProvider.sleep(Calculations.random(1000, 3000));
            m.status = "Smelting!!";
            if (m.getLocalPlayer().isAnimating() || !m.getLocalPlayer().isStandingStill()) {
                MethodProvider.sleep(Calculations.random(1000, 3000));
                MethodProvider.sleepWhile(() -> m.getLocalPlayer().isAnimating() || m.getInventory().contains(currentOreName), Calculations.random(60000));
            }
        }
        if(!m.traveler.hasItemInInventory(currentOreName)){
            MethodProvider.sleep(Calculations.random(1500, 10000));
        }
    }

    private void makeInteractiveWidgetShow(Entity range) {
        m.findWithCamera(range);
        if(range.interact()){
            List<WidgetChild> children = getSmeltChildren();
            while (children.isEmpty()){
                clickEntity(range);
                MethodProvider.sleep(1800);
                children = getSmeltChildren();
            }
        }
    }

    private Entity getCloseByFurnace() {
        Entity range = m.getGameObjects().closest(g -> g != null && (g.getName().toLowerCase().contains("furnace")));
        while (range == null) {
            MethodProvider.sleep(Calculations.random(1000, 2000));
            range = m.getGameObjects().closest(g -> g != null && (g.getName().toLowerCase().contains("furnace")));
        }
        m.status = "Found " + range.getName();
        return range;
    }

    public List<WidgetChild> getSmeltChildren () {
        return m.getWidgets().getWidgets(w -> w != null && w.getText().contains("What would you like"));
    }

    private void clickEntity(Entity entity) {
        entity.interact();
    }

    private boolean inFaladorSmeltingArea() {
        return m.faladorFurnaceArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.lumbridgeCookingArea.getCenter()) < 6;
    }


}
