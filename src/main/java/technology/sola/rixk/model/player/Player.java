package technology.sola.rixk.model.player;

import technology.sola.rixk.model.Territory;

import java.util.List;

public interface Player {
  /**
   * Returns true if the Player owns the Territory.
   *
   * @param territory The Territory to be checked if owned
   * @return Whether or not it is owned by this Player
   */
  boolean ownsTerritory(Territory territory);

  /**
   * Add a Territory to this Player's world domination scheme.
   *
   * @param territory The Territory that now belongs to Player
   */
  void addTerritory(Territory territory);

  /**
   * Removes a Territory to from the Player's world domination scheme.
   *
   * @param territory The Territory that is no longer the Player's
   */
  void removeTerritory(Territory territory);

  /**
   * Returns the number of armies the Player has left to place.
   *
   * @return The armies left to place
   */
  int getArmiesToPlace();

  /**
   * Adds to the number of armies this Player can place.
   *
   * @param armiesAdded The amount of armies added
   */
  void addArmiesToPlace(int armiesAdded);

  /**
   * Reduces the number of armies this Player can place by however many they
   * placed.
   *
   * @param armiesPlaced The number of armies placed
   */
  void removeArmiesToPlace(int armiesPlaced);

  /**
   * Returns a List of the territories owned by this Player.
   *
   * @return The List of territories owned by this Player
   */
  List<Territory> getOwnedTerritories();

  /**
   * Returns the total number of territories owned by this Player.
   *
   * @return The total number of owned territories
   */
  int getTotalOwnedTerritories();

  /**
   * This method notifies the Player that it is time to place armies. The
   * Player can place up to but no more than "getArmiesToPlace()" armies
   * territories that the Player owns.
   */
  void placeArmies();

  /**
   * This method notifies the Player that it is time to place one army. This
   * is usually used during the setup phase of Rixk.
   */
  void placeOneArmy();

  /**
   * Returns the name that of this Player.
   *
   * @return The name of the Player
   */
  String getName();

  /**
   * Returns whether or not the Player wants to attack.
   *
   * @return Whether or not the Player want to attack
   */
  boolean wantToAttack();

  /**
   * The Player selects the Territory they want to attack from the possible
   * attack choices.
   *
   * @param territories The possible attack choices
   * @return The Territory they want to attack
   */
  Territory chooseTarget(Territory[] territories);

  /**
   * The Player selects the Territory the want to attack from given a list of
   * possible attack points.
   *
   * @param attackPoints The List of possible attack points
   * @return The Territory they want to attack from
   */
  Territory chooseAttacker(List<Territory> attackPoints);

  /**
   * The Player chooses their roll. They are notified of whether they are
   * attacking or not, the minimum and maximum amount of dice they can roll,
   * the attacking Territory and the defending Territory.
   *
   * @param attacking Whether they are attacking or not
   * @param min       The minimum dice roll they can pick
   * @param max       The maximum dice roll they can pick
   * @param attacker  The attacking Territory
   * @param defender  The defending Territory
   * @return The roll the Player chose
   */
  int chooseRoll(boolean attacking,
                        int min,
                        int max,
                        Territory attacker,
                        Territory defender);

  /**
   * When a Territory that was just attacked is taken over the Player chooses
   * to move between min and max troops to the Territory.
   *
   * @param min The minimum number of armies they have to move
   * @param max The maximum number of armies they can move
   * @return The number of armies they want to move
   */
  int takeOver(int min, int max);

  /**
   * The player chooses a territory to fortify from a list of options.
   *
   * @param fortifiableTerritories The list of options to fortify
   * @return The desired territory to fortify
   */
  Territory selectFortifyTarget(List<Territory> fortifiableTerritories);

  /**
   * The Player chooses a territory to fortify from.
   *
   * @param possibleFortifiers The list of possible fortifying territories
   * @return The territory to provide fortifying armies
   */
  Territory selectFortifyingTerritory(List<Territory> possibleFortifiers);

  /**
   * The player selects a number of armies to fortify ranging from min to max.
   *
   * @param min The minimum number of armies required to send
   * @param max The maximum number of armies possible to send
   * @return The player's choice of armies to send
   */
  int fortify(int min, int max);
}

