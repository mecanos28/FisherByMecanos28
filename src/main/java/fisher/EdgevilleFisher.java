package fisher;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class EdgevilleFisher {

    BotMain m;

    public EdgevilleFisher(BotMain m) {
        this.m = m;
    }



    public enum FisherStates {
        FISHING,
        COOKING,
        GOING_TO_BANK,
        BANKING
    }

    public FisherStates getCurrentFisherState(){
        if(!m.getInventory().isFull()){
            MethodProvider.log("Trying to fish...");
            m.status = "Trying to fish...";
            return FisherStates.FISHING;
        }
        else if(m.getInventory().isFull() && m.getInventory().contains(item -> item != null && item.getName().toLowerCase().contains("raw"))){
            MethodProvider.log("Trying to fish...");
            m.status = "Trying to fish...";
            return FisherStates.COOKING;
        }
        else if (m.getInventory().isFull() && !(m.getLocalPlayer().distance(BankLocation.EDGEVILLE.getArea(1).getCenter()) < 14)){
            MethodProvider.log("Going to go to bank...");
            m.status = "Going to go to  bank..";
            return FisherStates.GOING_TO_BANK;
        }
        else if (m.getInventory().isFull() && (m.getLocalPlayer().distance(BankLocation.EDGEVILLE.getArea(1).getCenter()) < 14)){
            MethodProvider.log("Banking...");
            m.status = "Banking...";
            return FisherStates.BANKING;
        }
        return null;
    }

    public void processState(FisherStates curentState){
        m.randomCameraMovement();

        switch(curentState) {
            case FISHING:
                m.fisher.activateEdgevilleFlyFisher();
                break;
            case COOKING:
                m.traveler.walkToArea(m.edgeVilleFireArea);
                m.cookHelper.cookAllRaw();
                break;
            case GOING_TO_BANK:
                m.traveler.walkToArea(BankLocation.EDGEVILLE.getArea(1));
                break;
            case BANKING:
                m.traveler.bankFlyFishing();
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
