package fisher;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class LumbridgeCowKiller {

    BotMain m;

    public LumbridgeCowKiller(BotMain m) {
        this.m = m;
    }


    public enum CowKillerStates {
        KILLING,
        BURYING,
        LOOTING,
        GOING_TO_BANK_OUTSIDE_CASTLE,
        IN_SECOND_FLOOR_GOING_UP,
        BANKING,
        JUST_BANKED,
        IN_SECOND_FLOOR_GOING_DOWN

    }

    public CowKillerStates getCurrentFighterState(){
        if(shouldLootCow() && m.fighterHelper.inLumbrdigeCowArea()){
            MethodProvider.log("Trying to loot cows..");
            m.status = "Trying to loot cows...";
            return CowKillerStates.LOOTING;
        }
        else if(!m.traveler.hasFullInventory() && m.traveler.isInGroundLevel()){
            MethodProvider.log("Trying to kill cows..");
            m.status = "Trying to kill cows...";
            return CowKillerStates.KILLING;
        }
        else if(m.traveler.hasFullInventory() && m.getInventory().contains(item -> item != null && item.getName().toLowerCase().contains("bones"))){
            MethodProvider.log("Going to bury bones...");
            m.status = "Going to bury bones..";
            return CowKillerStates.BURYING;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInGroundLevel()){
            MethodProvider.log("Going to castle...");
            m.status = "Going to castle..";
            return CowKillerStates.GOING_TO_BANK_OUTSIDE_CASTLE;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInSecondLevel()){
            MethodProvider.log("Going upstairs...");
            m.status = "Going upstairs..";
            return CowKillerStates.IN_SECOND_FLOOR_GOING_UP;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInThirdLevel()){
            MethodProvider.log("Banking...");
            m.status = "Banking...";
            return CowKillerStates.BANKING;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInThirdLevel()){
            MethodProvider.log("Just banked..");
            m.status = "Just banked... going to second floor.";
            return CowKillerStates.JUST_BANKED;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInSecondLevel()){
            MethodProvider.log("Going downstairs...");
            m.status = "Going downstairs...";
            return CowKillerStates.IN_SECOND_FLOOR_GOING_DOWN;
        }
        return null;
    }

    public boolean shouldLootCow() {
        return (m.getCloseByGroundItem("arrow") != null
                || m.getCloseByGroundItem("bones") != null
//                || m.getCloseByGroundItem("cowhide") != null
        )
                && !m.traveler.hasFullInventory();
    }

    public void processFighterState(CowKillerStates curentState){
        m.randomCameraMovement();

        switch(curentState) {
            case KILLING:
                m.fighterHelper.activateKillAndLootCows();
                break;
            case BURYING:
                m.fighterHelper.buryBones();
                break;
            case LOOTING:
                m.fighterHelper.lootCowItems();
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
