package technology.sola.rixk.model.player;

import technology.sola.rixk.model.Territory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAI implements Player {
  private static int aiCount = 0;

  private String name;
  private List<Territory> ownedTerritories = new ArrayList<>();
  private int armiesToPlace = 0;

  public AbstractAI() {
    name = "Default" + aiCount++;
  }

  /**
   * This sets this AI player's name to a specified name followed by a unique
   * number.
   *
   * @param name The player's name
   */
  public void setName(String name) {
    this.name = name + aiCount;
  }

  @Override
  public final boolean ownsTerritory(Territory territory) {
    return ownedTerritories.contains(territory);
  }

  @Override
  public final void addTerritory(Territory territory) {
    ownedTerritories.add(territory);
  }

  @Override
  public final void removeTerritory(Territory territory) {
    ownedTerritories.remove(territory);
  }

  @Override
  public final int getArmiesToPlace() {
    return armiesToPlace;
  }

  @Override
  public final void addArmiesToPlace(int armiesAdded) {
    armiesToPlace += armiesAdded;
  }

  @Override
  public final void removeArmiesToPlace(int armiesPlaced) {
    armiesToPlace -= armiesPlaced;
  }

  @Override
  public final List<Territory> getOwnedTerritories() {
    return ownedTerritories;
  }

  @Override
  public final int getTotalOwnedTerritories() {
    return ownedTerritories.size();
  }

  /**
   * Use the method placeArmiesOnTerritories(Territory, int) when wanting to
   * place armies on a Territory.
   *
   * @see Player#placeArmies()
   */
  @Override
  public abstract void placeArmies();

  /**
   * Places an amount of armies on a territory if the Player owns the
   * Territory and has enough armies to do so.
   *
   * @param territory The Player owned Territory
   * @param armies    The armies desired to be placed
   */
  protected void placeArmiesOnTerritories(Territory territory, int armies) {
    if (ownsTerritory(territory) && getArmiesToPlace() >= armies) {
      territory.addArmies(armies);
      armiesToPlace -= armies;
    }
  }

  /**
   * Use the method placeOneArmyOnTerritory(Territory) to place an army on
   * territory.
   *
   * @see Player#placeOneArmy()
   */
  @Override
  public abstract void placeOneArmy();

  /**
   * Places one army on a territory if the Player owns the Territory and has
   * at least one army to place
   *
   * @param territory The territory to place one army on
   */
  protected void placeOneArmyOnTerritory(Territory territory) {
    placeArmiesOnTerritories(territory, 1);
  }

  @Override
  public final String getName() {
    return name;
  }
}
