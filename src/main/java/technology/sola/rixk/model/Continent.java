package technology.sola.rixk.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Continent This class represents a Continent in Rixk. A Continent has a list
 * of Territories, a bonus for when the Territories are all owned, and a unique
 * name.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class Continent {
  private final List<Territory> territories;
  private final int bonus;
  private final String name;

  /**
   * Builds this Continent object.
   *
   * @param name  The unique name of this Continent
   * @param bonus The bonus when all Territories are owned by one Player
   */
  public Continent(String name, int bonus) {
    this.name = name;
    this.bonus = bonus;

    territories = new LinkedList<>();
  }

  /**
   * Getter for this Continent object's name.
   *
   * @return The name of the Continent
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the bonus.
   *
   * @return The bonus
   */
  public int getBonus() {
    return bonus;
  }

  /**
   * Gets an Iterator for the Territory objects inside of this Continent.
   *
   * @return The Territory Iterator
   */
  public Iterator<Territory> getTerritoryIterator() {
    return territories.iterator();
  }

  /**
   * Adds a Territory to this Continent.
   *
   * @param territory The Territory to add
   */
  public void addTerritory(Territory territory) {
    territories.add(territory);
  }

  /**
   * Returns the total number of territories in this Continent.
   *
   * @return The Territory count
   */
  public int getTerritoryCount() {
    return territories.size();
  }
}

