package technology.sola.rixk.model;

import java.util.*;

/**
 * RixkMap This class represents a map in the game of Rixk. A RixkMap has list
 * of continents.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class RixkMap {
  private static final int MAX_PLAYERS = 6;

  private final int[] startingArmies;
  private final List<Continent> continents;
  private final Map<String, Territory> nameToTerritoryMap;

  private int totalTerritoryCount = 0;

  /**
   * Builds this RixkMap object.
   */
  public RixkMap() {
    continents = new LinkedList<>();
    startingArmies = new int[MAX_PLAYERS + 1];
    nameToTerritoryMap = new HashMap<>();
  }

  /**
   * Returns the starting armies per Player when playerCount players are
   * player Rixk.
   *
   * @param playerCount The number of Players playing
   * @return The starting armies per Player
   */
  public int getStartingArmiesPerPlayer(int playerCount) {
    return startingArmies[playerCount];
  }

  /**
   * Sets the starting armies per Player for playerCount to startingArmies.
   *
   * @param playerCount    The number of Players that will be playing
   * @param startingArmies The number of armies each Player receives to start
   */
  public void setStartingArmiesPerPlayer(int playerCount, int startingArmies) {
    this.startingArmies[playerCount] = startingArmies;
  }

  /**
   * Gets an Iterator for the Continent objects of this RixkMap.
   *
   * @return The Continent Iterator
   */
  public Iterator<Continent> getContinentIterator() {
    return continents.iterator();
  }

  /**
   * Adds a Continent to this RixkMap. This method increases adds to the total
   * territory count after adding the Continent to the RixkMap.
   *
   * @param continent The Continent to add
   */
  public void addContinent(Continent continent) {
    continents.add(continent);
    totalTerritoryCount += continent.getTerritoryCount();
  }

  /**
   * Getter for the total territory count in this RixkMap.
   *
   * @return The total territory count
   */
  public int getTotalTerritoryCount() {
    return totalTerritoryCount;
  }

  /**
   * Adds a name to Territory mapping.
   *
   * @param name      The name of the Territory
   * @param territory The Territory
   */
  public void addNameToTerritoryMapping(String name, Territory territory) {
    nameToTerritoryMap.put(name, territory);
  }

  /**
   * Retrieves a Territory using its name.
   *
   * @param name The name of the Territory
   * @return The Territory
   */
  public Territory getTerritoryByName(String name) {
    return nameToTerritoryMap.get(name);
  }

  /**
   * Return a Collection of all of the Territories inside of this RixkMap
   *
   * @return The Collection of Territory objects
   */
  public Collection<Territory> getAllTerritories() {
    return nameToTerritoryMap.values();
  }
}
