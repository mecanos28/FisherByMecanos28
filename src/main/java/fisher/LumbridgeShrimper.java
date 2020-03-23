package fisher;

import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class LumbridgeShrimper {

    FisherMain m;

    public LumbridgeShrimper(FisherMain m) {
        this.m = m;
    }


    public enum ShrimperStates {
        FISHING,
        GOING_TO_BANK_OUTSIDE_CASTLE,
        IN_SECOND_FLOOR_GOING_UP,
        BANKING,
        JUST_BANKED,
        IN_SECOND_FLOOR_GOING_DOWN
    }

    public ShrimperStates getCurrentShriperState(){
        if(!m.traveler.hasFullInventory() && m.traveler.isInGroundLevel()){
            m.log("Trying to go fish...");
            m.status = "Trying to go fish...";
            return ShrimperStates.FISHING;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInGroundLevel()){
            m.log("Going to castle...");
            m.status = "Going to castle..";
            return ShrimperStates.GOING_TO_BANK_OUTSIDE_CASTLE;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInSecondLevel()){
            m.log("Going upstairs...");
            m.status = "Going upstairs..";
            return ShrimperStates.IN_SECOND_FLOOR_GOING_UP;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInThirdLevel()){
            m.log("Banking...");
            m.status = "Banking...";
            return ShrimperStates.BANKING;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInThirdLevel()){
            m.log("Just banked..");
            m.status = "Just banked... going to second floor.";
            return ShrimperStates.JUST_BANKED;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInSecondLevel()){
            m.log("Going downstairs...");
            m.status = "Going downstairs...";
            return ShrimperStates.IN_SECOND_FLOOR_GOING_DOWN;
        }
        return null;
    }

    public void processShrimpState(ShrimperStates curentState){
        m.randomCameraMovement();
        switch(curentState) {
            case FISHING:
                m.fisher.activateLumbridgeShrimpFisher();
                break;
            case GOING_TO_BANK_OUTSIDE_CASTLE:
                m.traveler.walkToArea(m.lumbridgeFirstFloorStairsArea);
                m.traveler.interactWithStaircase("Climb-up");
                break;
            case IN_SECOND_FLOOR_GOING_UP:
                m.traveler.interactWithStaircase("Climb-up");
                break;
            case BANKING:
                m.traveler.walkToArea(BankLocation.LUMBRIDGE.getArea(1));
                m.traveler.bankShrimp();
                break;
            case JUST_BANKED:
                m.traveler.walkToArea(m.lumbridgeThirdFloorStairsArea);
                m.traveler.interactWithStaircase("Climb-down");
                break;
            case IN_SECOND_FLOOR_GOING_DOWN:
                m.traveler.interactWithStaircase("Climb-down");
                break;
            default:
                m.log("Program has bugged out. :(");
        }

    }
}
