package fisher;

import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class LumbridgeCooker {

    BotMain m;

    public LumbridgeCooker(BotMain m) {
        this.m = m;
    }


    public enum CookerStates {
        COOKING,
        GOING_TO_BANK,
        IN_SECOND_FLOOR_GOING_UP,
        BANKING,
        JUST_BANKED,
        IN_SECOND_FLOOR_GOING_DOWN
    }

    public CookerStates getCurrentLumbridgeCookerState(){
        if(m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && m.traveler.isInGroundLevel()){
            m.log("Trying to cook...");
            m.status = "Trying to cook...";
            return CookerStates.COOKING;
        }
        else if (!m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && m.traveler.isInGroundLevel()){
            m.log("Going to go to bank...");
            m.status = "Going to go to  bank..";
            return CookerStates.GOING_TO_BANK;
        }
        else if (!m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && m.traveler.isInSecondLevel()){
            m.log("Going upstairs...");
            m.status = "Going upstairs..";
            return CookerStates.IN_SECOND_FLOOR_GOING_UP;
        }
        else if (!m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && m.traveler.isInThirdLevel()){
            m.log("Banking...");
            m.status = "Banking...";
            return CookerStates.BANKING;
        }
        else if (m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && m.traveler.isInThirdLevel()){
            m.log("Just banked and got new raw food..");
            m.status = "Just banked... going to second floor.";
            return CookerStates.JUST_BANKED;
        }
        else if (m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && m.traveler.isInSecondLevel()){
            m.log("Going downstairs to cook...");
            m.status = "Going downstairs to cook...";
            return CookerStates.IN_SECOND_FLOOR_GOING_DOWN;
        }
        return null;
    }

    public void processCurrentLumbridgeCookerState(CookerStates curentState){
        m.randomCameraMovement();

        switch(curentState) {
            case COOKING:
                m.cookHelper.actiaveLumbridgeCooker();
                break;
            case GOING_TO_BANK:
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
