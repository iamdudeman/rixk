package technology.sola.rixk.controller;

import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.GameSummaryView;

/**
 * GameSummaryController This controller handles events that happen during the
 * game summary phase of Rixk.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class GameSummaryController {
  private GameState _gameState = null;

  private GameSummaryView _gameSummaryView = null;

  /**
   * Builds this FortifyController object.
   *
   * @param gameState       The game state
   * @param gameSummaryView The game summary view this controller operates on
   */
  public GameSummaryController(GameState gameState, GameSummaryView gameSummaryView) {
    _gameState = gameState;
    _gameSummaryView = gameSummaryView;
  }
}
