package technology.sola.rixk.view.cli;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CliInput This class handles basic user input validation from an InputStream
 * object. Each line is treated as an input value.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliInput {
  private final Scanner scanner;
  private final Pattern integerPattern;
  private final Pattern booleanPattern;

  /**
   * Builds this CliInput object.
   *
   * @param inputStream Typically System.in or FileInputStream
   */
  public CliInput(InputStream inputStream) {
    scanner = new Scanner(inputStream);
    integerPattern = Pattern.compile("([0-9]+)");
    booleanPattern = Pattern.compile("(t(rue)?|[1-9][0-9]*|y(es)?)");
  }

  /**
   * Get an Integer from the InputStream. Returns null if the input was not an
   * Integer.
   *
   * @return The Integer object or null
   */
  public Integer getInteger() {
    Integer inputInt = null;

    String input = scanner.nextLine();
    Matcher integerMatcher = integerPattern.matcher(input);

    if (integerMatcher.find()) {
      inputInt = Integer.parseInt(integerMatcher.group(1));
    }

    return inputInt;
  }

  /**
   * Get a String from the InputStream.
   *
   * @return The String object
   */
  public String getString() {
    return scanner.nextLine();
  }

  /**
   * Get a boolean from the InputStream. True values are case insensitive t,
   * tr, tru, true, y, ye, yes, or any integer except 0.
   *
   * @return The boolean
   */
  public boolean getBoolean() {
    String input = scanner.nextLine();
    Matcher booleanMatcher = booleanPattern.matcher(input.toLowerCase());

    return booleanMatcher.find();
  }
}
