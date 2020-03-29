package fisher;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class LumbridgeMiner {

    BotMain m;

    public LumbridgeMiner(BotMain m) {
        this.m = m;
    }


    public enum MinerStates {
        MINING,
        GOING_TO_BANK_OUTSIDE_CASTLE,
        IN_SECOND_FLOOR_GOING_UP,
        BANKING,
        JUST_BANKED,
        IN_SECOND_FLOOR_GOING_DOWN
    }

    public MinerStates getCurrentMiningState(){
        if(!m.traveler.hasFullInventory() && m.traveler.isInGroundLevel()){
            MethodProvider.log("Trying to go fish...");
            m.status = "Trying to go fish...";
            return MinerStates.MINING;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInGroundLevel()){
            MethodProvider.log("Going to castle...");
            m.status = "Going to castle..";
            return MinerStates.GOING_TO_BANK_OUTSIDE_CASTLE;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInSecondLevel()){
            MethodProvider.log("Going upstairs...");
            m.status = "Going upstairs..";
            return MinerStates.IN_SECOND_FLOOR_GOING_UP;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInThirdLevel()){
            MethodProvider.log("Banking...");
            m.status = "Banking...";
            return MinerStates.BANKING;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInThirdLevel()){
            MethodProvider.log("Just banked..");
            m.status = "Just banked... going to second floor.";
            return MinerStates.JUST_BANKED;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInSecondLevel()){
            MethodProvider.log("Going downstairs...");
            m.status = "Going downstairs...";
            return MinerStates.IN_SECOND_FLOOR_GOING_DOWN;
        }
        return null;
    }

    public void processMineState(MinerStates curentState){
        m.randomCameraMovement();

        switch(curentState) {
            case MINING:
                m.mineHelper.activateLumbridgeMiner();
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
                m.traveler.depositAllInventory();
                break;
            case JUST_BANKED:
                m.traveler.interactWithStaircase("Climb-down");
                break;
            case IN_SECOND_FLOOR_GOING_DOWN:
                m.traveler.interactWithStaircase("Climb-down");
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
