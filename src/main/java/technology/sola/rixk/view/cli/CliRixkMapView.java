package technology.sola.rixk.view.cli;

import technology.sola.rixk.RixkUtils;
import technology.sola.rixk.controller.RixkMapController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.RixkMapView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * CliRixkMapView RixkMapView meant for the command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliRixkMapView extends CliAbstractView implements RixkMapView {
  private static final Logger LOGGER = Logger.getLogger(CliRixkMapView.class.getName());

  private RixkMapController _rixkMapController = null;

  /**
   * Builds this CliRixkMapView object.
   *
   * @param gameState    The game state
   * @param inputStream  Where the input comes from
   * @param outputStream Where the output goes
   */
  public CliRixkMapView(GameState gameState,
                        InputStream inputStream,
                        OutputStream outputStream) {
    super(gameState, inputStream, outputStream);
  }

  @Override
  public void startView() {
    LOGGER.info("Enter");

    LOGGER.info("Exit");
    throw new RuntimeException("Not yet implemented");
  }

  @Override
  public void addRixkMapController(RixkMapController controller) {
    _rixkMapController = controller;
  }

}
