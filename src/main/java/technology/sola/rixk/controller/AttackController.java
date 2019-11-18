package technology.sola.rixk.controller;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.model.BattleResult;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.model.Territory;
import technology.sola.rixk.view.AttackView;
import technology.sola.rixk.view.FortifyView;
import technology.sola.rixk.view.GameSummaryView;

import java.util.*;

/**
 * AttackController This controller handles events that happen during the attack
 * phase of Rixk.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class AttackController {
  private GameState _gameState = null;

  private AttackView _attackView = null;

  private Territory _attackTarget = null;

  private Territory _attackingTerritory = null;

  /**
   * Builds this AttackController object.
   *
   * @param gameState The GameState
   * @param view      The view this controller operates on
   */
  public AttackController(GameState gameState, AttackView view) {
    _gameState = gameState;
    _attackView = view;
  }

  /**
   * This event returns whether or not the attack phase should start. If the
   * Player has no options of attack or does not want to attack this will
   * return false.
   *
   * @return Whether or not attack phase should start
   */
  public boolean shouldAttackStartEvent() {
    boolean startAttack = false;

    // If the player does not want to attack then do no start attack
    if (!_gameState.getActingPlayer().wantToAttack()) {
      startAttack = false;
    } else {
      // Figure out if there is a place they can attack from
      // Go through their owned territories
      Iterator<Territory> ownedIter = _gameState.getActingPlayer().getOwnedTerritories().iterator();
      while (ownedIter.hasNext()) {
        // If this territory has more than one army
        Territory current = ownedIter.next();
        if (current.getArmies() > 1) {
          // And it has at least one adjacent
          Iterator<Territory> adjacentIter = current.getAdjacentTerritoryIterator();
          while (adjacentIter.hasNext()) {
            // And not owned territory
            Territory adjacent = adjacentIter.next();
            if (!_gameState.getActingPlayer().ownsTerritory(
              adjacent)) {
              // Then this player can attack
              startAttack = true;
            }
          }
        }
      }
    }

    return startAttack;
  }

  /**
   * This event tells the player to select an attack target from their
   * eligible choices.
   */
  public void playerChooseTargetEvent() {
    // Create a set of possible attack targets
    Set<Territory> targets = new HashSet<Territory>();
    // Go through Player's owned territories
    Iterator<Territory> playerTerritoryIter = _gameState.getActingPlayer().getOwnedTerritories().iterator();
    while (playerTerritoryIter.hasNext()) {
      // If the territory has more than one army
      Territory territory = playerTerritoryIter.next();
      if (territory.getArmies() > 1) {
        // Add all of the territory's attack targets to the possible
        // targets
        targets.addAll(territory.getAttackTargets());
      }
    }

    // Set the attack target to what the player chose
    _attackTarget = _gameState.getActingPlayer().chooseTarget(
      targets.toArray(new Territory[targets.size()]));
  }

  /**
   * This event tells the player to select an attacking territory from their
   * possible choices after their target selection.
   */
  public void playerChooseAttackingTerritoryEvent() {
    // Create a list of possible attack points
    List<Territory> attackPoints = new ArrayList<Territory>();
    // Iterate through places our attack target can attack
    Iterator<Territory> possibleAttackPointsIter = _attackTarget.getAttackTargets().iterator();
    while (possibleAttackPointsIter.hasNext()) {
      // If the place the attack target can attack is one that we own
      // And it has more than one army on it
      Territory territory = possibleAttackPointsIter.next();
      if (territory.getArmies() > 1
        && _gameState.getActingPlayer().ownsTerritory(territory)) {
        // Then add it as a possible attack point for the target
        attackPoints.add(territory);
      }
    }

    // Set the attacking territory as the player's choice
    _attackingTerritory = _gameState.getActingPlayer().chooseAttacker(
      attackPoints);
  }

  /**
   * This event will manage the flow of battle and ask the player to roll dice
   * when necessary. It will also tell a Player who conquered a territory to
   * move armies on to the conquered territory.
   *
   * @return Whether the game is over or not
   */
  public boolean playerBattleEvent() {
    boolean gameOver = false;

    // Calculate the minimum and maximum dice to be thrown for attacker
    int min = 1;
    // We need at least one more than the number of armies on the territory
    // Can't attack with one army
    int max = _attackingTerritory.getArmies() - 1;
    // Can only attack with 3 though
    if (max > 3) {
      max = 3;
    }
    // Get the attack roll from the acting player
    int attackRoll = _gameState.getActingPlayer().chooseRoll(true, min,
      max, _attackingTerritory, _attackTarget);

    // Calculate the minimum and maximum dice to be thrown for defender
    min = 1;
    // We can defend with one army
    max = _attackTarget.getArmies();
    if (max > 2) {
      max = 2;
    }
    int defendRoll = _attackTarget.getOwningPlayer().chooseRoll(false,
      min, max, _attackingTerritory, _attackTarget);

    // Get the result of the battle and remove armies where appropriate
    BattleResult result = _gameState.rollDice(attackRoll, defendRoll);
    _attackingTerritory.removeArmies(result.getAttackingLoss());
    _attackTarget.removeArmies(result.getDefendingLoss());

    // Display notifications of battle
    _attackView.displayNotification(_attackingTerritory.getOwningPlayer().getName()
      + "'s "
      + _attackingTerritory.getName()
      + " lost "
      + result.getAttackingLoss() + " armies\n");
    _attackView.displayNotification(_attackTarget.getOwningPlayer().getName()
      + "'s "
      + _attackTarget.getName()
      + " lost "
      + result.getDefendingLoss() + " armies\n");
    _attackView.displayNotification("Attacker has " + _attackingTerritory.getArmies() + " remaining\n");
    _attackView.displayNotification("Defender has " + _attackTarget.getArmies() + " remaining\n");

    // If the attack target was defeated
    if (_attackTarget.getArmies() <= 0) {
      // Display notification of take over
      _attackView.displayNotification(_attackTarget.getName()
        + " was taken over by "
        + _attackingTerritory.getOwningPlayer().getName() + "\n");
      // Change ownership of the target territory
      _attackTarget.setOwningPlayer(_gameState.getActingPlayer());
      // Check if the acting player has all of the territories now
      if (_gameState.getActingPlayer().getTotalOwnedTerritories() >= _gameState.getRixkMap().getTotalTerritoryCount()) {
        // Finish up the game
        gameOver = true;
      } else {
        // Calculate minimum and maximum possible troops to move
        // Minimum is what we attacked with
        min = attackRoll;
        // Maximum is leaving one behind on territory
        max = _attackingTerritory.getArmies() - 1;

        // Get the amount moved from the acting player
        int movedTroops = _gameState.getActingPlayer().takeOver(min,
          max);
        // Change army values
        _attackTarget.addArmies(movedTroops);
        _attackingTerritory.removeArmies(movedTroops);
        gameOver = false;
      }
    }

    // Update play object for both attacking and defending players
    _gameState.updatePlayObject(_attackingTerritory.getOwningPlayer());
    _gameState.updatePlayObject(_attackTarget.getOwningPlayer());

    return gameOver;
  }

  /**
   * This event readies the Fortify phase.
   *
   * @param fortifyView The Fortify phase coming up next
   */
  public void startFortifyEvent(FortifyView fortifyView) {
    // Trigger a change of phase
    _gameState.nextPhase();

    // Set the next active View in the GameViewManager instance
    GameViewManager.getInstance().setNextActiveView(fortifyView);
  }

  /**
   * This event readies the GameSummary phase.
   *
   * @param gameSummaryView The GameSummaryPhase to come next
   */
  public void startGameSummaryEvent(GameSummaryView gameSummaryView) {
    // Trigger a change of phase
    _gameState.nextPhase();

    GameViewManager.getInstance().setNextActiveView(gameSummaryView);
  }
}
