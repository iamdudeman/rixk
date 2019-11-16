package technology.sola.rixk.controller;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.RixkUtils;
import technology.sola.rixk.model.ASCIIPlayObject;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.model.RixkMap;
import technology.sola.rixk.model.Territory;
import technology.sola.rixk.model.player.Player;
import technology.sola.rixk.view.GetArmiesView;
import technology.sola.rixk.view.SetupView;

import java.io.IOException;
import java.util.*;

/**
 * SetupController This controller handles events that happen during the setup
 * phase of Rixk.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class SetupController {
  /**
   * The GameState that gets modified during events.
   */
  private final GameState gameState;

  /**
   * The SetupView that triggers the events.
   */
  private final SetupView setupView;

  /**
   * Builds this SetupController object.
   *
   * @param model The GameState object for the game
   * @param view  The SetupView this Controller operates on
   */
  public SetupController(GameState model, SetupView view) {
    gameState = model;
    setupView = view;
  }

  /**
   * Modifies the current RixkMap being played on in the game state.
   *
   * @param filename The filename for the desired RixkMap
   * @return Whether or not the RixkMap was successfully selected
   */
  public boolean selectMapEvent(String filename) {
    // Exit early
    if (filename == null) {
      return false;
    }

    boolean success = false;

    if (RixkUtils.isASCIIRixkMap(filename)) {
      ASCIIPlayObject playObject = RixkUtils.loadASCIIPlayObject(filename);
      gameState.setPlayObject(playObject);
      RixkUtils.savePlayObjectToFile(gameState.getPlayObject());
    }

    // Load the RixkMap from the file system
    RixkMap mapSelected = RixkUtils.loadRixkMap(filename);

    // If there was a map properly selected
    if (mapSelected != null) {
      // Set the RixkMap in the game state
      gameState.setRixkMap(mapSelected);
      success = true;
    }

    return success;
  }

  public boolean addPlayerEvent(String classname) {
    boolean success = false;

    Player player = null;
    // If the player is human load it from the classname
    player = RixkUtils.loadAIFromClassname(classname);

    // If the player is null then it was not loaded properly
    if (player != null) {
      success = true;
      gameState.addPlayer(player);
    }

    return success;
  }

  public boolean addPlayerEvent(String filename, String classname) {
    boolean success = false;

    Player player = null;
    try {
      player = RixkUtils.insantiatePlayerFromJar(filename, classname);
    } catch (InstantiationException | IllegalAccessException
      | ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }

    // If the player is null then it was not loaded properly
    if (player != null) {
      success = true;
      gameState.addPlayer(player);
    }

    return success;
  }

  /**
   * Randomly assigns Territories to Players (using up one of their armies to
   * place) until all territories are owned.
   */
  public void assignPlayerTerritoriesEvent() {
    int playerIndex = 0;

    // Randomize the Player order
    gameState.shufflePlayerOrder();

    // Get Player order
    List<Player> playerOrder = gameState.getPlayerOrder();

    // Get the Player count
    int playerCount = playerOrder.size();
    // Get the starting armies count
    int startingArmiesCount = gameState.getRixkMap().getStartingArmiesPerPlayer(
      playerCount);

    // Iterate through Players and give them their starting armies
    Iterator<Player> playerIter = playerOrder.iterator();
    while (playerIter.hasNext()) {
      playerIter.next().addArmiesToPlace(startingArmiesCount);
    }

    // Get the Collection of territories and make it into a list
    Collection<Territory> territories = gameState.getRixkMap().getAllTerritories();
    List<Territory> territoryList = new ArrayList<>(territories);

    // Shuffle the List of territories
    Collections.shuffle(territoryList, RixkUtils.getRng());

    // Get an iterator for the territories
    Iterator<Territory> territoryIter = territoryList.iterator();
    while (territoryIter.hasNext()) {
      // Get the current Territory
      Territory currentTerritory = territoryIter.next();
      // Get the current Player
      Player currentPlayer = playerOrder.get(playerIndex);

      // Set the owner of the current Territory to the current Player
      currentTerritory.setOwningPlayer(currentPlayer);
      // Add one army to Territory
      currentTerritory.addArmies(1);
      // Remove one of current Player's armies to place
      currentPlayer.removeArmiesToPlace(1);

      // Increment the index of the next Player to get a territory
      playerIndex = (playerIndex + 1) % playerOrder.size();
    }
  }

  /**
   * This event signals a Player to place one army if the Player has at least
   * 1 army to place. The Player needs to be passed in since there is no
   * active Player until the end of the setup phase.
   *
   * @param player The Player to place an army
   */
  public void placeArmyEvent(Player player) {
    // Check if the Player has armies to place
    if (player.getArmiesToPlace() > 0) {
      // Allow the Player to place one army
      player.placeOneArmy();
      // Update the play object
      gameState.updatePlayObject(player);
    }
  }

  /**
   * This event signals the GameViewManager that the SetupView will be done
   * soon and it queues up the GetArmiesView that should follow.
   *
   * @param getArmiesView The GetArmiesView that should follow the SetupView
   */
  public void startGetArmiesEvent(GetArmiesView getArmiesView) {
    // Shuffle the Player playing order
    gameState.shufflePlayerOrder();

    // Trigger a change of phase
    gameState.nextPhase();

    // Set the next active View in the GameViewManager instance
    GameViewManager.getInstance().setNextActiveView(getArmiesView);
  }
}
