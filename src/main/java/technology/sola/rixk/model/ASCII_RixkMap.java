package technology.sola.rixk.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Generates a simplified, Rixk map, with randomly placed continents and
 * territories. A map consists of ascii characters representing pieces of the
 * following: continents, continent shore lines, continent connections,
 * territories, territory borders, or default background (i.e oceans).
 *
 * @author Brint
 * @version created 3/18/2015
 *
 */

public class ASCII_RixkMap {

  private static final Logger LOGGER = Logger.getLogger(ASCII_RixkMap.class.getName());

  // Constants
  /**
   * Must be tall enough to hold three minimum sized continents, which
   * size could be recovered from the Continent class if needed.
   * Playing maps are likely to be much larger than these minimums.
   */
  private final int    MINIMUM_MAP_HEIGHT = 12;

  /**
   * Must be wide enough to hold three minimum sized continents, which
   * size could be recovered from the Continent class if needed.
   * Playing maps are likely to be much larger than these minimums.
   */
  private final int    MINIMUM_MAP_WIDTH  = 18;

  /**
   * Allow no taller cont. than this, but can be adjusted to fit preference.
   */
  private final int    MAXIMUM_CONTINENT_HEIGHT = 20;

  /**
   * Allow no wider cont. than this, but can be adjusted to fit preference.
   */
  private final int    MAXIMUM_CONTINENT_WIDTH = 40;

  /**
   * Placeholder for when nothing is occupying some portion of the map, akin to ocean.
   */
  private final String EMPTY_AREA_CHARACTER = ".";

  /**
   * Allow no more than this number of attempts for building a continent. If a
   * continent is built, and not immediately randomly placed (say, due to
   * overlapping another continent or continent connection), a new continent is
   * built, and placement is tried again up to n times. Placement can be a
   * problem for small maps or for maps of any size which are very densely
   * populated by continents.
   */
  private final int 	 DEFAULT_BUILD_LIMIT = 500;

  /**
   * Holds all the characters representing parts of map objects.
   */
  private String[][]  _mapMatrix;

  /**
   * Holds information about how many bonus armies a player
   * gets, depending on number of players.  Elements 0-1 are
   * undefined, since rixk requires at least 2 players.
   */
  private int[] _startingArmies = { -1,-2,40,35,30,25,20 };

  private String 		_name;
  private int 		_mapHeight;
  private int         _mapWidth;
  private int 		_continentsRequested;

  /**
   * How many continents have successfully been placed on the map during the
   * generation process.
   */
  private int _continentPlacementCnt = 0;

  /**
   * How many continents have been built for potential placement.
   */
  private int _continentBuildCnt;

  /**
   * After a map is built and continents placed, the builder connects every continent.
   */
  private ASCII_ConnectionsBuilder connectionsBuilder;

  /**
   * Holds all the continents that are located on the map, where element 0 holds
   * The first continent generated.
   */
  private ArrayList<ASCII_Continent> continentList = new ArrayList<ASCII_Continent>();


  // Hash Maps

  /**
   * Every continent is essentially a two dimensional string matrix, with the
   * row,col (0,0) element be stipulated as "the handle" of that continent.
   * All the handles represent where the (0,0) element shall be placed on the
   * greater RixkMap. These handles are stored here in a comma separated
   * format (e.g. "3,2"), which is the <b>value</b>, and where the continent
   * names (e.g. "C0") are stored as the <b>key</b>.
   * <p>
   * e.g. "C0=14,4, C1=2,14"
   *
   * @see http://www.dotnetperls.com/hashmap
   *
   */
  private HashMap<String, String> _contHandlesHashMap = new HashMap<>();


  /**
   * Every territory is essentially a two dimensional string matrix, with the
   * row,col (0,0) element be stipulated as "the handle" of that territory.
   * All the handles represent where the (0,0) element shall be placed on the
   * greater RixkMap. These handles are stored here in a comma separated
   * format (e.g. "12,35"), which is the <b>key</b>, and where the territory
   * names (e.g. "C1.T3") are stored as the <b>value</b>. Note that every
   * territory name is preceded by a period, and then the continent name
   * which contains it.
   * <p>
   * e.g. "19,5=C0.T1, 27,5=C0.T3, 3,27=C1.T2 ..."
   *
   */
  private HashMap<String, String> _terrHandlesHashMap = new HashMap<>();


  /**
   * The <b>key</b> contains an ordered pair describing the location of a piece of
   * shore line where first element is the row and second element is the column
   * (e.g. "3,4"). The <b>value</b> contains the name of the continent associated
   * with the piece of shore line (e.g. "C0").
   * <p>
   * e.g. "3,4" = "C0"
   *
   */
  private HashMap<String,String> _continentShorelinesHashMap = new HashMap<>();


  /**
   * Holds the locations for all the territories' points on all the continents.
   * <p>
   * The <b>key</b> contains an ordered pair describing the location of a piece of
   * territory where the first element is the row and the second element is the column
   * (e.g. "6,4"). The <b>value</b> contains the full name of the territory (e.g. "C13.T4").
   * <p>
   * e.g. "6,4" = "C13.T4"
   *
   */
  private HashMap<String,String> _allTerritoryPointsHashMap  = new HashMap<>();


  /**
   * Contains an ordered pair describing the name of the starting
   * territory and the ending territory of the connection.
   * <p>
   * e.g. "C0.T1,C0.T0"
   *
   */
  private ArrayList<String> _connectionsList = new ArrayList<>();

  /**
   * Holds all 4-tuples which supply information for continent connections.
   * <p>
   * e.g. 5,5,0,20
   * Which legend is: [stRow,stCol,compassDirection,length]
   */
  private ArrayList<String> _connectionsRays = new ArrayList<>();

  private Object contient;



  /**
   * Base constructor
   */
  public ASCII_RixkMap() {

    LOGGER_classSettings();
  }


  /**
   * Constructor to build map which subsequently places continents and their
   * associated territories.
   *
   * @param name
   *            Name of the map as set by user.
   * @param height
   *            How many rows high the map shall be.
   * @param width
   *            How many columns wide the map shall be.
   * @param continentsRequested
   *            How many continents the user wants.
   */
  public ASCII_RixkMap(String name, int height, int width, int continentsRequested) {

    LOGGER_classSettings();

    LOGGER.info("enter");

    _name 	= name;
    _mapHeight = height;
    _mapWidth 	= width;
    _continentsRequested = continentsRequested;

    _mapMatrix = new String[_mapHeight][_mapWidth]; // build the map matrix	to specs.

    go();

    LOGGER.info("exit");
  }

  /**
   * Constructor to build map which subsequently places continents and their
   * associated territories.
   *
   * @param userText
   *            String which holds parameters in CSV format:
   *            <p>
   *            name,height,width,continentsRequested
   */
  public ASCII_RixkMap(String userText) {

    LOGGER_classSettings();

    LOGGER.info("enter");

    String[] parameters = userText.split(",");

    _name 	= parameters[0];
    _mapHeight = Integer.parseInt(parameters[1].trim());
    _mapWidth 	= Integer.parseInt(parameters[2].trim());
    _continentsRequested = Integer.parseInt(parameters[3].trim());

    _mapMatrix = new String[_mapHeight][_mapWidth]; // build the map matrix	to specs.

    go();

    LOGGER.info("exit");

  }

  /**
   * Main algorithm is here.
   */
  private void go() {

    LOGGER_classSettings();

    LOGGER.info("enter");

    errorCheckMapParameters();
    fillMapWithPlaceHolderCharacter();
    placeAllContinents();
    buildAllConnections();

    LOGGER.info("exit");
  }


  /**
   * Set logger stuff everything in this class.
   */
  private void LOGGER_classSettings() {

//		LOGGER.setLevel(Level.CONFIG);
    LOGGER.info("Logger Name: "+LOGGER.getName());


  }


  /**
   * Loop through the process of building and placing continents, tracking that a proper number
   * were successfully place.
   */
  public void placeAllContinents() {

    LOGGER.info("enter");

    ASCII_Continent continent;
    boolean isContinentPlaced;

    while (_continentPlacementCnt < _continentsRequested ) {
      continent = buildNewContinent();
      trackContinentBuilds();
      isContinentPlaced = placeContinentAtArbitraryLocation(continent);
      trackContinentPlacements(isContinentPlaced);

      LOGGER.config ("placements / builds:  "+ _continentPlacementCnt +" / " + _continentBuildCnt);
    } // end while

    LOGGER.info("exit");
  }

  /**
   * Given the name of the continent, pull that continent and return
   * it as a continent object.
   *
   * @param continentName  The name of a continent (e.g. "C12")
   * @return a continent object
   * @since 3/25/15
   */
  public ASCII_Continent pullAContinentByName(String continentName) {

    int idNum = Integer.parseInt( continentName.substring(1)); // get cont id #
    ASCII_Continent continent = continentList.get( idNum ); // pull the [continent]

    return continent;
  }



  /**
   * Creates a connections builder class and passes the map to it, then pulls back out from
   * builder the newly modified map and the connections hashmap.
   *
   */
  public void buildAllConnections() {
    connectionsBuilder = new ASCII_ConnectionsBuilder( this );
    _mapMatrix = connectionsBuilder.getMap()._mapMatrix;
  }



  /**
   * Provides some basic checks on hard-coded continent parameter values to keep the
   * user from trying to build illegally structured maps.
   */
  public void errorCheckMapParameters() {

    LOGGER.info("enter");

    String errMessage = "";

    if (this._mapHeight < MINIMUM_MAP_HEIGHT) {
      errMessage = ("ERROR: map must be at least "
        + MINIMUM_MAP_HEIGHT + " rows in height.");
    }

    if (this._mapWidth < MINIMUM_MAP_WIDTH) {
      errMessage = ("ERROR: map must be at least "
        + MINIMUM_MAP_WIDTH + " columns wide.");
    }

    if (this._mapWidth < this.MAXIMUM_CONTINENT_WIDTH + 2) {
      errMessage = ("ERROR: Maximum continent"
        + " width too large for maximum map width. Change one or the other.");
    }

    if (this._mapHeight < this.MAXIMUM_CONTINENT_HEIGHT + 2) {
      errMessage = ("ERROR: Maximum continent"
        + " height too large for maximum map height. Change one or the other.");
    }

    if (!errMessage.equals("")) { throw new IllegalArgumentException( errMessage ); }

    LOGGER.info("exit");

  }

  /**
   * fill it with place holder character.
   */
  public void fillMapWithPlaceHolderCharacter() {

    LOGGER.info("enter");

    for (int r = 0 ; r < _mapHeight; r++){
      for (int c = 0; c < _mapWidth; c++) {
        _mapMatrix[r][c] = EMPTY_AREA_CHARACTER;
      }
    }

    LOGGER.info("exit");
  }

  /**
   * Build a continent based on size of map (details).  No continent will be larger than
   * 33% the size of the map.
   */
  public ASCII_Continent buildNewContinent() {

    LOGGER.info("enter");

    String _continentName 	= "C" + Integer.toString(_continentPlacementCnt);
    int bonusArmies     	= -1; // -1 means use territory count to determine

    ASCII_Continent continent = new ASCII_Continent(); // grab to inspect limits.

    int maxContHeight = MAXIMUM_CONTINENT_HEIGHT;
    int minContHeight =  continent.getMINIMUM_CONTINENT_HEIGHT();

    int maxContWidth =  MAXIMUM_CONTINENT_WIDTH;
    int minContWidth =  continent.getMINIMUM_CONTINENT_WIDTH();

    errorCheckNewContinentSize(maxContHeight, minContHeight, maxContWidth, minContWidth);

    int continentHeight = (minContHeight + (int)(Math.random() * ((maxContHeight - minContHeight) + 1)));
    int continentWidth  = (minContWidth  + (int)(Math.random() * ((maxContWidth  - minContWidth)  + 1)));

    continent = new ASCII_Continent(_continentName,bonusArmies,continentHeight,continentWidth);

    LOGGER.info("exit");

    return continent;

  }


  /**
   * Counts the times a continent build attempt has occurred.
   *
   * @throws IllegalStateException
   *             if too many continent build attempts tried.
   */
  public void trackContinentBuilds() {

    LOGGER.info("enter");

    _continentBuildCnt++;

    if (_continentBuildCnt > this.DEFAULT_BUILD_LIMIT) {
      throw new IllegalStateException("ERROR: Too many continent build attempts:" +_continentBuildCnt );
    }

    LOGGER.info("exit");

  }


  /**
   * Get the continent matrix and define it's handles, then place it into the map
   *
   * @return true if continent placed, false otherwise.
   */
  public boolean placeContinentAtArbitraryLocation(ASCII_Continent continent) {

    LOGGER.info("enter");


    /* keeps any continent off of map perimeter */

    int handleRow = stipulateContinentHandle(_mapHeight, continent.get_continentHeight());
    int handleCol = stipulateContinentHandle(_mapWidth, continent.get_continentWidth());

    LOGGER.info("Attempting to place @ " + handleRow + ","+ handleCol +
      " a continent of size " + continent.get_continentHeight() + ","+continent.get_continentWidth() );

    boolean isPerimeterSafe = checkPerimeter(continent, handleRow,handleCol);
    boolean isEmptyForContinent = isEmptyForContinent(continent, handleRow, handleCol);


    boolean isSafeToPlace = (isPerimeterSafe && isEmptyForContinent);


    if (isSafeToPlace) {
      placeContinentAtMapLocation(continent, handleRow, handleCol);
      LOGGER.info("placed at ("+handleRow+","+handleCol+"):"+
        " size: ["+continent.get_continentHeight()+"x"+continent.get_continentWidth()+"]\r\n" + this.toString());
      LOGGER.info("emptiness,perimeter: " +isEmptyForContinent + "," + isPerimeterSafe);
    }
    else {
      LOGGER.info("NOT placed at ("+handleRow+","+handleCol+"):"+
        " size: ["+continent.get_continentHeight()+"x"+continent.get_continentWidth()+"]\r\n" + this.toString());

      LOGGER.info("emptiness,perimeter: " +isEmptyForContinent + "," + isPerimeterSafe);
    }


    LOGGER.info("exit");

    return isSafeToPlace;
  }

  /**
   * Counts continent placements and resets builds when continent placements
   * successful.
   *
   * @param isContinentPlaced true if was successfully placed, false otherwise.
   *
   */
  public void trackContinentPlacements( boolean isContinentPlaced) {

    LOGGER.info("enter");

    if (isContinentPlaced) {
      _continentBuildCnt = 0; // reset at success.
      _continentPlacementCnt++;
    }

    LOGGER.info("exit");

  }


  /**
   * Given the size of a matrix axis, and a continent axis (i.e. row or col
   * size) stipulate a handle.
   *
   * @param matrixAxisSize
   *            how many rows or columns in the map
   * @param continentAxisSize
   *            how many rows or columns in the continent.
   * @return an integer handle value for either a row or a col
   */
  public int stipulateContinentHandle(int matrixAxisSize, int continentAxisSize) {

    LOGGER.info("enter");

    int minHandleAllowed = 1;
    int maxHandleAllowed = (matrixAxisSize-1) - continentAxisSize; // TODO bm 3/36 this might need -2

    int handle = (minHandleAllowed + (int)(Math.random() * ((maxHandleAllowed - minHandleAllowed) + 1)));

    LOGGER.config("\r\n"+
      "matrixAxisSize:    " + matrixAxisSize + "\r\n"+
      "continentAxisSize: " + continentAxisSize + "\r\n"+
      "minHandleAllowed:  " + minHandleAllowed + "\r\n"+
      "maxHandleAllowed:  " + maxHandleAllowed + "\r\n"+
      "handle:            " + handle
    );

    if (handle < 1 ) {
      throw new IllegalStateException(""+
        "ERROR: Handle cannot be constructed." + "\r\n" +
        "matrixAxisSize: " + matrixAxisSize +  "\r\n" +
        "continentAxisSize: " + continentAxisSize  + "\r\n" +
        "minHandleAllowed: " + minHandleAllowed  + "\r\n" +
        "maxHandleAllowed: " + maxHandleAllowed  + "\r\n" + // -3 would mean contient axis 3 too large.
        "handle: " + handle  + "\r\n"

      );
    }

    LOGGER.info("\r\n"+
      "matrixAxisSize: " + matrixAxisSize +  "\r\n" +
      "continentAxisSize: " + continentAxisSize  + "\r\n" +
      "minHandleAllowed: " + minHandleAllowed  + "\r\n" +
      "maxHandleAllowed: " + maxHandleAllowed  + "\r\n" + // -3 would mean contient axis 3 too large.
      "handle: " + handle  + "\r\n"

    );


    LOGGER.info("exit");


    return handle;
  }

  /**
   * Given the handles, put a continent on the map and add it to
   * the continent list and update all the continent hashmaps
   * with the newly-placed continent.
   */
  public void placeContinentAtMapLocation(ASCII_Continent continent,
                                          int handleRow, int handleCol) {

    LOGGER.info("enter");

    drawContinentOntoMapLocation(continent,handleRow, handleCol);

    String[][] continentMatrix = continent.get_continentMatrix();
    for (int rr=0; rr < continent.get_continentHeight(); rr++) {
      for (int cc = 0; cc < continent.get_continentWidth(); cc++) {
        _mapMatrix[handleRow+rr][handleCol+cc] = continentMatrix[rr][cc];

      } // end for
    } // end for

    continent.set_handleRow(handleRow);	// officially set it's row and col values since placed.
    continent.set_handleCol(handleCol);

    this.continentList.add(continent);  // put it into the big list of continents for this map

    updateAllContinentHashMaps(continent);

    LOGGER.info("exit");
  }

  /** TODO bm
   * Given the the continent and its map coordinate handle locations, draw
   * that continent onto the map.
   *
   * @param continent a continent object fully loaded.
   * @param handleRow a location on the map
   * @param handleCol a location on the map
   */
  public void drawContinentOntoMapLocation(ASCII_Continent continent,
                                           int handleRow, int handleCol) {

    //RixkUtils.setupLogger( LOGGER, "C:/Users/brint/Documents/GitHub/csne4323-spring2015/project1/logs/ASCII_RixkMap.log" );

    LOGGER.info("enter");

    String[][] continentMatrix = continent.get_continentMatrix();

    LOGGER.config(continent.toString2());
    LOGGER.config("\r\n Map:"+this.toString());


    for (int rr=0; rr < continent.get_continentHeight(); rr++) {
      for (int cc = 0; cc < continent.get_continentWidth(); cc++) {

        LOGGER.config("\r\n"
          + "map size: " + _mapMatrix.length +","+_mapMatrix[0].length + " "
          + "handleRow+rr["+ (handleRow+rr) + "] handleCol+cc[" + (handleCol+cc)+"]"
        );

        _mapMatrix[handleRow+rr][handleCol+cc] = continentMatrix[rr][cc];

      } // end for
    } // end for


    LOGGER.info("exit");


  }

  /**
   * Continent has been placed, so update hash maps associated with
   * the collection of all continents.
   *
   */
  public void updateAllContinentHashMaps(ASCII_Continent continent) {

    LOGGER.info("enter");

    updateContHandlesHashMap(continent);
    updateContinentShorelinesHashMap(continent);
    convertTerritoryHandleHashMapCoordsToMapCoords(continent);
    convertAllTerritoryPointsHashMapToMapCoords(continent);

    LOGGER.info("exit");
  }


  /**
   * Take newly minted continent and store it's handles in the
   * hashmap which tracks handles for all continents.
   */
  public void updateContHandlesHashMap(ASCII_Continent continent) {

    LOGGER.info("enter");

//		String key = "";  // original
//		String value = "";
//		int row = continent.get_handleRow();
//		int col = continent.get_handleCol();
//		key = row + "," + col;
//		value = continent.get_continentName();
//		this._contHandlesHashMap.put(key,value);

    String key = "";
    String value = "";
    int row = continent.get_handleRow();
    int col = continent.get_handleCol();
    value = row + "," + col;
    key = continent.get_continentName();
    this._contHandlesHashMap.put(key,value);

    LOGGER.info("exit");

  }

  /**
   * Get all the locations of a continents surrounding shore line characters
   * and put them in the shore line hashmap.
   */
  public void updateContinentShorelinesHashMap(ASCII_Continent continent) {

    LOGGER.info("enter");

    int row = 0;
    int col = 0;

    // get starting point of all four shore line perimeters
    int sTopRow = continent.get_handleRow();
    int sBotRow = continent.get_handleRow() + continent.get_continentHeight() - 1;
    int sLefCol = continent.get_handleCol();
    int sRgtCol = continent.get_handleCol() + continent.get_continentWidth() -1;

    // do top row
    for (col = sLefCol; col < sLefCol + continent.get_continentWidth(); col++) {
      placeKeyValuePair(continent, sTopRow, col);

    }// end loop

    // get all bottom row
    for (col = sLefCol; col < sLefCol + continent.get_continentWidth(); col++) {
      placeKeyValuePair(continent, sBotRow, col);
    }// end loop

    // get all left side
    for (row = sTopRow; row < sTopRow + continent.get_continentHeight(); row++) {
      placeKeyValuePair(continent, row, sLefCol);
    }

    // get all right side
    for (row = sTopRow; row < sTopRow + continent.get_continentHeight(); row++) {
      placeKeyValuePair(continent, row, sRgtCol);
    }

    LOGGER.info("exit");
  }

  /**
   * Given a continent, and a row,col location on it where a shore line
   * character is located, place as key,value pair as < "row,col","name">
   *
   * @param continent
   *            a placed continent
   * @param row
   *            row location of a shore line character
   * @param col
   *            col location of a shore line character
   *
   * @return  the row,col key entered.
   */
  public String placeKeyValuePair(ASCII_Continent continent, int row, int col) {
    String key;
    String value;
    key = row + "," + col; // set key to row,col location
    value = continent.get_continentName();// set value to cont. name
    this._continentShorelinesHashMap.put(key, value);

    return key;
  }


  /**
   * One a continent has been placed on the map, it's territories must have
   * their handles updated to match the map coordinates.
   * @param continent a recently placed continent.
   */
  public void convertTerritoryHandleHashMapCoordsToMapCoords(
    ASCII_Continent continent) {

    LOGGER.info("enter");

    String key;
    String value;
    int updatedValue = 0;
    ASCII_Territory singleTerritory = null;
    for ( int i = 0; i <continent.get_territories().size(); i++ ) {  // look thru them all

      singleTerritory = continent.get_territories().get( i );  // pull a territory

      updatedValue = singleTerritory.get_handleRow() + continent.get_handleRow(); // update it's row
      singleTerritory.set_handleRow( updatedValue);

      updatedValue = singleTerritory.get_handleCol() + continent.get_handleCol(); // and col too.
      singleTerritory.set_handleCol( updatedValue );

      /* properly format things */
      key = singleTerritory.get_handleRow() +","+ singleTerritory.get_handleCol(); // e.g "3,5"
      value = singleTerritory.get_territoryName(); // terr. name already has cont. name in it. e.g. "C0.T1"

      this._terrHandlesHashMap.put(key, value); // place info

    } // end loop

    LOGGER.info("exit");

  } // end method


  /**
   * Once a continent has been placed on the map, it's territories must have
   * all the points inside their boundaries updated to match the map
   * coordinates.
   *
   * @param continent
   *            a recently placed continent.
   */
  public void convertAllTerritoryPointsHashMapToMapCoords(
    ASCII_Continent continent) {

    LOGGER.info("enter");

    String []rc_array;
    String value;
    int nRow,nCol = 0;
    String newKey;

    HashMap<String,String> catphm = continent.get_allTerritoryPointsOnContientHashMap(); // get the cont.'s terr. loc's map.
    Set<String> keys = catphm.keySet(); // get all the locations
    for (String key : keys) { 			// loop thru all the keys
      rc_array = key.split(",");		// split key to rc_array[0][1]
      value 	 = catphm.get(key); 	// pull value to hold for a moment
      nRow     = Integer.parseInt(rc_array[0]) + continent.get_handleRow(); 	// convert to new row
      nCol     = Integer.parseInt( rc_array[1]) + continent.get_handleCol(); 	// convert to new col
      newKey   = nRow + "," + nCol;   // make new key from updated row,col values
      LOGGER.fine("\r\n" +
        "before: " + key+   ","+value+"\r\n" +
        "after : " + newKey+","+value+"\r\n"
      );

      _allTerritoryPointsHashMap.put(newKey, value); // place converted key-value into ascii map all points hash map.
    }

    LOGGER.info("exit");
  }


  /**
   * Given the handles, see if a map area is empty for reception of a
   * continent at that location.
   *
   * @return true if area is empty and can hold the the continent, false if
   *         area contains something.
   */
  public boolean isEmptyForContinent(ASCII_Continent continent,
                                     int handleRow, int handleCol) {

    LOGGER.info("enter");

    boolean isEmpty = true;

    for (int rr=0; rr < (continent.get_continentHeight()) && (isEmpty==true); rr++) {
      for (int cc = 0; cc < continent.get_continentWidth(); cc++) {

        //LOGGER.info("rr,cc:" + rr +"," + cc);

        if (!(_mapMatrix[handleRow+rr][handleCol+cc].equals(EMPTY_AREA_CHARACTER))) {
          LOGGER.fine("nonempty found:" +
            _mapMatrix[handleRow+rr][handleCol+cc] +
            " @ (" + (handleRow+rr) + " , " + (handleCol+cc) +")"  );
          isEmpty = false;
          break;
          //break;
        }
      } // end for
    }

    //LOGGER.info("exit value:" + isEmpty );
    LOGGER.info("exit");
    return isEmpty;
  }


  /**
   *
   * @return boolean true if continent has empty perimeter, false otherwise
   */
  public boolean checkPerimeter(ASCII_Continent continent, int handleRow, int handleCol) {

    LOGGER.info("enter");

    boolean isPerimeterSafe = true;


    // check outside top & bottom of continent (-1/+2 checks for corners too)
    for (int cc = 0; cc < continent.get_continentWidth()+2; cc++) {

      // top outer
      if (!this._mapMatrix[handleRow-1][cc + handleCol-1].equals(this.EMPTY_AREA_CHARACTER)) { // check above
        isPerimeterSafe = false;
        break;
      }

      // bott outer
      if (!this._mapMatrix[handleRow+continent.get_continentHeight()][cc + handleCol-1]
        .equals(this.EMPTY_AREA_CHARACTER)) { // below
        isPerimeterSafe = false;
        break;
      }
    }

    for (int rr = 0; rr < continent.get_continentHeight()+2; rr++) {

      // left side outer
      if (!this._mapMatrix[rr + handleRow-1][handleCol-1].equals(this.EMPTY_AREA_CHARACTER)) { // check above
        isPerimeterSafe = false;
        break;
      }

      //right side outer
      if (!this._mapMatrix[rr + handleRow-1][handleCol+continent.get_continentWidth()]
        .equals(this.EMPTY_AREA_CHARACTER)) { // below
        isPerimeterSafe = false;
        break;
      }

    }

    LOGGER.info("exit");

    return isPerimeterSafe;
  }

  /**
   * Makes sure that map is not too small for minimum continent size.
   */
  public void errorCheckNewContinentSize(int maxContHeight,
                                         int minContHeight, int maxContWidth, int minContWidth) {

    LOGGER.info("enter");

    String message ="";

    if (maxContHeight < minContHeight) {
      message = _mapHeight+" x "+_mapWidth+" map too small for "
        + "minimum continent height of " + minContHeight;
      throw new IllegalStateException(message);
    }
    else if (maxContWidth < minContWidth) {
      message = _mapHeight+" x "+_mapWidth+" map too small for "
        + "minimum continent width of " + minContWidth;
      throw new IllegalStateException(message);
    }

    LOGGER.info("exit");

  }

  /**
   * @return a string representing the map.
   */
  public String toString() {

    String str = "";
    int r=0,c=0;

    for (r = 0 ; r < _mapHeight; r++){
      str = addRowColNumbers(str, r, c);
      for (c = 0; c < _mapWidth; c++) {
        str += _mapMatrix[r][c];
      }
      str += "\r\n";
    }

    return str;
  }

  /**
   * Just pretties things up with numbers on top and left side of map.
   */
  public String addRowColNumbers(String str, int r, int c) {
    if (r==0 && c==0) {
      for (int i = 0; i < _mapWidth; i++) {
        str += i % 10;
      }
      str = "  " + str + "\r\n";
    }
    if (r<10) str += "0";
    str += r;
    return str;
  }

  /**
   * Given a CSV location, return either as row axis or col axis<p>
   * Example:
   *<pre>
   * int row = toInt(location, "row");
   * int col = toInt(location, "col");
   * </pre>
   *
   * @param cmd either "row" or "col" axis
   * @return in integer representing the command request.
   */
  public int cvtAxis( String location, String cmd) {

    int axisValue = -1;
    String[] values = location.split(",");

    if (cmd.equals("row")) {
      axisValue = Integer.parseInt(values[0].trim());
    }
    else if (cmd.equals("col")) {
      axisValue = Integer.parseInt(values[1].trim());

    } else
    {
      throw new IllegalArgumentException("ERROR: Improper command: " + cmd);
    }
    return axisValue;
  }


  /**
   * Get what's in the matrix.
   *
   * @param row a matrix row
   * @param col a matrix col
   * @return a string of what's there.
   */
  public String peek(int row, int col) {

    return this._mapMatrix[row][col];
  }


  /**
   * Put a string character into the matrix.
   *
   * @param str desired character.
   * @param row a matrix row
   * @param col a matrix col
   * @return a string of what's there.
   */
  public void poke(String str, int row, int col) {

    _mapMatrix[row][col] = str;
  }

  /**
   * checks if either row or col values are off the map.
   *
   * @param row  a map location
   * @param col a col location
   *
   * @return true if row or col value is off map.
   */
  public boolean isOffMap(int row, int col) {

    boolean flag = false;

    if (row >= this.get_mapHeight()) {
      flag = true;
    }

    if (row < 0 ) {
      flag = true;
    }

    if (col < 0 ) {
      flag = true;
    }

    if (col >= this.get_mapWidth()) {
      flag = true;
    }

    return flag;

  }



  // Getters and Setters

  public String[][] get_mapMatrix() {
    return _mapMatrix;
  }


  public void set_mapMatrix(String[][] _mapMatrix) {
    this._mapMatrix = _mapMatrix;
  }


  public String get_name() {
    return _name;
  }


  public void set_name(String _name) {
    this._name = _name;
  }

  public int get_mapHeight() {
    return _mapHeight;
  }


  public void set_mapHeight(int _mapHeight) {
    this._mapHeight = _mapHeight;
  }


  public int get_mapWidth() {
    return _mapWidth;
  }


  public void set_mapWidth(int _mapWidth) {
    this._mapWidth = _mapWidth;
  }


  public int get_continentPlacementCnt() {
    return _continentPlacementCnt;
  }


  public void set_continentPlacementCnt(int _continentPlacementCnt) {
    this._continentPlacementCnt = _continentPlacementCnt;
  }


  public ASCII_ConnectionsBuilder getConnectionsBuilder() {
    return connectionsBuilder;
  }


  public void setConnectionsBuilder(ASCII_ConnectionsBuilder connectionsBuilder) {
    this.connectionsBuilder = connectionsBuilder;
  }


  public HashMap<String, String> get_contHandlesHashMap() {
    return _contHandlesHashMap;
  }


  public void set_contHandlesHashMap(HashMap<String, String> _contHandlesHashMap) {
    this._contHandlesHashMap = _contHandlesHashMap;
  }


  public HashMap<String, String> get_terrHandlesHashMap() {
    return _terrHandlesHashMap;
  }


  public void set_terrHandlesHashMap(HashMap<String, String> _terrHandlesHashMap) {
    this._terrHandlesHashMap = _terrHandlesHashMap;
  }


  public HashMap<String, String> get_continentShorelinesHashMap() {
    return _continentShorelinesHashMap;
  }


  public void set_continentShorelinesHashMap(
    HashMap<String, String> _continentShorelinesHashMap) {
    this._continentShorelinesHashMap = _continentShorelinesHashMap;
  }


  public ArrayList<String> get_connectionsList() {
    return _connectionsList;
  }


  public void set_connectionsList(ArrayList<String> _connectionsHashMap) {
    this._connectionsList = _connectionsHashMap;
  }


  public int getMINIMUM_MAP_HEIGHT() {
    return MINIMUM_MAP_HEIGHT;
  }


  public int getMINIMUM_MAP_WIDTH() {
    return MINIMUM_MAP_WIDTH;
  }


  public int getMAXIMUM_CONTINENT_HEIGHT() {
    return MAXIMUM_CONTINENT_HEIGHT;
  }


  public int getMAXIMUM_CONTINENT_WIDTH() {
    return MAXIMUM_CONTINENT_WIDTH;
  }


  public String getEMPTY_AREA_CHARACTER() {
    return EMPTY_AREA_CHARACTER;
  }


  public HashMap<String, String> get_allTerritoryPointsHashMap() {
    return _allTerritoryPointsHashMap;
  }


  public void set_allTerritoryPointsHashMap(
    HashMap<String, String> _allTerritoryPointsHashMap) {
    this._allTerritoryPointsHashMap = _allTerritoryPointsHashMap;
  }


  public ArrayList<ASCII_Continent> getContinentList() {
    return continentList;
  }


  public void setContinentList(ArrayList<ASCII_Continent> continentList) {
    this.continentList = continentList;
  }


  public int get_continentsRequested() {
    return _continentsRequested;
  }


  public void set_continentsRequested(int _continentsRequested) {
    this._continentsRequested = _continentsRequested;
  }


  public int get_continentBuildCnt() {
    return _continentBuildCnt;
  }


  public void set_continentBuildCnt(int _continentBuildCnt) {
    this._continentBuildCnt = _continentBuildCnt;
  }


  public int[] get_startingArmies() {
    return _startingArmies;
  }


  public void set_startingArmies(int[] _startingArmies) {
    this._startingArmies = _startingArmies;
  }




  /**
   * @deprecated here just to keep errors from appearing.
   *
   * @param foo
   */
  public void set_NumberOfPlayers( int foo) {

    throw new IllegalArgumentException("Should not be calling this.");

  }

  /**
   * @deprecated here just to keep errors from appearing.
   *
   *
   */
  public int get_NumberOfBonusArmies() {

    if (true) throw new IllegalArgumentException("Should not be calling this.");

    return -99999;
  }


  public ArrayList<String> get_connectionsRays() {
    return _connectionsRays;
  }


  public void set_connectionsRays(ArrayList<String> _connectionsRays) {
    this._connectionsRays = _connectionsRays;
  }

}



// redundant. delete later if not needed.
//public int get_height() {
//	return _mapHeight;
//}


//public void set_height(int _height) {
//	this._mapHeight = _height;
//}


//public int get_width() {
//	return _mapWidth;
//}

//
//public void set_width(int _width) {
//	this._mapWidth = _width;
//}



