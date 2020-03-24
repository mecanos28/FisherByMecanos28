package fisher.helpers;

import fisher.BotMain;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import static org.dreambot.api.methods.MethodProvider.log;

public class CoinsHelper extends Helper {

    public CoinsHelper(BotMain m) {
        super(m);
    }

    public boolean haveCoinsForNextTravel(){
        if (m.getInventory().count("Coins") < 60) {
            return false;
        }
        return true;
    }

    public void sellFish() {
        log("Trading...");
        m.status="Opening trade window...";
        openTradeWindow();
        m.sleep(Calculations.random(4000, 7000));
        m.status="Selling fish...";
        sellRawFish();
        m.sleep(Calculations.random(2000, 2500));
        if(!haveCoinsForNextTravel()){
            log("Logged out, no more coins...");
            m.status="Logging out..";
            m.getTabs().logout();
            m.sleep(5000);
            m.onExit();
        }
    }

    private void openTradeWindow() {
        NPC trader = m.traveler.getGuyByActionName("Trade");
        m.findWithCamera(trader);
        trader.interact("Trade");
        log("Trading...");
    }

    private void sellRawFish() {
        WidgetChild shop = m.getWidgets().getWidgetChild(300, 1);
        m.sleep(Calculations.random(1000));
        if (shop.isVisible()) {
            m.sleep(Calculations.random(0, 1500));
            m.getInventory().get(item -> item != null && item.getName().toLowerCase().contains("raw")).interact("Sell 50");
        }
    }
}
