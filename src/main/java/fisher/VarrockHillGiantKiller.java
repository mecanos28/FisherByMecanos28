package fisher;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.skills.Skill;

public class VarrockHillGiantKiller {

    BotMain m;

    public VarrockHillGiantKiller(BotMain m) {
        this.m = m;
    }


    public enum HillGiantKillerStates {
        KILLING,
        LOOTING,
        GOING_TO_LADDER_UP,
        GOING_TO_LADDER_DOWN,
        BANKING,
        GOINT_TO_CAVE,
        BURYING

    }

    public HillGiantKillerStates getCurrentFighterState(){
        if(m.traveler.hasFood("trout") && m.traveler.hasFullInventory() && m.getInventory().contains(item -> item != null && item.getName().toLowerCase().contains("bones"))){
            MethodProvider.log("Going to bury bones...");
            m.status = "Going to bury bones..";
            return HillGiantKillerStates.BURYING;
        }
        else if(m.traveler.hasFood("trout") && shouldLootHillGiant() && m.fighterHelper.inHillGiantArea()){
            MethodProvider.log("Trying to loot giamts..");
            m.status = "Trying to loot giamts...";
            return HillGiantKillerStates.LOOTING;
        }
        else if(m.traveler.hasFood("trout") && m.fighterHelper.inHillGiantArea()){
            MethodProvider.log("Trying to kill giants..");
            m.status = "Trying to kill giants...";
            return HillGiantKillerStates.KILLING;
        }
        else if (m.traveler.hasFood("trout") && m.getInventory().isFull() && !m.fighterHelper.inHillGiantArea() && !m.fighterHelper.inDungeonRoomHillGiantArea()){
            MethodProvider.log("Going to cave...");
            m.status = "Going to cave...";
            return HillGiantKillerStates.GOINT_TO_CAVE;
        }
        else if (!m.traveler.hasFood("trout") && m.fighterHelper.inHillGiantArea()){
            MethodProvider.log("Banking...");
            m.status = "Banking...";
            return HillGiantKillerStates.GOING_TO_LADDER_UP;
        }
        else if (m.traveler.hasFood("trout") && (m.fighterHelper.inDungeonRoomHillGiantArea())){
            MethodProvider.log("Going down to dungeon...");
            m.status = "Going down to dungeon...";
            return HillGiantKillerStates.GOING_TO_LADDER_DOWN;
        }
        else if (!m.traveler.hasFood("trout") && (m.fighterHelper.inDungeonRoomHillGiantArea() || !m.fighterHelper.inHillGiantArea())){
            MethodProvider.log("Banking...");
            m.status = "Banking...";
            return HillGiantKillerStates.BANKING;
        }
        return null;

    }

    public boolean shouldLootHillGiant() {
        return !m.traveler.hasFullInventory() && (m.getCloseByGroundItem("bronze arrow") != null || m.getCloseByGroundItem("root") != null || m.getCloseByGroundItem("bones") != null);
    }

    public void eat(String foodname){
        if(m.getSkills().getBoostedLevels(Skill.HITPOINTS) < 28){
            m.getInventory().interact(foodname,"Eat");
            m.sleep(Calculations.random(800, 1500));
        }
    }

    public void processFighterState(HillGiantKillerStates curentState){
        m.randomCameraMovement();
        eat("Trout");
        switch(curentState) {
            case KILLING:
                m.fighterHelper.activateKillAndLootGiants();
                break;
            case BURYING:
                m.fighterHelper.buryBones();
                break;
            case LOOTING:
                m.fighterHelper.lootGiantItems();
                break;
            case GOING_TO_LADDER_UP:
                m.traveler.walkToAreaClose(m.dungeonHillGiantLadderArea);
                m.traveler.interactWithStaircase("Climb-up");
                break;
            case GOING_TO_LADDER_DOWN:
                m.traveler.interactWithStaircase("Climb-down");
                break;
            case BANKING:
                m.traveler.walkToArea(BankLocation.VARROCK_WEST.getArea(5));
                m.traveler.bankHillGiants();
                break;
            case GOINT_TO_CAVE:
                m.traveler.walkToAreaClose(m.hillGiantRoomArea);
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
