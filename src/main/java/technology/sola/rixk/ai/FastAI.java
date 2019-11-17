package technology.sola.rixk.ai;

import technology.sola.rixk.model.Territory;
import technology.sola.rixk.model.player.AbstractAI;

import java.util.Iterator;
import java.util.List;

public class FastAI extends AbstractAI {
  public FastAI() {
    setName("FastAI");
  }

  @Override
  public Territory chooseAttacker(List<Territory> arg0) {
    return arg0.get(0);
  }

  @Override
  public int chooseRoll(boolean arg0, int arg1, int arg2, Territory arg3, Territory arg4) {
    return arg2;
  }

  @Override
  public Territory chooseTarget(Territory[] arg0) {
    return arg0[0];
  }

  @Override
  public int fortify(int arg0, int arg1) {
    return arg1;
  }

  @Override
  public Territory selectFortifyTarget(List<Territory> arg0) {
    return arg0.get(0);
  }

  @Override
  public Territory selectFortifyingTerritory(List<Territory> arg0) {
    return arg0.get(0);
  }

  @Override
  public int takeOver(int arg0, int arg1) {
    return arg1;
  }

  @Override
  public boolean wantToAttack() {
    return true;
  }

  @Override
  public void placeArmies() {
    Territory placedAt = null;
    if (getArmiesToPlace() > 0) {
      Iterator<Territory> territoryIter = getOwnedTerritories().iterator();
      while (territoryIter.hasNext()) {
        placedAt = territoryIter.next();
        if (placedAt.getAttackTargets().size() > 0) {
          break;
        }
      }
      if (placedAt == null) {
        placedAt = getOwnedTerritories().get(0);
      }
      placeArmiesOnTerritories(placedAt, getArmiesToPlace());
    }
  }

  @Override
  public void placeOneArmy() {
    placeOneArmyOnTerritory(getOwnedTerritories().get(0));

  }
}

