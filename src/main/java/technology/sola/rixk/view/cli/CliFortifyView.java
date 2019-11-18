package technology.sola.rixk.view.cli;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.controller.FortifyController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.FortifyView;
import technology.sola.rixk.view.GetArmiesView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * CliFortifyView FortifyView meant for the command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliFortifyView extends CliAbstractView implements FortifyView {
  private static final Logger LOGGER = Logger.getLogger(CliFortifyView.class.getName());

  private FortifyController _fortifyController = null;

  /**
   * Builds this CliFortifyView object.
   *
   * @param gameState    The game state
   * @param inputStream  Where the input comes from
   * @param outputStream Where the output goes
   */
  public CliFortifyView(GameState gameState, InputStream inputStream, OutputStream outputStream) {
    super(gameState, inputStream, outputStream);
  }

  @Override
  public void startView() {
    LOGGER.info("Enter");
    LOGGER.info("Acting player: " + gameState.getActingPlayer().getName());

    // Write to output that the fortify phase has begun
    getCliOutput().write(
      gameState.getActingPlayer().getName() + " fortify phase\n");

    // Call the fortify territory event
    _fortifyController.fortifyTerritoryEvent();

    // Finish up the fortify
    finishFortify();

    LOGGER.info("Exit");
  }

  @Override
  public void addFortifyController(FortifyController controller) {
    _fortifyController = controller;
  }

  private void finishFortify() {
    LOGGER.info("Enter");

    // Get the GameViewManager instance
    GameViewManager gvm = GameViewManager.getInstance();
    // Get the key for the GetArmiesView
    String getArmiesKey = GameViewManager.GET_ARMIES_VIEW_KEY;
    // Get the next view from the GameViewManager
    GetArmiesView nextView = (GetArmiesView) gvm.getViewFromMap(getArmiesKey);
    // Trigger the startGetArmiesEvent for the nextView
    _fortifyController.startGetArmiesEvent(nextView);

    LOGGER.info("Exit");
  }
}

