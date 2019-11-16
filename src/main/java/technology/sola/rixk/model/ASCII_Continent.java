package technology.sola.rixk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds information about continents
 * <p>
 * 0,0 is upper left # of continent.
 *
 * @author Brint
 *
 */
public class ASCII_Continent {

  private static final Logger LOGGER = Logger.getLogger(ASCII_Continent.class.getName());

  // Constants
  private final int 	 MINIMUM_TERRITORY_HEIGHT = 4;  // enough for minimum name, player, army info to fit.
  private final int 	 MINIMUM_TERRITORY_WIDTH  = 6;  // enough for minimum name info to fit.
  private final int 	 MINIMUM_CONTINENT_HEIGHT = (MINIMUM_TERRITORY_HEIGHT * 2) + 2;  // accounts for short name & perimeter
  private final int 	 MINIMUM_CONTINENT_WIDTH  = MINIMUM_TERRITORY_WIDTH  +  2;  // accounts for # top/bot perimeter

  private final String PLACE_HOLDER_CHARACTER = " ";
  private final String EDGE_SYMBOL         = "#";
  private final int    HORIZONTAL_ORIENTED = 1;
  private final int	 VERTICAL_ORIENTED 	 = 2;
  private final int	 USE_TERRITORY_COUNT = -1;
  private final int	 UNNDEFINED_INT      = -999; // used for initializations

  // Continent attributes
  private String 		_continentName;  // e.g. C0
  private int    		_handleRow;
  private int			_handleCol;
  private int			_bonusArmies;
  private int 		_continentHeight;
  private int 		_continentWidth;
  private int 		_orientation; // how each and every territory oriented (horz or vert.) on continent.
  private int			_minTerritoriesAllowed = 2;
  private int			_maxTerritoriesAllowed = 4;
  private String[][] 	_continentMatrix;
  private int			_numberOfConnections;

  // Territory meta-attributes
  private int 		_numberOfTerritories;
  private int			_territoryWidth;
  private int			_territoryHeight;

  /**
   * Contains all handle locations in one place for each continent. The upper
   * most location that a territory sits within a continent would be 1,1. Note that
   * individual territory handles should be taken from each territory object itself, since these
   * eventually get updated to map coordinates, not continent coordinates, as are stored in this list.
   * <p>
   * Format is [row][col][row][col][row][col][row][col]..."
   */
  private ArrayList<Integer> _territoryHandlesOnContienentList = new ArrayList<Integer>();

  /**
   * Holds all the territories that are located within the continent.
   */
  private ArrayList<ASCII_Territory> _territoriesList = new ArrayList<ASCII_Territory>();

  /**
   * The <b>key</b> contains an ordered pair describing the intra-continent
   * location (not the map location!) of a piece of territory, where the first
   * element is the row and the second element is the column (e.g. "6,4"). The
   * <b>value</b> contains the name of the territory (e.g. "C13.T4").
   *
   */
  private HashMap<String,String> _allTerritoryPointsOnContientHashMap  = new HashMap<>();


  public ASCII_Continent() {  LOGGER.setLevel(Level.OFF);}

  /**
   * Constructor to build a continent.
   *
   * @param continentName
   *            A string of the continent's name. Should be short.
   * @param height
   *            An integer for how many rows high the continent should be.
   * @param width
   *            Am integer for how many columns wide the continent should be.
   */
  public ASCII_Continent(String continentName, int height, int width) {

    LOGGER_classSettings();

    _continentName 	 = continentName;
    _bonusArmies = USE_TERRITORY_COUNT;
    _continentHeight = height;
    _continentWidth  = width;

    go();

    LOGGER.info("exit");
  }

  /**
   * Constructor to build a continent.
   *
   * @param continentName
   *            A string of the continent's name. Should be short.
   * @param bonusArmies
   *            An integer for how many additional armies this continent gets
   *            during get armies phase.
   * @param height
   *            An integer for how many rows high the continent should be.
   * @param width
   *            Am integer for how many columns wide the continent should be.
   */
  public ASCII_Continent(String continentName, int bonusArmies, int height, int width) {

    LOGGER_classSettings();

    _continentName 	 = continentName;
    _bonusArmies     = bonusArmies;
    _continentHeight = height;
    _continentWidth  = width;

    go();
  }


  /**
   * Main algorithm is here.
   */
  private void go() {


    LOGGER.info("entry");

    _continentMatrix = new String[_continentHeight][_continentWidth];

    errorcheckParameters();
    fillContinentWithPlaceHolderSymbol();
    fillPerimeterWithEdgeSymbol();
    do {
      decideOrientationForTerritoryPlacement();
      calcNumTerritoriesToPlace();
      calcWidthHeightOfEachTerritory();
    } while (territoryMismatch());
    establishHandlesForTerritories();
    createTerritories();
    placeTerritoriesIntoContinent();
    stipulateBonusArmies();

    LOGGER.info("exit");
  }

  /**
   * Set logger stuff everything in this class.
   */
  private void LOGGER_classSettings() {

//		RixkUtils.setupLogger( LOGGER, "ASCII_Continent.log" );
    LOGGER.setLevel(Level.OFF);
    LOGGER.info("Logger Name: "+LOGGER.getName());
  }

  /**
   * Checks if bonus armies have been stipulated by number of territories
   * in continent, and if so stipulates an amount for the continent.
   *
   */
  public void stipulateBonusArmies() {

    if (_bonusArmies == USE_TERRITORY_COUNT) { _bonusArmies = this._numberOfTerritories; }

  }


  /**
   * Makes sure that territories can fit without remainder or within
   * given continent size.
   *
   * @return
   */
  public boolean territoryMismatch() {

    LOGGER.info("enter");

    boolean flag = false;
    flag = flag || (this._territoryHeight < MINIMUM_TERRITORY_HEIGHT);
    flag = flag || (this._territoryWidth < MINIMUM_TERRITORY_WIDTH);

    LOGGER.config("territory mismatch flag: " + flag);

    LOGGER.info("exit");

    return flag;
  }


  /**
   * fills in continent matrix with something to see.
   */
  public void fillContinentWithPlaceHolderSymbol() {

    LOGGER.info("enter");

    for (int rr=0; rr < this._continentHeight; rr++) {
      for (int cc = 0; cc < this._continentWidth; cc++) {
        this._continentMatrix[rr][cc] = PLACE_HOLDER_CHARACTER;
      } // end for
    } // end for

    LOGGER.info("exit");

  }


  /**
   * provides some basic checks on continent parameter values.
   */
  public void errorcheckParameters() {

    LOGGER.info("enter");

    if (this._continentHeight < MINIMUM_CONTINENT_HEIGHT) {
      throw new IllegalArgumentException(
        "ERROR: continent must be at least "
          + MINIMUM_CONTINENT_HEIGHT + " rows in height.");
    }

    if (this._continentWidth < MINIMUM_CONTINENT_WIDTH) {
      throw new IllegalArgumentException(
        "ERROR: continent must be at least "
          + MINIMUM_CONTINENT_WIDTH + " columns wide.");
    }

    LOGGER.info("exit");
  }


  /**
   * Run thru each territory and grab the handles and its matrix and copy it
   * into the continent where the handles stipulate it shall be.
   */
  public void placeTerritoriesIntoContinent() {

    LOGGER.info("enter");

    String terrName	  = "";
    int handleRow = UNNDEFINED_INT;
    int handleCol = UNNDEFINED_INT;
    String[][] territoryMatrix = null;

    for (int terrNum = 0; terrNum< this._numberOfTerritories; terrNum++) {

      terrName = this._territoriesList.get(terrNum).get_territoryName();
      handleRow = this._territoriesList.get(terrNum).get_handleRow(); // get handles
      handleCol = this._territoriesList.get(terrNum).get_handleCol();
      territoryMatrix = this._territoriesList.get(terrNum).get_territoryMatrix(); // and the matrix

      copySingleTerritoryIntoContinent(terrName, handleRow, handleCol, territoryMatrix);
    } // end for

    LOGGER.info("exit");
  } // end method


  /**
   * Copy a single territory into a continent.
   *
   * @param name
   * 			  String name of the territory to be placed.
   * @param handleRow
   *            int of first row location
   * @param handleCol
   *            int of first column location
   * @param territoryMatrix
   *            String [][] array holding territory to be copied.
   */
  public void copySingleTerritoryIntoContinent(String name, int handleRow, int handleCol,
                                               String[][] territoryMatrix) {

    LOGGER.info("enter");

    /* copy the territory matrix into the continent */
    for (int rr=0; rr < this._territoryHeight; rr++) {
      for (int cc = 0; cc < this._territoryWidth; cc++) {
        this._continentMatrix[handleRow+rr][handleCol+cc] =
          territoryMatrix[rr][cc];

        updateTerritoryPointsHashMap(name, handleRow, handleCol, rr, cc);

      } // end for
    } // end for

    LOGGER.info("exit");
  }

  /**
   * Given a territory object with all of its attributes already
   *  set to map coordinates, place it into the continent.
   *
   * @param territory
   */
  public void placeTerritoryIntoContient(ASCII_Territory territory) {

    LOGGER.info("enter");

    int contHandleRow = this._handleRow;
    int contHandleCol = this._handleCol;
    int terrHandleRow = territory.get_handleRow();
    int terrHandleCol = territory.get_handleCol();

    int contRow = 0;
    int contCol = 0;

    String[][] territoryMatrix = territory.get_territoryMatrix();

    /* copy the territory matrix into the continent */
    for (int rr=0; rr < this._territoryHeight; rr++) {
      for (int cc = 0; cc < this._territoryWidth; cc++) {
        contRow = rr + Math.abs(contHandleRow - terrHandleRow);
        contCol = cc + Math.abs(contHandleCol - terrHandleCol);
        _continentMatrix[contRow][contCol] = territoryMatrix[rr][cc];
      } // end for

    } // end for


    LOGGER.info("exit");

    // lstr += (contRow+","+contCol + "\r\n");

  }

  /**
   * Given a territory name, put out the territory object corresponding to that name.
   * @param territoryName a territory name (e.g. "C0.T0")
   *
   * @return a territory object.
   *
   */
  public ASCII_Territory pullTerritoryByName(String territoryName) {

    LOGGER.info("enter");

    int idNum = Integer.parseInt( territoryName.substring(1)); // get territory id #
    ASCII_Territory territory = this._territoriesList.get( idNum ); // pull the [continent]

    LOGGER.info("exit");

    return territory;
  }


  /**
   * Every point within a territory must be associated with that territory, so when a territory
   * is placed, the points within its bouds must be updated.
   *
   * @param name official name of territory
   * @param handleRow	row where it sits in contient
   * @param handleCol col where it sits in contient
   * @param rr relative row index from from handle
   * @param cc relative col index from handle
   */
  private void updateTerritoryPointsHashMap(String name, int handleRow,
                                            int handleCol, int rr, int cc) {

    LOGGER.info("enter");

    String key;
    String value;
    key = (handleRow+rr) +","+ (handleCol+cc);		// note location chunck
    value = name;			// note official name (e.g. C0.T0)
    _allTerritoryPointsOnContientHashMap.put(key, value);

    LOGGER.info("enter");

  }


  /**
   * Fill the territories list with well-formed territories (details). A
   * well-formed territory has a name, height and width, orientation, and the
   * handle values set within it -- the latter of which is done here.
   */
  public void createTerritories() {

    LOGGER.info("enter");

    ASCII_Territory territory;
    int i;
    for (i = 0; i< this._numberOfTerritories; i++) {

      territory = new ASCII_Territory(
        _continentName + ".T" + Integer.toString(i),
        _territoryHeight,
        _territoryWidth,
        _orientation,
        _territoryHandlesOnContienentList.get(i*2), 	// row handle
        _territoryHandlesOnContienentList.get(i*2+1));  // col handle

      _territoriesList.add(territory);

    } // end loop

    /* erase orientation line on last territory
     * so any left-over continent after territory
     * division can be incorporated into last
     * territory.
     */
    territory = _territoriesList.get(i-1);	// pluck out the last territory
    territory.manageOrientationLine("erase"); // erase its line
    _territoriesList.set(i-1, territory); // put it back into last spot again.

    LOGGER.info("exit");

  }


  public void establishHandlesForTerritories() {

    LOGGER.info("enter");

    int terrOrientation = UNNDEFINED_INT;
    int rowhandle 		= UNNDEFINED_INT;
    int colhandle 		= UNNDEFINED_INT;
    int territoriesPlaced = 0; // territories placed
    int col = 1;	// begin just inside continent # perimeter, at upper left.
    int row = 1;
    do {
      switch (_orientation) {
        case VERTICAL_ORIENTED:
          rowhandle = row;
          colhandle = col+(territoriesPlaced*this._territoryWidth);
          break;
        case HORIZONTAL_ORIENTED:
          colhandle = col;
          rowhandle = row+(territoriesPlaced*this._territoryHeight);
          break;
        default:
          System.out.println ("Unknown orientation encountered: " + terrOrientation);
          System.exit(0);
          break;
      } // end switch
      this._territoryHandlesOnContienentList.add(rowhandle);
      this._territoryHandlesOnContienentList.add(colhandle);

      territoriesPlaced++;	// get the next one.

    } while (territoriesPlaced < _numberOfTerritories); // while territories still to place

    LOGGER.info("exit");

  } // end method

  /**
   * With number of territories, their orientation, and size of continent
   * already known, calculate how wide and high all the equally sized
   * territories must be to fit into the continent, and place that number pair
   * into the territory height and width value array list (details). Note that
   * a -2 offset is applied because of surrounding perimeter of #'s, so territories
   * must all be small enought to be packed-in to the continent.
   */
  public void calcWidthHeightOfEachTerritory() {

    LOGGER.info("enter");

    switch (_orientation) {
      case HORIZONTAL_ORIENTED:
        _territoryHeight = (this._continentHeight-2) / _numberOfTerritories;
        _territoryWidth = this._continentWidth-2;
        break;
      case VERTICAL_ORIENTED:
        _territoryHeight = this._continentHeight-2;
        _territoryWidth = (this._continentWidth-2) / _numberOfTerritories;
        break;
      default:
        throw new IllegalArgumentException("ERROR: Unknown orientation encountered: " + _orientation);

    } // end switch

    LOGGER.info("exit");

  }

  /**
   * Given a horizontal or vertical orientation style, figure out how many
   * territories to place in a continent.
   *
   */
  public void calcNumTerritoriesToPlace() {

    LOGGER.info("enter");

    _numberOfTerritories = (_minTerritoriesAllowed + (int)(Math.random() *
      ((_maxTerritoriesAllowed - _minTerritoriesAllowed) + 1)));

    LOGGER.info("numberOfTerritories to place: " + _numberOfTerritories);

    LOGGER.info("exit");
  }

  /**
   * Randomly decide style of placement applied to all territories in continent.  They
   * will all have the same orientation in a continent.
   *
   * @return 1 if horizontal, 2 if vertical */
  public void decideOrientationForTerritoryPlacement() {

    LOGGER.info("enter");

    _orientation = (1 + (int)(Math.random() *
      ((VERTICAL_ORIENTED - HORIZONTAL_ORIENTED) +	HORIZONTAL_ORIENTED)));

    LOGGER.info("exit");
  }

  /**
   * Show the continent matrix
   */
  public String toString() {

    String str = "";

    for (int r = 0 ; r < _continentHeight; r++){
      for (int c = 0; c < _continentWidth; c++) {
        str += _continentMatrix[r][c];
      }
      str += "\r\n";
    }
    return str;
  }

  /**
   * Sends all the attributes of the continent object
   * to a string. Shows the FULL MONTY of fields.
   *
   * @return
   */
  public String toString2() {

    String str = "\r\n";

    str += "_continentName " + _continentName+ "\r\n";
    str += "_handleRow " + _handleRow+ "\r\n";
    str += "_handleCol " + _handleCol+ "\r\n";
    str += "_bonusArmies " + _bonusArmies+ "\r\n";
    str += "_continentHeight " + _continentHeight+ "\r\n";
    str += "_continentWidth " + _continentWidth+ "\r\n";
    str += "_orientation " + _orientation+ "\r\n";
    str += "_minTerritoriesAllowed " + _minTerritoriesAllowed+ "\r\n";
    str += "_maxTerritoriesAllowed " + _maxTerritoriesAllowed+ "\r\n";
    str += "_numberOfConnections " + _numberOfConnections+ "\r\n";
    str += "cont. matrix size h " + this._continentMatrix.length+ "\r\n";
    str += "cont. matrix size w " + this._continentMatrix[1].length+ "\r\n";
    str += "TERRITORY ATTRIBUTES " + ""+ "\r\n";
    str += "   _numberOfTerritories " + _numberOfTerritories+ "\r\n";
    str += "   _territoryWidth " + _territoryWidth+ "\r\n";
    str += "   _territoryHeight " + _territoryHeight+ "\r\n";


    return str;
  }

  /**
   * Shows all the territories in the list
   * @return a string representation of the territories.
   */

  public String toString_Territories() {

    String str = "";
    ASCII_Territory tt;

    for (int i = 0; i < this._numberOfTerritories; i++) {

      tt = this._territoriesList.get(i);
      str += tt.toString() + "\r\n";
    }

    return str;
  }

  /**
   * Places the edge around the perimeter within the continent matrix.
   */
  public void fillPerimeterWithEdgeSymbol() {

    LOGGER.info("enter");

    /* fill sides */
    for (int r = 0 ; r < _continentHeight; r++){
      _continentMatrix[r][0] = EDGE_SYMBOL;
      _continentMatrix[r][_continentWidth-1] = EDGE_SYMBOL;
    }

    /* fill top & bottom */
    for (int c = 0 ; c < _continentWidth; c++){
      _continentMatrix[0][c] = EDGE_SYMBOL;
      _continentMatrix[_continentHeight-1][c] = EDGE_SYMBOL;
    }

    LOGGER.info("exit");

  }


  /* Getters & Setters */


  public String get_continentName() {
    return _continentName;
  }


  public void set_continentName(String _continentName) {
    this._continentName = _continentName;
  }


  public int get_bonusArmies() {
    return _bonusArmies;
  }


  public void set_bonusArmies(int _bonusArmies) {
    this._bonusArmies = _bonusArmies;
  }


  public String[][] get_continentMatrix() {
    return _continentMatrix;
  }


  public void set_continentMatrix(String[][] _continentMatrix) {
    this._continentMatrix = _continentMatrix;
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


  public int get_minTerritoriesAllowed() {
    return _minTerritoriesAllowed;
  }


  public void set_minTerritoriesAllowed(int _minTerritoriesAllowed) {
    this._minTerritoriesAllowed = _minTerritoriesAllowed;
  }


  public int get_maxTerritoriesAllowed() {
    return _maxTerritoriesAllowed;
  }


  public void set_maxTerritoriesAllowed(int _maxTerritoriesAllowed) {
    this._maxTerritoriesAllowed = _maxTerritoriesAllowed;
  }


  public int get_numberOfTerritories() {
    return _numberOfTerritories;
  }


  public void set_numberOfTerritories(int numberOfTerritories) {
    this._numberOfTerritories = numberOfTerritories;
  }


  public int get_orientation() {
    return _orientation;
  }


  public void set_orientation(int orientation) {
    this._orientation = orientation;
  }


  public int get_continentHeight() {
    return _continentHeight;
  }


  public void set_continentHeight(int _continentHeight) {
    this._continentHeight = _continentHeight;
  }


  public int get_continentWidth() {
    return _continentWidth;
  }


  public void set_continentWidth(int _continentWidth) {
    this._continentWidth = _continentWidth;
  }


  public ArrayList<Integer> get_territoryHandlesOnContienentList() {
    return _territoryHandlesOnContienentList;
  }


  public void set_territoryHandlesOnContienentList(ArrayList<Integer> _territoryHandles) {
    this._territoryHandlesOnContienentList = _territoryHandles;
  }


  public int get_territoryWidth() {
    return _territoryWidth;
  }


  public void set_territoryWidth(int _territoryWidth) {
    this._territoryWidth = _territoryWidth;
  }


  public int get_territoryHeight() {
    return _territoryHeight;
  }


  public void set_territoryHeight(int _territoryHeight) {
    this._territoryHeight = _territoryHeight;
  }


  public ArrayList<ASCII_Territory> get_territories() {
    return _territoriesList;
  }


  public void set_territories(ArrayList<ASCII_Territory> _territories) {
    this._territoriesList = _territories;
  }

  public int getMINIMUM_TERRITORY_HEIGHT() {
    return MINIMUM_TERRITORY_HEIGHT;
  }

  public int getMINIMUM_TERRITORY_WIDTH() {
    return MINIMUM_TERRITORY_WIDTH;
  }

  public int getMINIMUM_CONTINENT_HEIGHT() {
    return MINIMUM_CONTINENT_HEIGHT;
  }

  public int getMINIMUM_CONTINENT_WIDTH() {
    return MINIMUM_CONTINENT_WIDTH;
  }

  public HashMap<String, String> get_allTerritoryPointsOnContientHashMap() {
    return _allTerritoryPointsOnContientHashMap;
  }

  public void set_allTerritoryPointsOnContientHashMap(
    HashMap<String, String> _allTerritoryPointsHashMap) {
    this._allTerritoryPointsOnContientHashMap = _allTerritoryPointsHashMap;
  }

  public String getPLACE_HOLDER_CHARACTER() {
    return PLACE_HOLDER_CHARACTER;
  }

  public String getEDGE_SYMBOL() {
    return EDGE_SYMBOL;
  }

  public int getHORIZONTAL_ORIENTED() {
    return HORIZONTAL_ORIENTED;
  }

  public int getVERTICAL_ORIENTED() {
    return VERTICAL_ORIENTED;
  }

  public int getUSE_TERRITORY_COUNT() {
    return USE_TERRITORY_COUNT;
  }

  public int get_numberOfConnections() {
    return _numberOfConnections;
  }

  public void set_numberOfConnections(int _numberOfConnections) {
    this._numberOfConnections = _numberOfConnections;
  }

  public ArrayList<ASCII_Territory> get_territoriesList() {
    return _territoriesList;
  }

  public void set_territoriesList(ArrayList<ASCII_Territory> _territoriesList) {
    this._territoriesList = _territoriesList;
  }


}

