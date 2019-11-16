package technology.sola.rixk.controller;

import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.RixkMapView;

/**
 * RixkMapController This controller handles events that happen while the Rixk
 * Map is being shown.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class RixkMapController {
  private final GameState gameState;
  private final RixkMapView rixkMapView;

  /**
   * Builds this FortifyController object.
   *
   * @param gameState   The game state
   * @param rixkMapView The game summary view this controller operates on
   */
  public RixkMapController(GameState gameState, RixkMapView rixkMapView) {
    this.gameState = gameState;
    this.rixkMapView = rixkMapView;
  }
}
