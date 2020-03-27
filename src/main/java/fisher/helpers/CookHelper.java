package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.List;

import static org.dreambot.api.methods.MethodProvider.log;

public class CookHelper extends Helper {

    private String cookItemName;

    public CookHelper(BotMain m) {
        super(m);
    }


    public CookHelper(BotMain m, String cookItemName) {
        super(m);
        this.cookItemName = cookItemName;
    }

    public void actiaveLumbridgeCooker() {
        if(inLumbridgeCookingArea()){
            cook();
        }else{
            m.status = "Walking to kitchen...";
            if(m.getWalking().walk(m.lumbridgeCookingArea.getRandomTile())){
                MethodProvider.sleepUntil(() -> inLumbridgeCookingArea(),Calculations.random(3000, 5500));
            }
        }
    }

    public void cook() {
        m.status = "Trying to click cooking spot";
        if(!m.getTabs().isOpen(Tab.INVENTORY)){
            m.getTabs().open(Tab.INVENTORY);
        }
        Entity range = getCloseByCookingSpot();
        makeInteractiveWidgetShow(range);
        m.status = "Trying to click cooking action";
        WidgetChild cook = m.getWidgets().getWidgetChild(270, 14);
        if(cook != null && cook.interact()) {
            m.status = "Cooking!";
            if (m.getLocalPlayer().isAnimating() || !m.getLocalPlayer().isStandingStill()) {
                MethodProvider.sleep(Calculations.random(1000, 3000));
                MethodProvider.sleepWhile(() -> m.getLocalPlayer().isAnimating() || m.getInventory().contains(cookItemName), Calculations.random(60000));
            }
        }
        if(!m.traveler.hasItemInInventory(m.cookHelper.getCookItemName())){
            MethodProvider.sleep(Calculations.random(1500, 10000));
        }
    }

    private void makeInteractiveWidgetShow(Entity range) {
        m.findWithCamera(range);
        clickEntity(range);
        List<WidgetChild> children = getFoodChildren();
        while (children.isEmpty()){
            clickEntity(range);
            MethodProvider.sleep(Calculations.random(1000, 2400));
            children = getFoodChildren();
        }
    }

    private Entity getCloseByCookingSpot() {
        Entity range = m.getGameObjects().closest(g -> g != null && (g.getName().contains("range") || g.getName().equals("Clay oven") || g.getName().equals("Fire")));
        while (range == null) {
            MethodProvider.sleep(Calculations.random(1000, 2000));
            range = m.getGameObjects().closest(g -> g != null && (g.getName().contains("range")  || g.getName().equals("Clay oven") || g.getName().equals("Fire")));
        }
        m.status = "Found " + range.getName();
        return range;
    }

    public List<WidgetChild> getFoodChildren () {
        return m.getWidgets().getWidgets(w -> w != null && w.getText().contains("How many would you like"));
    }

    private void clickEntity(Entity entity) {
        entity.interact();
    }

    private boolean inLumbridgeCookingArea() {
        return m.lumbridgeCookingArea.contains(m.getLocalPlayer()) || m.getLocalPlayer().distance(m.lumbridgeCookingArea.getCenter()) < 3;
    }

    public String getCookItemName() {
        return cookItemName;
    }

    public void setCookItemName(String cookItemName) {
        this.cookItemName = cookItemName;
    }
}
