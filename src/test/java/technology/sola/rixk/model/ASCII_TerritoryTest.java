package technology.sola.rixk.model;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
class ASCII_TerritoryTest {


  @Test
  public void testToString() {

    String[][] temp = {

      {".", ".", "."},
      {".", ".", "."}
    };

    ASCII_Territory tt = new ASCII_Territory();
    tt.set_territoryMatrix(temp);
    tt.set_height(2);
    tt.set_width(3);

    toConsole(tt.toString());
  }

  @Test
  public void testdropInString() {

    String[][] temp = {

      {".", ".", ".", ".", "."},
      {".", ".", ".", ".", "."},
      {".", ".", ".", ".", "."}
    };

    ASCII_Territory tt = new ASCII_Territory();
    tt.set_territoryMatrix(temp);
    tt.set_height(3);
    tt.set_width(5);
    tt.dropInString("123", 1, 0);

    toConsole(tt.toString());

  }

  @Test
  public void testFillTerritoryWithSpacingCharacter() {

    String[][] temp = {

      {"x", "x", "x", "x", "x"}, // fill matrix with something
      {"x", "x", "x", "x", "x"},
      {"x", "x", "x", "x", "x"}
    };

    ASCII_Territory tt = new ASCII_Territory();
    tt.set_territoryMatrix(temp);
    tt.set_height(3);
    tt.set_width(5);

    tt.fillTerritoryWithSpacingCharacter();

    toConsole(tt.toString());

  }

  @Test
  public void testAddOrientationLine() {

    String[][] temp = {

      {".", ".", ".", ".", "."},
      {".", ".", ".", ".", "."},
      {".", ".", ".", ".", "."}
    };

    ASCII_Territory tt = new ASCII_Territory();
    tt.set_territoryMatrix(temp);
    tt.set_height(3);
    tt.set_width(5);

    tt.set_orientation(1); // horizontal

    tt.manageOrientationLine("draw");

    tt.set_orientation(2); // vertical

    tt.manageOrientationLine("draw");

    toConsole(tt.toString());

    tt.manageOrientationLine("erase");

    toConsole(tt.toString());

  }

  @Test
  public void testTerritory() {

    String territoryName = "T1";
    int height = 4;
    int width = 6;
    int orientation = 1; // 1 horizontal, 2 vertical
    String playerName = "P2";
    int armySize = 123;
    int handleRow = 1;
    int handleCol = 1;


    ASCII_Territory tt = new ASCII_Territory(
      territoryName,
      height,
      width,
      orientation,
      handleRow,
      handleCol);

    toConsole(tt.toString());

  }

  /**
   * quick and single place to turn on/off when all tests have passed. Simply
   * place a // in front of the line println below.
   *
   * @param stringForConsole a string to be passed to the console
   */
  private void toConsole(String stringForConsole) {
    System.out.println(stringForConsole);
  }


}

