package technology.sola.rixk.model;

import technology.sola.rixk.model.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Territory This class represents a Territory in the game of Rixk. It is owned
 * by a Player, is identified by its unique name, and has adjacent territories
 * to it.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class Territory {
  private final String name;
  private final List<Territory> adjacentTerritories;

  private Player owningPlayer = null;
  private int armies = 0;


  /**
   * Builds this Territory object.
   *
   * @param name The unique name of this Territory
   */
  public Territory(String name) {
    this.name = name;

    adjacentTerritories = new LinkedList<>();
  }

  /**
   * Getter for the name of this Territory.
   *
   * @return The name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the owning Player of this Territory.
   *
   * @return The owning Player
   */
  public Player getOwningPlayer() {
    return owningPlayer;
  }

  /**
   * Setter for the new owning Player of this Territory. This removes this
   * territory from the old owner and adds it to the new owner.
   *
   * @param player The new owner
   */
  public void setOwningPlayer(Player player) {
    // If there is an owning Player
    if (owningPlayer != null) {
      // Remove it from their possession
      owningPlayer.removeTerritory(this);
    }
    // Change the owner and add it to the new owner
    owningPlayer = player;
    owningPlayer.addTerritory(this);
  }

  /**
   * Getter for the number of armies on this Territory.
   *
   * @return The army count
   */
  public int getArmies() {
    return armies;
  }

  /**
   * Setter for the number of armies on this Territory.
   *
   * @param armyCount The new number of armies on this Territory
   */
  public void setArmies(int armyCount) {
    armies = armyCount;
  }

  /**
   * Adds armies to the Territory.
   *
   * @param armiesToAdd The number of armies to be added
   */
  public void addArmies(int armiesToAdd) {
    armies += armiesToAdd;
  }

  /**
   * Remove armies from the Territory.
   *
   * @param armiesToRemove The number of armies to remove
   */
  public void removeArmies(int armiesToRemove) {
    armies -= armiesToRemove;
  }

  /**
   * Gets the Territory Iterator for the adjacent territories.
   *
   * @return The Territory Iterator
   */
  public Iterator<Territory> getAdjacentTerritoryIterator() {
    return adjacentTerritories.iterator();
  }

  /**
   * Adds an adjacent Territory to this Territory.
   *
   * @param adjacentTerritory The adjacent Territory
   */
  public void addAdjacentTerritory(Territory adjacentTerritory) {
    adjacentTerritories.add(adjacentTerritory);
  }

  /**
   * Returns a list of possible attack targets from this territory. Possible
   * attack targets include all adjacent territories that are not owned by the
   * player that owns this territory.
   *
   * @return The List of possible attack targets
   */
  public List<Territory> getAttackTargets() {
    List<Territory> attackTargets = new ArrayList<>();

    // Iterate through adjacent territories
    Iterator<Territory> adjacentIter = getAdjacentTerritoryIterator();
    while (adjacentIter.hasNext()) {
      // If the territories are not owned by the same Player then add as
      // an attack target
      Territory adjacent = adjacentIter.next();
      if (!adjacent.getOwningPlayer().equals(getOwningPlayer())) {
        attackTargets.add(adjacent);
      }
    }

    return attackTargets;
  }

  /**
   * Returns a list of possible fortify targets from this territory. Possible
   * fortify targets include all adjacent territories that are owned by the
   * player that owns this territory.
   *
   * @return The List of possible fortify targets
   */
  public List<Territory> getFortifyTargets() {
    List<Territory> fortifyTargets = new ArrayList<>();

    // Iterate through adjacent territories
    Iterator<Territory> adjacentIter = getAdjacentTerritoryIterator();
    while (adjacentIter.hasNext()) {
      // If the territories are owned by the same Player then add as
      // a fortify target
      Territory adjacent = adjacentIter.next();
      if (adjacent.getOwningPlayer().equals(getOwningPlayer())) {
        fortifyTargets.add(adjacent);
      }
    }

    return fortifyTargets;
  }

  @Override
  public String toString() {
    return String.format("%-22s %-16s %3d", getName(),
      getOwningPlayer().getName(), getArmies());
  }
}
