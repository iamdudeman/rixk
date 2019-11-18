package technology.sola.rixk.view.cli;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.controller.AttackController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.AttackView;
import technology.sola.rixk.view.FortifyView;
import technology.sola.rixk.view.GameSummaryView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * CliAttackView AttackView meant for the command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliAttackView extends CliAbstractView implements AttackView {
  private static final Logger LOGGER = Logger.getLogger(CliAttackView.class.getName());

  private AttackController _attackController = null;

  /**
   * Builds this CliAttackView object.
   *
   * @param gameState    The state of the game
   * @param inputStream  Where the input comes from
   * @param outputStream Where the output goes to
   */
  public CliAttackView(GameState gameState,
                       InputStream inputStream,
                       OutputStream outputStream) {
    super(gameState, inputStream, outputStream);
  }

  @Override
  public void startView() {
    LOGGER.info("Enter");
    LOGGER.info("Acting player: " + gameState.getActingPlayer().getName());

    boolean gameOver = false;

    // Output that the attack phase has begun
    getCliOutput().write(
      gameState.getActingPlayer().getName() + "'s attack phase\n");

    // While we are in attack phase
    while (!gameOver && _attackController.shouldAttackStartEvent()) {
      // Call the choose target event
      _attackController.playerChooseTargetEvent();

      // Call the choose attacking territory event
      _attackController.playerChooseAttackingTerritoryEvent();

      // Call the battle event
      gameOver = _attackController.playerBattleEvent();
    }

    // Check if the game is over to see where we transition next
    if (gameOver) {
      // Finish the game
      finishGame();
    } else {
      // Finish attack phase
      finishAttack();
    }

    LOGGER.info("Exit");
  }

  @Override
  public void addAttackController(AttackController controller) {
    _attackController = controller;
  }

  private void finishAttack() {
    LOGGER.info("Enter");

    // Get the GameViewManager instance
    GameViewManager gvm = GameViewManager.getInstance();
    // Get the key for the FortifyView
    String fortifyViewKey = GameViewManager.FORTIFY_VIEW_KEY;
    // Get the next view from the GameViewManager
    FortifyView nextView = (FortifyView) gvm.getViewFromMap(fortifyViewKey);
    // Trigger the startGetArmiesEvent for the nextView
    _attackController.startFortifyEvent(nextView);

    LOGGER.info("Exit");
  }

  private void finishGame() {
    // Get the GameViewManager instance
    GameViewManager gvm = GameViewManager.getInstance();
    // Get the key for the GameSummaryView
    String gameSummaryViewKey = GameViewManager.GAME_SUMMARY_VIEW_KEY;
    // Get the next view from the GameViewManager
    GameSummaryView nextView = (GameSummaryView) gvm.getViewFromMap(gameSummaryViewKey);
    // Trigger the startGetArmiesEvent for the nextView
    _attackController.startGameSummaryEvent(nextView);
  }
}

