package fisher;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class DraynorShrimper {

    BotMain m;

    public DraynorShrimper(BotMain m) {
        this.m = m;
    }

    public enum RimmingtonShrimperStates {
        FISHING,
        BANKING
    }

    public RimmingtonShrimperStates getCurrentShriperState(){
        if(!m.traveler.hasFullInventory()){
            MethodProvider.log("Trying to go fish...");
            m.status = "Trying to go fish...";
            return RimmingtonShrimperStates.FISHING;
        }
        else if (m.traveler.hasFullInventory()) {
            MethodProvider.log("Going to bank...");
            m.status = "Going to bank..";
            return RimmingtonShrimperStates.BANKING;
        }
        return null;
    }

    public void processShrimpState(RimmingtonShrimperStates curentState){
        m.randomCameraMovement();
        switch(curentState) {
            case FISHING:
                m.fisher.activateDraynorShrimpFisher();
                break;
            case BANKING:
                m.traveler.walkToArea(BankLocation.DRAYNOR.getArea(1));
                m.traveler.bankShrimp();
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
