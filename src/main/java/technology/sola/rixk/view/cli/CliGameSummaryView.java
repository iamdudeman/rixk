package technology.sola.rixk.view.cli;

import technology.sola.rixk.RixkUtils;
import technology.sola.rixk.controller.GameSummaryController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.GameSummaryView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * CliGameSummaryView GameSummaryView meant for the command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliGameSummaryView extends CliAbstractView implements GameSummaryView {
  private static final Logger LOGGER = Logger.getLogger(CliGameSummaryView.class.getName());

  private GameSummaryController _gameSummaryController = null;

  /**
   * Builds this CliGameSummaryView object.
   *
   * @param gameState    The game state
   * @param inputStream  Where the input comes from
   * @param outputStream Where the output goes
   */
  public CliGameSummaryView(GameState gameState, InputStream inputStream, OutputStream outputStream) {
    super(gameState, inputStream, outputStream);
  }

  @Override
  public void startView() {
    LOGGER.info("Enter");
    LOGGER.info("Winning player: " + gameState.getActingPlayer().getName());

    LOGGER.warning("No stats are calculated or shown!!!");
    getCliOutput().write(gameState.getActingPlayer().getName() + " wins!\n");

    LOGGER.info("Exit");
  }

  @Override
  public void addGameSummaryController(GameSummaryController controller) {
    _gameSummaryController = controller;
  }
}
