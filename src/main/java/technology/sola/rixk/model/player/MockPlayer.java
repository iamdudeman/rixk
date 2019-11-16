package technology.sola.rixk.model.player;

import technology.sola.rixk.model.Territory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class MockPlayer implements Player {
  private final Logger LOGGER = Logger.getLogger(MockPlayer.class.getName());

  private static int mock_object_count = 0;

  private List<Territory> _territories = null;

  private int _armiesToPlace = 0;

  private String _name = null;

  public MockPlayer() {
    _territories = new LinkedList<>();
    _name = "MockPlayer" + mock_object_count++;
  }

  @Override
  public boolean ownsTerritory(Territory territory) {
    return _territories.contains(territory);
  }

  @Override
  public int getArmiesToPlace() {
    return _armiesToPlace;
  }

  @Override
  public void removeArmiesToPlace(int armiesPlaced) {
    _armiesToPlace -= armiesPlaced;
  }

  @Override
  public int getTotalOwnedTerritories() {
    return _territories.size();
  }

  @Override
  public void addTerritory(Territory territory) {
    _territories.add(territory);
  }

  @Override
  public void removeTerritory(Territory territory) {
    _territories.remove(territory);
  }

  @Override
  public void addArmiesToPlace(int armiesAdded) {
    _armiesToPlace += armiesAdded;
  }

  @Override
  public void placeArmies() {
    Territory placedAt = null;
    if (getArmiesToPlace() > 0) {
      Iterator<Territory> territoryIter = getOwnedTerritories().iterator();
      while (territoryIter.hasNext()) {
        placedAt = territoryIter.next();
        if (placedAt.getAttackTargets().size() > 0) {
          placedAt.addArmies(getArmiesToPlace());
          break;
        }
      }
      if (placedAt == null) {
        placedAt = getOwnedTerritories().get(0);
        placedAt.addArmies(getArmiesToPlace());
      }
      LOGGER.info(_name + " placed " + getArmiesToPlace()
        + " armies on " + placedAt.getName());
      removeArmiesToPlace(getArmiesToPlace());
    }
  }

  @Override
  public void placeOneArmy() {
    // If the MockPlayer has at least 1 army
    if (getArmiesToPlace() >= 1) {
      // Place it on the first territory in the list
      getOwnedTerritories().get(0).addArmies(1);
      removeArmiesToPlace(1);
      LOGGER.info(_name + " placed one army on "
        + getOwnedTerritories().get(0).getName());
    }
  }

  @Override
  public List<Territory> getOwnedTerritories() {
    return _territories;
  }

  @Override
  public String getName() {
    return _name;
  }

  @Override
  public Territory chooseTarget(Territory[] territories) {
    LOGGER.info(_name + " chose target: " + territories[0].getName());
    return territories[0];
  }

  @Override
  public Territory chooseAttacker(List<Territory> attackPoints) {
    LOGGER.info(_name + " chose attacking territory: "
      + attackPoints.get(0).getName());
    return attackPoints.get(0);
  }

  @Override
  public int chooseRoll(boolean attacking,
                        int min,
                        int max,
                        Territory attacker,
                        Territory defender) {
    LOGGER.info(_name + (attacking ? " attacking" : " defending")
      + " rolls " + max + " dice");
    return max;
  }

  @Override
  public int takeOver(int min, int max) {
    LOGGER.info(_name + " taking over moving " + max + " armies. Has " + getTotalOwnedTerritories() + " territories.");
    return max;
  }

  @Override
  public boolean wantToAttack() {
    LOGGER.info(_name + " wants to attack");
    return true;
  }

  @Override
  public Territory selectFortifyTarget(List<Territory> fortifiableTerritories) {
    LOGGER.info(_name + " is fortifying "
      + fortifiableTerritories.get(0).getName());
    return fortifiableTerritories.get(0);
  }

  @Override
  public Territory selectFortifyingTerritory(List<Territory> possibleFortifiers) {
    LOGGER.info(_name + " is sending armies from "
      + possibleFortifiers.get(0).getName());
    return possibleFortifiers.get(0);
  }

  @Override
  public int fortify(int min, int max) {
    LOGGER.info(_name + " is sending " + max + " armies");
    return max;
  }
}
