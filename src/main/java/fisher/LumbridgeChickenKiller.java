package fisher;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.wrappers.items.Item;

public class LumbridgeChickenKiller {

    BotMain m;

    public LumbridgeChickenKiller(BotMain m) {
        this.m = m;
    }


    public enum ChickenKillerStates {
        KILLING,
        BURYING,
        LOOTING
    }

    public ChickenKillerStates getCurrentFighterState(){
        if(shouldLootChicken()){
            MethodProvider.log("Trying to kill chickens..");
            m.status = "Trying to kill chickens...";
            return ChickenKillerStates.LOOTING;
        }
        else if(!m.traveler.hasFullInventory()){
            MethodProvider.log("Trying to kill chickens..");
            m.status = "Trying to kill chickens...";
            return ChickenKillerStates.KILLING;
        }
        else {
            MethodProvider.log("Going to bury bones...");
            m.status = "Going to bury bones..";
            return ChickenKillerStates.BURYING;
        }
    }

    public boolean shouldLootChicken() {
        return (m.getCloseByGroundItem("arrow") != null
                || m.getCloseByGroundItem("bones") != null
                || m.getCloseByGroundItem("feather") != null)
                && !m.traveler.hasFullInventory();
    }

    public void processFighterState(ChickenKillerStates curentState){
        m.randomCameraMovement();

        switch(curentState) {
            case KILLING:
                m.fighterHelper.killAndLootChickenBonesAndFeathers();
                break;
            case BURYING:
                m.fighterHelper.buryBones();
                break;
            case LOOTING:
                m.fighterHelper.lootChickenItems();
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
