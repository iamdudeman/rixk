package technology.sola.rixk.controller;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.model.Continent;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.model.Territory;
import technology.sola.rixk.model.player.Player;
import technology.sola.rixk.view.AttackView;
import technology.sola.rixk.view.GetArmiesView;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * GetArmiesController This Controller handles the events that happen during the
 * GetArmies phase of playing Rixk.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class GetArmiesController {
  private static final Logger LOGGER = Logger.getLogger(GetArmiesController.class.getName());

  /**
   * The GameState that gets modified during events.
   */
  private GameState _gameState = null;

  /**
   * The SetupView that triggers the events.
   */
  private GetArmiesView _getArmiesView = null;

  /**
   * Builds this SetupController object.
   *
   * @param model The GameState object for the game
   * @param view  The GetArmiesView this Controller operates on
   */
  public GetArmiesController(GameState model, GetArmiesView view) {
    _gameState = model;
    _getArmiesView = view;
  }

  /**
   * This event signals that the active Player should receive armies based on
   * their bonuses.
   */
  public void getPlayerArmiesEvent() {
    // Get the acting Player
    Player actingPlayer = _gameState.getActingPlayer();

    // Calculate the territory bonus for the acting Player
    int playerTerritories = actingPlayer.getTotalOwnedTerritories();

    // Territory bonus is owned territories divided by 3
    int territoryBonus = playerTerritories / 3;
    // Territory bonus needs to at least be 3
    if (territoryBonus < 3) {
      territoryBonus = 3;
    }

    // Calculate the continents owned bonus
    int continentBonus = 0;

    // Loop through continents
    Iterator<Continent> continentIter = _gameState.getRixkMap().getContinentIterator();
    while (continentIter.hasNext()) {
      Continent continent = continentIter.next();
      // Assume the Player gets this continent bonus and try to prove it
      // false
      boolean getsBonus = true;
      // Loop through territories
      Iterator<Territory> territoryIter = continent.getTerritoryIterator();
      while (territoryIter.hasNext()) {
        // If the Player does not own one of the territories they do not
        // get the bonus
        if (!actingPlayer.ownsTerritory(territoryIter.next())) {
          getsBonus = false;
        }
      }

      // If they get the bonus add to the continent bonus received
      if (getsBonus) {
        continentBonus += continent.getBonus();
      }
    }

    // Add the bonuses to the acting Player's armies to place
    actingPlayer.addArmiesToPlace(territoryBonus + continentBonus);
  }

  /**
   * This event triggers the acting Player to begin placing armies.
   */
  public void placeArmiesEvent() {
    // Tell the acting Player to place their armies
    _gameState.getActingPlayer().placeArmies();
    // Update play object
    _gameState.updatePlayObject(_gameState.getActingPlayer());
  }

  /**
   * This event signals the GameViewManager that the GetArmiesView will be
   * done soon and it queues up the AttackView that should follow.
   *
   * @param attackView The AttackView that comes next
   */
  public void startAttackEvent(AttackView attackView) {
    // Trigger a change of phase
    _gameState.nextPhase();

    GameViewManager.getInstance().setNextActiveView(attackView);
  }

}

