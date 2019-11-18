package technology.sola.rixk.view.cli;

// TODO remove debug stuff

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.controller.GetArmiesController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.model.player.Player;
import technology.sola.rixk.view.AttackView;
import technology.sola.rixk.view.GetArmiesView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * CliGetArmiesView GetArmiesView meant for the command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliGetArmiesView extends CliAbstractView implements GetArmiesView {
  private static final Logger LOGGER = Logger.getLogger(CliGetArmiesView.class.getName());

  private GetArmiesController _getArmiesController = null;

  /**
   * Builds this CliGetArmiesView object
   *
   * @param gameState    The Model
   * @param inputStream  Typically System.in or FileInputStream
   * @param outputStream Typically System.out or FileOutputStream
   */
  public CliGetArmiesView(GameState gameState, InputStream inputStream, OutputStream outputStream) {
    super(gameState, inputStream, outputStream);
  }

  @Override
  public void startView() {
    LOGGER.info("Enter");
    LOGGER.info("Acting player: " + gameState.getActingPlayer().getName());

    // Output that get army phase has begun
    getCliOutput().write(
      gameState.getActingPlayer().getName()
        + "'s get place armies phase\n");

    // Give the acting Player the armies they deserve
    getPlayerArmies();

    // Tell the Player to place armies that they have
    placeArmies();

    // Finish up this phase
    finishGetArmies();

    LOGGER.info("Exit");
  }

  @Override
  public void addGetArmiesController(GetArmiesController controller) {
    _getArmiesController = controller;
  }

  private void getPlayerArmies() {
    LOGGER.info("Enter");

    // Trigger the get armies event for the activePlayer
    _getArmiesController.getPlayerArmiesEvent();

    // Debug stuff
    {
      Player actingPlayer = gameState.getActingPlayer();
      System.out.println(actingPlayer.getName() + " can place "
        + actingPlayer.getArmiesToPlace());
    }
    // End Debug stuff

    LOGGER.info("Exit");
  }

  private void placeArmies() {
    LOGGER.info("Enter");

    // Trigger the place armies event
    _getArmiesController.placeArmiesEvent();

    // Debug stuff
    {
      Player actingPlayer = gameState.getActingPlayer();
      System.out.println(actingPlayer.getOwnedTerritories().get(0).toString());
      System.out.println(actingPlayer.getName() + " can place "
        + actingPlayer.getArmiesToPlace());
    }
    // End Debug stuff

    LOGGER.info("Exit");
  }

  private void finishGetArmies() {
    LOGGER.info("Enter");

    // Get the GameViewManager instance
    GameViewManager gvm = GameViewManager.getInstance();
    // Get the key for the GetArmiesView
    String attackViewKey = GameViewManager.ATTACK_VIEW_KEY;
    // Get the next view from the GameViewManager
    AttackView nextView = (AttackView) gvm.getViewFromMap(attackViewKey);
    // Trigger the startGetArmiesEvent for the nextView
    _getArmiesController.startAttackEvent(nextView);

    LOGGER.info("Exit");
  }
}

