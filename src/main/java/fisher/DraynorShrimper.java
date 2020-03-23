package fisher;

import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class DraynorShrimper {

    FisherMain m;

    public DraynorShrimper(FisherMain m) {
        this.m = m;
    }

    public enum RimmingtonShrimperStates {
        FISHING,
        BANKING
    }

    public RimmingtonShrimperStates getCurrentShriperState(){
        if(!m.traveler.hasFullInventory()){
            m.log("Trying to go fish...");
            m.status = "Trying to go fish...";
            return RimmingtonShrimperStates.FISHING;
        }
        else if (m.traveler.hasFullInventory()) {
            m.log("Going to bank...");
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
                m.log("Program has bugged out. :(");
        }

    }
}
