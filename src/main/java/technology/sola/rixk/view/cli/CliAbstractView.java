package technology.sola.rixk.view.cli;

import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.RixkView;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * CliAbstractView Contains basic functionality for a View meant for the command
 * line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public abstract class CliAbstractView implements RixkView {
  private CliInput input = null;
  private CliOutput output = null;

  /**
   * The Model for this View.
   */
  protected GameState gameState = null;

  /**
   * Builds this CliAbstractView object.
   *
   * @param gameState    The game state of the game
   * @param inputStream  The desired InputStream
   * @param outputStream The desired OutputStream
   */
  public CliAbstractView(GameState gameState, InputStream inputStream, OutputStream outputStream) {
    this.gameState = gameState;
    input = new CliInput(inputStream);
    output = new CliOutput(outputStream);
  }

  /**
   * Getter for CliInput.
   *
   * @return The CliInput object
   */
  public CliInput getCliInput() {
    return input;
  }

  /**
   * Getter for CliOutput.
   *
   * @return The CliOutput object
   */
  public CliOutput getCliOutput() {
    return output;
  }

  @Override
  public void displayNotification(String message) {
    output.write(message);
  }
}

