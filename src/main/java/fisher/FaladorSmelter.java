package fisher;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;

public class FaladorSmelter {

    BotMain m;

    public FaladorSmelter(BotMain m) {
        this.m = m;
    }



    public enum SmelterStates {
        SMELTING,
        GOING_TO_BANK,
        BANKING
    }

    public SmelterStates getCurrentFaladorSmelterState(){
        if(m.traveler.hasItemInInventory(m.smeltingHelper.currentOreName)){
            MethodProvider.log("Trying to smelt...");
            m.status = "Trying to smelt...";
            return SmelterStates.SMELTING;
        }
        else if (!m.traveler.hasItemInInventory(m.smeltingHelper.currentOreName) && !(m.getLocalPlayer().distance(BankLocation.FALADOR_WEST.getArea(1).getCenter()) < 3)){
            MethodProvider.log("Going to go to bank...");
            m.status = "Going to go to  bank..";
            return SmelterStates.GOING_TO_BANK;
        }
        else if (!m.traveler.hasItemInInventory(m.cookHelper.getCookItemName()) && (m.getLocalPlayer().distance(BankLocation.FALADOR_WEST.getArea(1).getCenter()) < 3)){
            MethodProvider.log("Banking...");
            m.status = "Banking...";
            return SmelterStates.BANKING;
        }
        return null;
    }

    public void processSmelterState(SmelterStates curentState){
        m.randomCameraMovement();

        switch(curentState) {
            case SMELTING:
                m.smeltingHelper.activateFaladorSmelterTinCopper();
                break;
            case GOING_TO_BANK:
                m.traveler.walkToArea(BankLocation.FALADOR_WEST.getArea(1));
                break;
            case BANKING:
                m.traveler.depositAllAndWithdrawForBronze();
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
