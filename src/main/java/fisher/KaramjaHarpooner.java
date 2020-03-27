package fisher;

import org.dreambot.api.methods.MethodProvider;

public class KaramjaHarpooner {
    
    BotMain m;
    
    public KaramjaHarpooner(BotMain m) {
        this.m = m;
    }


    public enum HarpooningStates {
        FISHING,
        INSIDE_BOAT_KARAMJA,
        OUTSIDE_BOAT_KARAMJA,
        INSIDE_BOAT_PORT_SARIM,
        OUTSIDE_BOAT_PORT_SARIM_BANK,
        OUTSIDE_BOAT_PORT_SARIM_SELL,
        OUTSIDE_BOAT_PORT_SARIM_PAY_FARE,
        IN_GENERAL_STORE_WITH_MONEY,
        IN_GENERAL_STORE_WITHOUT_MONEY,
    }

    public HarpooningStates getCurrentHarpoonState(){
        if(!m.traveler.hasFullInventory() && m.traveler.isInKaramjaIsland() && !m.traveler.isInsideKaramjaBoat()){
            MethodProvider.log("Trying to go fish...");
            m.status = "Trying to go fish...";
            return HarpooningStates.FISHING;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInKaramjaIsland() && m.traveler.isInsideKaramjaBoat()){
            MethodProvider.log("Trying to leave boat...");
            m.status = "Trying to leave boat...";
            return HarpooningStates.INSIDE_BOAT_KARAMJA;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInKaramjaIsland() && !m.traveler.isInsideKaramjaBoat()){
            MethodProvider.log("Trying to pay fare...");
            m.status = "Trying to pay fare...";
            return HarpooningStates.OUTSIDE_BOAT_KARAMJA;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInPortSarim() && m.traveler.isInsidePortSarimBoat()){
            MethodProvider.log("Trying to leave boat...");
            m.status = "Trying to leave boat...";
            return HarpooningStates.INSIDE_BOAT_PORT_SARIM;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInPortSarim() && !m.traveler.isInsidePortSarimBoat() && m.moneyMaker.haveCoinsForNextTravel()){
            MethodProvider.log("Trying to bank fish...");
            m.status = "Trying to bank fish...";
            return HarpooningStates.OUTSIDE_BOAT_PORT_SARIM_BANK;
        }
        else if (m.traveler.hasFullInventory() && m.traveler.isInPortSarim() && !m.traveler.isInsidePortSarimBoat() && !m.moneyMaker.haveCoinsForNextTravel()){
            MethodProvider.log("No Coins... Selling fish...");
            m.status = "Trying to sell fish...";
            return HarpooningStates.OUTSIDE_BOAT_PORT_SARIM_SELL;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInPortSarim() && !m.traveler.isInsidePortSarimBoat()){
            MethodProvider.log("Trying to travel to Karamja...");
            m.status = "Trying to travel to Karamja...";
            return HarpooningStates.OUTSIDE_BOAT_PORT_SARIM_PAY_FARE;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInGeneralStore() && m.moneyMaker.haveCoinsForNextTravel() ){
            MethodProvider.log("In general store with new money... Going to Karamja.");
            m.status = "New money... Going to Karamja.";
            return HarpooningStates.IN_GENERAL_STORE_WITH_MONEY;
        }
        else if (!m.traveler.hasFullInventory() && m.traveler.isInGeneralStore() && !m.moneyMaker.haveCoinsForNextTravel() ){
            MethodProvider.log("In general store with new money... Going to Karamja.");
            m.status = "New money... Going to Karamja.";
            return HarpooningStates.IN_GENERAL_STORE_WITHOUT_MONEY;
        }
        return null;
    }

    public void processHarpoonState(HarpooningStates curentState){
        m.randomCameraMovement();
        switch(curentState) {
            case FISHING:
                m.fisher.activateHarpoonFisher();
                break;
            case INSIDE_BOAT_KARAMJA:
            case INSIDE_BOAT_PORT_SARIM:
                m.traveler.crossPlank();
                break;
            case OUTSIDE_BOAT_KARAMJA:
                m.traveler.walkToKaramjaPier();
                m.traveler.payFareToPortSarim();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_BANK:
                m.traveler.depositKaramjaLoot();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_PAY_FARE:
            case IN_GENERAL_STORE_WITH_MONEY:
                m.traveler.walkToPortSarimPier();
                m.traveler.payFareToKaramja();
                break;
            case OUTSIDE_BOAT_PORT_SARIM_SELL:
                m.traveler.walkToGeneralStore();
                break;
            case IN_GENERAL_STORE_WITHOUT_MONEY:
                m.moneyMaker.sellFish();
                break;
            default:
                MethodProvider.log("Program has bugged out. :(");
        }

    }
}
