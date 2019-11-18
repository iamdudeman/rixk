package technology.sola.rixk.controller;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.model.Territory;
import technology.sola.rixk.view.FortifyView;
import technology.sola.rixk.view.GetArmiesView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * FortifyController This controller handles events that happen during the
 * Fortify phase of Rixk.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class FortifyController
{
  private GameState _gameState = null;

  private FortifyView _fortifyView = null;

  private Territory _fortifyTarget = null;

  private Territory _fortifyingTerritory = null;

  /**
   * Builds this FortifyController object.
   *
   * @param gameState The game state
   * @param fortifyView The fortify view this controller operates on
   */
  public FortifyController( GameState gameState, FortifyView fortifyView )
  {
    _gameState = gameState;
    _fortifyView = fortifyView;
  }

  /**
   * This event tells the Player to fortify an owned territory.
   */
  public void fortifyTerritoryEvent()
  {
    // Make a list of possible fortifiable territories
    List<Territory> fortifiableTerritories = new ArrayList<Territory>();
    // Go through acting Player's territories
    Iterator<Territory> ownedIter = _gameState.getActingPlayer().getOwnedTerritories().iterator();
    while( ownedIter.hasNext() )
    {
      // Check if the current territory has adjacent territories that the
      // player owns that also have more than one army
      Territory territory = ownedIter.next();
      Iterator<Territory> adjacentIter = territory.getAdjacentTerritoryIterator();
      while( adjacentIter.hasNext() )
      {
        Territory adjacent = adjacentIter.next();
        if( adjacent.getArmies() > 1
          && _gameState.getActingPlayer().ownsTerritory( adjacent ) )
        {
          // If it does than add it as a possibly fortifiable
          // territory
          fortifiableTerritories.add( territory );
        }
      }
    }

    // If there are not fortifiable territories then return early
    if( fortifiableTerritories.size() == 0 )
    {
      return;
    }

    // Get the fortify target from the acting Player
    _fortifyTarget = _gameState.getActingPlayer().selectFortifyTarget(
      fortifiableTerritories );

    // Make a list of possible fortifiers
    List<Territory> possibleFortifiers = new ArrayList<Territory>();
    // Add all the possible fortifiers for this target
    addPossibleFortifiers( possibleFortifiers, _fortifyTarget );

    // Get the fortifier choice from the acting Player
    _fortifyingTerritory = _gameState.getActingPlayer().selectFortifyingTerritory(
      possibleFortifiers );

    // Give the Player a choice of how many armies they can use to fortify
    int min = 0;
    int max = _fortifyingTerritory.getArmies() - 1;
    int fortifyingArmies = _gameState.getActingPlayer().fortify( min, max );

    // Move the armies
    _fortifyTarget.addArmies( fortifyingArmies );
    _fortifyingTerritory.removeArmies( fortifyingArmies );

    // Update play object
    _gameState.updatePlayObject( _gameState.getActingPlayer() );
  }

  private void addPossibleFortifiers( List<Territory> possibleFortifiers,
                                      Territory potentialTerritory )
  {
    // Loop through all of the adjacent territories to this potential
    // fortify territory
    Iterator<Territory> fortifierIter = potentialTerritory.getAdjacentTerritoryIterator();
    while( fortifierIter.hasNext() )
    {
      // If the territory has at more than one army and is owned by the
      // acting Player
      Territory territory = fortifierIter.next();
      if( territory.getArmies() > 1
        && _gameState.getActingPlayer().ownsTerritory( territory ) )
      {
        // And it has not already been added
        if( !possibleFortifiers.contains( territory ) && territory != _fortifyTarget )
        {
          // Add this territory as a possible fortifier
          possibleFortifiers.add( territory );
          // See if this territory has others around it that can
          // fortify as well
          addPossibleFortifiers( possibleFortifiers, territory );
        }
      }
    }
  }

  /**
   * Readies the game for the get armies view that will be coming up next.
   *
   * @param getArmiesView The next active view
   */
  public void startGetArmiesEvent( GetArmiesView getArmiesView )
  {
    // Trigger a change of phase
    _gameState.nextPhase();

    GameViewManager.getInstance().setNextActiveView( getArmiesView );
  }
}

