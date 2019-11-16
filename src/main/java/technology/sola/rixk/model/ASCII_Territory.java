package technology.sola.rixk.model;

/**
 * Holds information about territory
 * <pre>
 0,0 is first letter of name of territory
 1,0 is first letter of player name that controls territory
 2,0 is first digit of number of armies currently on territory.
 Example:
 T1....
 P2....
 123...
 ~~~~~~

 Note that a territory can only be placed as north and as west as 1,1 on a continent.
 *</pre>
 *
 * @author Brint
 *
 */
/**
 * @author Brint
 *
 */
public class ASCII_Territory {

  private final int    HORIZONTAL_ORIENTED = 1;
  private final int	 VERTICAL_ORIENTED 	 = 2;
  private final String ORIENTATION_CHARACTER = "|";
  private final String PLACEHOLDER_CHARACTER = " ";

  private String[][] 	_territoryMatrix;
  private int 		_height;
  private int 		_width;

  private String 		_territoryName ="n/a";
  private String 		_playerNameWhoControls = "n/a";
  private int 		_numberOfArmies;
  private int			_orientation;

  private int			_handleRow; // where located in continent row
  private int			_handleCol; // where located in continent col

  public ASCII_Territory() { }

  /**
   * Constructor to build a territory
   *
   * @param territoryName
   *            A string name, preferably short.
   * @param height
   *            int number of rows in height.
   * @param width
   *            int number of columns in width
   * @param orientation
   *            int 1 horizontal line at bottom, 2 vertical line at right
   */
  public ASCII_Territory(String territoryName,
                         int height,
                         int width,
                         int orientation,
                         int handleRow,
                         int handleCol) {

    _territoryName 	= territoryName;
    _height = height;
    _width 	= width;
    _orientation = orientation;
    _handleRow = handleRow;
    _handleCol = handleCol;

    go();
  }

  /**
   * Main algorithm is here.
   */
  private void go() {

    _territoryMatrix = new String[_height][_width];

    fillTerritoryWithSpacingCharacter();
    manageOrientationLine("draw"); // fill orientation row at bottom or on right.
    placeTerritoryInfo();
  }

  /**
   * Places information about territory name, which player controls it,
   * and how many armies are on it (details). First line is territories,
   * second player control, third is no. of armies.
   */
  public void placeTerritoryInfo() {

    dropInString(_territoryName, 0, 0);
    dropInString(_playerNameWhoControls, 1, 0);
    dropInString(Integer.toString(_numberOfArmies), 2, 0);

  }

  /**
   * Show what the territory looks like.
   */
  public String toString() {

    String str = "";

    for (int r = 0 ; r < _height; r++){
      for (int c = 0; c < _width; c++) {
        str += _territoryMatrix[r][c];
      }
      str += "\r\n";
    }
    return str;
  }


  /**
   * Simply fill the matrix with the stipulated spacing character.
   */
  public void fillTerritoryWithSpacingCharacter() {

    for (int r = 0 ; r < _height; r++){
      for (int c = 0; c < _width; c++) {
        _territoryMatrix[r][c] = PLACEHOLDER_CHARACTER;
      }
    }
  }

  /**
   * Handles how orientation line is drawn.
   *
   * @param command is "draw" or "erase"
   */
  public void manageOrientationLine(String command) {

    String c;
    switch (command) {
      case "draw":
        c = ORIENTATION_CHARACTER;
        break;
      case "erase":
        c = PLACEHOLDER_CHARACTER;
        break;
      default:
        throw new IllegalArgumentException ("Unknown command requested: " + command);
    } // end switch

    drawOrientationLine(c);
  } // end method

  /**
   * Adds the orientation character across the bottom or along the right side
   * depending whether horizontal or vertical territory selected.
   *
   * @param str Holds the string character used to draw the line.
   */
  public void drawOrientationLine(String str) {

    switch (_orientation) {
      case HORIZONTAL_ORIENTED:
        for (int c = 0; c < _width; c++) {
          _territoryMatrix[_height-1][c] = str;
        }
        break;
      case VERTICAL_ORIENTED:
        for (int r = 0; r < _height; r++) {
          _territoryMatrix[r][_width-1] = str;
        }
        break;
      default:
        throw new IllegalArgumentException ("Unknown orientation: " + _orientation);
    } // end switch
  }

  /**
   * Given a whole string, and a row and column location, place that string as a
   * substring within the territory matrix.
   *
   * @param str
   *            The string desired to place as substring
   * @param r
   *            The row in the territory matrix
   * @param c
   *            The column in the territory matrix
   */
  public void dropInString(String str, int r, int c) {

    if (str.length() > _territoryMatrix[r].length) {
      throw new IllegalArgumentException("Supplied string too long for territory width.");
    }

    for (int i = 0; i < str.length(); i++) {
      _territoryMatrix[r][c+i] = str.substring(i,i+1);
    } // end for
  } // end method


  /* getter and setter methods */

  public String[][] get_territoryMatrix() {
    return _territoryMatrix;
  }

  public void set_territoryMatrix(String[][] _territoryMatrix) {
    this._territoryMatrix = _territoryMatrix;
  }

  public int get_height() {
    return _height;
  }

  public void set_height(int _height) {
    this._height = _height;
  }

  public int get_width() {
    return _width;
  }

  public void set_width(int _width) {
    this._width = _width;
  }

  public String get_territoryName() {
    return _territoryName;
  }

  public void set_territoryName(String _territoryName) {
    this._territoryName = _territoryName;
  }

  public String get_playerNameWhoControls() {
    return _playerNameWhoControls;
  }

  public void set_playerNameWhoControls(String _playerNameWhoControls) {
    this._playerNameWhoControls = _playerNameWhoControls;
  }

  public int get_numberOfArmies() {
    return _numberOfArmies;
  }

  public void set_numberOfArmies(int _numberOfArmies) {
    this._numberOfArmies = _numberOfArmies;
  }

  public int get_orientation() {
    return _orientation;
  }

  public void set_orientation(int _orientation) {
    this._orientation = _orientation;
  }

  public int get_handleRow() {
    return _handleRow;
  }

  public void set_handleRow(int _handleRow) {
    this._handleRow = _handleRow;
  }

  public int get_handleCol() {
    return _handleCol;
  }

  public void set_handleCol(int _handleCol) {
    this._handleCol = _handleCol;
  }

} // end class

