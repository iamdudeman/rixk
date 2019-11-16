package technology.sola.rixk.view.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * CliOutput This class handles basic output to an OutputStream as UTF-8 encoded
 * characters.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliOutput {
  private final OutputStream outputStream;

  /**
   * Builds this CliOutput object.
   *
   * @param outputStream Typically System.out or FileOutputStream
   */
  public CliOutput(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * Writes a String output to the OutputStream.
   *
   * @param output The contents that should be written
   */
  public void write(String output) {
    // Try to write out the output as UTF-8
    try {
      outputStream.write(output.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

