package technology.sola.rixk.model;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * After a map is built and continents placed, the builder receives the map
 * and builds connections for territories and continents.
 *
 * @author Brint
 * @version Created 3/20/2015
 */
@SuppressWarnings("unused")
public class ASCII_ConnectionsBuilder {

  private static final Logger LOGGER = Logger.getLogger(ASCII_ConnectionsBuilder.class.getName());


  // Constants

  private final String BORDER_CHARACTER = "|";
  private final String SHORELINE_CHARACTER = "#";
  private final String EMPTY_AREA_CHARACTER = ".";
  private final String CONTINENT_CONN_CHAR = "@";

  private final int HORIZONTAL_ORIENTED = 1;
  private final int VERTICAL_ORIENTED = 2;
  private final int UNDEFINED = -999;

  /**
   * Used for handy reference in setting directions (details).
   * NORTH is 0, and directions run clockwise to 7.
   *
   * @author Brint
   */
  public enum CompassDirection {
    NORTH,
    NEAST,
    EAST,
    SEAST,
    SOUTH,
    SWEST,
    WEST,
    NWEST
  }

  // Fields

  /**
   * A map consists of ascii characters representing pieces of the following:
   * continents, continent shore lines, continent connections, territories,
   * territory borders, or default background (i.e oceans).
   */
  private ASCII_RixkMap map;


  // Constructors

  /**
   * Constructor
   *
   * @param map A map with all the continents successfully placed.
   */
  public ASCII_ConnectionsBuilder(ASCII_RixkMap map) {

    LOGGER.setLevel(Level.OFF);
    LOGGER.info("Logger Name: " + LOGGER.getName());

    this.map = map;

    go();
  }

  public ASCII_ConnectionsBuilder() {
  }


  // Methods

  /**
   * Main algorithm starts here.
   */
  private void go() {

    LOGGER.info("enter");

    deriveAllTerritoryConnectionsWithinAllContinents();
    deriveTerritoryConnectionsBetweenContinents();

    LOGGER.info("exit");

  }

  /**
   * Cycle through each continent and make sure it has at least one connection to every
   * other continent, but if not, then start drawing connections.
   */
  public int deriveTerritoryConnectionsBetweenContinents() {

    LOGGER.info("enter");

    final int MAX_CONNECTION_ATTEMPTS = 500;

    ASCII_Continent continent = null;
    CompassDirection sideOrigin = null;
    String location = null;
    int orgRow = this.UNDEFINED;
    int orgCol = this.UNDEFINED;
    String reconResult = null;
    boolean shorelineFound = false;
    int units = this.UNDEFINED;
    String orgTerrName = null;
    String fndTerrName = null;
    int connectionsCnt = 0;
    int connectionAttempts = 0;
    int connectionsOffThisContinent = 0;

    while (!isEveryContWithConnection()) {// while ( cont's not connected )
      continent = pullAContientAtRandom();// pick a cont. at random [continent]
      if (!isContinentWithConnection(continent)) {// if ( cont. has no connections )
        sideOrigin = pickRandomContinentSide();// pick origin side to draw from [NORTH,EAST,SOUTH,WEST]
        location = pickOriginStartingPoint(continent, sideOrigin);// pick starting point on origin side [orgRow,orgCol]
        orgRow = map.cvtAxis(location, "row");
        orgCol = map.cvtAxis(location, "col");
        reconResult = travelConnectionFrom(   // vector recon out from selected side [reconResult]
          "recon", orgRow, orgCol, sideOrigin, this.map.get_mapWidth());
        if (isShorelineFound(reconResult)) { // if ( recon result was another shoreline )

          units = drawConnectionBetweenContinents(sideOrigin, orgRow, orgCol, reconResult);
          fndTerrName = locateFoundTerritoryName(sideOrigin, orgRow, orgCol, units);
          orgTerrName = locateOriginTerritoryName(sideOrigin, orgRow, orgCol); // TODO bm 3/26 check for "n/a" coming back.
          connectionsCnt = addTerritoriesToConnectionsList(orgTerrName, fndTerrName, connectionsCnt);

          connectionsOffThisContinent = continent.get_numberOfConnections();
          continent.set_numberOfConnections(++connectionsOffThisContinent);
          // TODO bm 3/22: Answer: need to increase the found continent's connections.
          // use fndTerrName to deduce the continent name, then inc. its connections too!
          // I think there's a method that does that I've written, but C1.T1 easy to pares.

          LOGGER.config(
            "sideOrigin...." + sideOrigin +
              "orgRow........" + orgRow +
              "orgCol........" + orgCol +
              "units ........" + units +
              "connAttmpts..." + orgTerrName +
              "orgTerrName..." + orgTerrName +
              "fndTerrName..." + fndTerrName);

          System.out.println("connections / attempts: " + connectionsCnt + "," + connectionAttempts);
        }// end if
      }// end if

      if (connectionAttempts == MAX_CONNECTION_ATTEMPTS) {
        throw new IllegalStateException("ERROR: Connection attempts reached: " + connectionAttempts);
      }


    } // end while

    LOGGER.info("exit");


    return connectionsCnt;

// 		3/22 in case i need it
//		LOGGER.config(
//				"sideOrigin...." + sideOrigin +
//				"orgRow........" + orgRow +
//				"orgCol........" + orgCol +
//				"units ........" + units +
//				"orgTerrName..." + orgTerrName +
//				"fndTerrName..." + fndTerrName );
  }

  /**
   * Add the two territories to connections list. [_connectionsList]
   *
   * @param orgTerrName    territory of origin's name
   * @param fndTerrName    the found territory's name
   * @param connectionsCnt how many connections are currently made between continents.
   * @return an updated connections count.
   */
  private int addTerritoriesToConnectionsList(String orgTerrName,
                                              String fndTerrName, int connectionsCnt) {

    map.get_connectionsList().add(orgTerrName + "," + fndTerrName);
    connectionsCnt++;  // count it.
    return connectionsCnt;
  }

  /**
   * Draws the connection between the two territories.
   *
   * @param sideOrigin  the compass direction of travel
   * @param orgRow      continent of origin location
   * @param orgCol      continent of origion location
   * @param reconResult a CSV string location of found shoreline
   * @return how far away in units the other continent is.
   */
  public int drawConnectionBetweenContinents(CompassDirection sideOrigin,
                                             int orgRow, int orgCol, String reconResult) {

    LOGGER.info("enter");

    int endRow;
    int endCol;
    int units;
    endRow = -map.cvtAxis(reconResult, "row"); // get endRow, endCol of that shoreline ...
    endCol = -map.cvtAxis(reconResult, "col"); // ... and negate, since known something found.
    units = Math.abs(orgRow - endRow) + Math.abs(orgCol - endCol);

    travelConnectionFrom(   // draw along same path reconned earlier
      "draw", orgRow, orgCol, sideOrigin, units);// draw connection from original cont. to terr.

    this.map.get_connectionsRays().add(orgRow + "," + orgCol + "," + sideOrigin.ordinal() + "," + units); //e.g. 5,5,0,20

    LOGGER.config("\r\n" +
      "sideOrigin: " + sideOrigin + "\r\n" +
      "orgCol    : " + orgCol + "\r\n" +
      "orgRow    : " + orgRow + "\r\n" +
      "units     : " + units + "\r\n"
    );

    LOGGER.info("exit");
    return units;


  }

  /**
   * Given map coords, infer what territory originated recon
   *
   * @param sideOriginDirection a compass direction of a continent's side.
   * @param orgRow              continent of origin location
   * @param orgCol              continent of origin location
   * @return the origin's territory's full name, or "n/a" if not found.
   */
  private String locateOriginTerritoryName(CompassDirection sideOriginDirection,
                                           int orgRow, int orgCol) {

    //LOGGER.setLevel(Level.OFF);
    LOGGER.info("enter");

    String location;
    int terrRow;
    int terrCol;
    String orgTerrName;

    CompassDirection inv_sideOriginDirection =
      calcInverseCompassDirection(sideOriginDirection); // e.g. WEST --> EAST
    location = travelConnectionFrom(
      "journey", orgRow, orgCol, inv_sideOriginDirection, 1); // back-up into continent one slot to find territory
    terrRow = map.cvtAxis(location, "row"); // now will contain territory point
    terrCol = map.cvtAxis(location, "col");
    orgTerrName = inferTerritoryAt(terrRow, terrCol);// given map coords, infer what territory was found [fndTerrName );

    LOGGER.config("\r\n" +
      "sideOriginDrct:  " + sideOriginDirection + "\r\n" +
      "orgRow:          " + orgRow + "\r\n" +
      "orgCol:          " + orgCol + "\r\n" +
      "inside org terr: " + inv_sideOriginDirection + "\r\n" +
      "location:        " + location + "\r\n" +
      "terrRow:         " + terrRow + "\r\n" +
      "terrCol:         " + terrCol + "\r\n" +
      "orgTerrName:     " + orgTerrName + "\r\n"
    );

    LOGGER.info("exit");
    //LOGGER.setLevel(Level.OFF);

    return orgTerrName;
  }

  /**
   * Given a compass direction, calculate the inverse direction.
   *
   * @param cd a compass direction (e.g. NORTH)
   * @return the inverted of the supplied compass direction (e.g. SOUTH)
   */
  private CompassDirection calcInverseCompassDirection(CompassDirection cd) {

    CompassDirection[] cdv = CompassDirection.values();  // 0-7, clockwise NORTH - NWEST

    int i = ((cd.ordinal() + 12) % 8); // uses magic inverting formula!

    CompassDirection inv_cd = cdv[i]; // spits-out correct enum element

    return inv_cd;
  }

  /**
   * Travel from the contient origin row,col, then to, and inside found continent to locate
   * a territory for connection.
   *
   * @param sideOrigin a compass direction of travel
   * @param orgRow     continent of origin location
   * @param orgCol     continent of origion location
   * @param units      how far away, plus one more to cut into territory within continent.
   * @return the found territory's full name.
   */
  private String locateFoundTerritoryName(CompassDirection sideOrigin, int orgRow, int orgCol, int units) {

    LOGGER.info("enter");

    String location;
    int terrRow;
    int terrCol;
    String fndTerrName;
    location = travelConnectionFrom(   // journey along same path reconned earlier , one more unit in, within found cont.
      "journey", orgRow, orgCol, sideOrigin, units + 1);
    terrRow = map.cvtAxis(location, "row"); // now will contain territory point
    terrCol = map.cvtAxis(location, "col");
    fndTerrName = inferTerritoryAt(terrRow, terrCol);// given map coords, infer what territory was found [fndTerrName );

    LOGGER.config("\r\n" +
      "sideOrigin:      " + sideOrigin + "\r\n" +
      "orgRow:          " + orgRow + "\r\n" +
      "orgCol:          " + orgCol + "\r\n" +
      "location:        " + location + "\r\n" +
      "terrRow:         " + terrRow + "\r\n" +
      "terrCol:         " + terrCol + "\r\n" +
      "fndTerrName:     " + fndTerrName + "\r\n"
    );

    LOGGER.info("exit");

    return fndTerrName;
  }

  /**
   * Given a row and a column on the map, infer what the territory name is at
   * that location
   *
   * @param row a location on the map
   * @param col a location on the map
   * @return The name of a territory (e.g. "C1.T2"), "n/a" if location not within a territory.
   */
  public String inferTerritoryAt(int row, int col) {

    LOGGER.info("enter");

    String terrName = map.get_allTerritoryPointsHashMap().get(row + "," + col);

    if (terrName == null) {
      terrName = "n/a";
    }

    LOGGER.config("\r\n" +
      "row:  " + row + "\r\n" +
      "col:  " + col + "\r\n" +
      "terrName:  " + terrName + "\r\n"
    );

    LOGGER.info("exit");

    return terrName;

  }

  /**
   * Given a CSV location on the map, determine shorline at that location.
   *
   * @param location a CSV location (e.g. "3,2")
   * @return true if shorline, false otherwise.
   */
  public boolean isShorelineFound(String location) {

    LOGGER.info("enter");

    boolean shorelineFound = true;
    int row = this.UNDEFINED;
    int col = this.UNDEFINED;

    if (location.equals("off,map")) { // not if off map, must be checked first!
      shorelineFound = false;
    } else if (row >= 0) {      // not if one ended finding only empty map areas (pos. vals)
      row = map.cvtAxis(location, "row");
      shorelineFound = false;
    } else {              // neg's mean something found ...
      row = map.cvtAxis(location, "row");
      col = map.cvtAxis(location, "col");
      shorelineFound = isCharacter(SHORELINE_CHARACTER, -row, -col);
      if (shorelineFound) { // it is indeed shorline
        if (isShoreLineCorner(-map.cvtAxis(location, "row"), -map.cvtAxis(location, "col"))) {
          shorelineFound = false;  // don't allow corners!
        }
      }
    }

    LOGGER.info("exit");

    return shorelineFound;
  }


  /**
   * corner Checks if row,col holding a known shore line character is in
   * a continent's corner, which would make it not usable for connections.
   *
   * @param row a continent location
   * @param col a continent location
   * @return returns true if corder, false otherwise
   */
  public boolean isShoreLineCorner(int row, int col) {

    boolean isCorner = false;

    String location = row + "," + col;

    ASCII_Continent continent = pullContinentByLocation(location);

    int cTopRow = continent.get_handleRow();
    int cBotRow = continent.get_handleRow() + continent.get_continentHeight() - 1;
    int cLefCol = continent.get_handleCol();
    int cRgtCol = continent.get_handleCol() + continent.get_continentWidth() - 1;

    if (location.equals(cTopRow + "," + cLefCol) ||
      location.equals(cTopRow + "," + cRgtCol) ||
      location.equals(cBotRow + "," + cLefCol) ||
      location.equals(cBotRow + "," + cRgtCol)) {

      isCorner = true;
    }

    return isCorner;
  }

  /**
   * Pull continent by CSV location
   *
   * @param location a CSV format string (e.g. "3,2")
   */
  private ASCII_Continent pullContinentByLocation(String location) {

    HashMap<String, String> shorelineHM = this.map.get_continentShorelinesHashMap(); // deduce from shore line chars
    String contName = shorelineHM.get(location); // e.g. C0
    HashMap<String, String> handlesHM = map.get_contHandlesHashMap(); // now get all cont handle locations.
    int index = Integer.parseInt(contName.substring(1)); // strip off the 'C'
    ASCII_Continent continent = map.getContinentList().get(index); // and pull contient by index.

    return continent;
  }

  /**
   * Pull continent by row and column location.
   *
   * @param row a map location
   * @param col a map location
   */
  private ASCII_Continent pullContinentByLocation(int row, int col) {

    return pullContinentByLocation(row + "," + col);

  }

  /**
   * Given a starting row,col, compass direction (defined clockwise from 0
   * North to 7 Northwest), and how many units to extend, travel along line of
   * characters until something encountered.. Returns a csv string formatted
   * ordered pair of the last location reconned (e.g. "5,8"), or negated
   * ordered pair ("-5,-8") of last location reconned if encountered something
   * at that location, or "off,map" if recon extended off of map. Note
   * that a connection starts from the shore line location, and so does
   * not draw a connecting character at the starting point location.
   *
   * @param cmd         <pre>
   *                               "recon"   - explores and returns a report.
   *                               "journey" - does not stop upon encountering something.
   *                               "draw"    - same as recon, but leaves char trail
   *                               </pre>
   *                    <p>
   *                    character to draw with.
   * @param stRow       where to start looking
   * @param stCol       where to start looking
   * @param cd          a compass direction enum value
   * @param reconUnits how far to look
   * @return a CSV string.
   */
  public String travelConnectionFrom(String cmd, int stRow, int stCol,
                                     CompassDirection cd, int reconUnits) {


    LOGGER.info("enter");

    String reconInfo = "";

    int[] rowOffSets = {-1, -1, 0, +1, +1, +1, 0, -1};
    int[] colOffSets = {0, +1, +1, +1, 0, -1, -1, -1};

    int ptrOffsets = cd.ordinal();

    int row = this.UNDEFINED;
    int col = this.UNDEFINED;
    String mapChar = "";

    int rowOffset = rowOffSets[ptrOffsets];
    int colOffset = colOffSets[ptrOffsets];

    LOGGER.config("st. row, st. col.: " + stRow + "," + stCol);
    LOGGER.config("cd : " + cd.toString() + "  units:" + reconUnits);

    row = stRow;
    col = stCol;
    int i = 0;
    while (i < reconUnits) {

      row = row + rowOffset;
      col = col + colOffset;

      if (map.isOffMap(row, col)) { // are we off map?
        reconInfo = "off,map";

        LOGGER.config("\r\n" +
          "row: " + row + "\r\n" +
          "col: " + col + "\r\n" +
          "off? " + reconInfo + "\r\n" +
          "val: " + "n/a"
        );

        break;
      }

      LOGGER.config("\r\n" +
        "row: " + row + "\r\n" +
        "col: " + col + "\r\n" +
        "off? " + map.isOffMap(row, col) + "\r\n" +
        "val: " + map.peek(row, col)
      );

      mapChar = map.peek(row, col); // get what's at the map point

      if (!cmd.equals("journey")) { // don't quit on encounter when journeying!
        if (!this.isCharacter(EMPTY_AREA_CHARACTER, row, col)) {  // not empty?
          LOGGER.config("non empty area found: " + map.peek(row, col));
          reconInfo = -row + "," + -col; // '-' char gives special notice
          break;
        }
      }
      reconInfo = row + "," + col;  // track that we've been here.

      if (cmd.equals("draw")) {
        map.poke("@", row, col);
      } // draw if you need to.

      i++;
    }

    LOGGER.info("exit");


    return reconInfo;
  }


  /**
   * Given a the compass direction indicating the NESW side of a contient, pick a shoreline point
   * along that continent, but don't allow extreme corners to be picked.
   *
   * @return a location in CSV format (e.g. "8,34").
   */
  public String pickOriginStartingPoint(ASCII_Continent continent, CompassDirection cd) {

    LOGGER.info("enter");

    String location = "";
    int lfCol = this.UNDEFINED;
    int rtCol = this.UNDEFINED;
    int tpRow = this.UNDEFINED;
    int btRow = this.UNDEFINED;
    int rowAxis = this.UNDEFINED;
    int colAxis = this.UNDEFINED;

    switch (cd) {
      case NORTH: // NORTH
        colAxis = calcOriginStartingPointColumn(continent);
        rowAxis = continent.get_handleRow(); // set row to top of continent   :: rowAxis
        break;
      case EAST: // EAST
        rowAxis = calculateOriginStartingPointRow(continent);
        colAxis = continent.get_handleCol() + continent.get_continentWidth() - 1;// get right most col
        break;
      case SOUTH: // SOUTH
        colAxis = calcOriginStartingPointColumn(continent);
        rowAxis = continent.get_handleRow() + continent.get_continentHeight() - 1;// set row to bottom of cont.    :: rowAxis
        break;
      case WEST: // WEST
        rowAxis = calculateOriginStartingPointRow(continent);
        colAxis = continent.get_handleCol(); // get left most col             :: colAxis
        break;
      default: // DEFAULT
        throw new IllegalArgumentException("Improper compass direction: " + cd);


    } // end switch

    location = rowAxis + "," + colAxis;

    if (!this.isCharacter(this.SHORELINE_CHARACTER, rowAxis, colAxis)) { // if it ain't shore line ...
      throw new IllegalStateException("" +
        "ERROR: Area should be shoreline!\r\n" +
        "location     : " + location + "\r\n" +
        "compass dir. :" + cd.toString()
      );
    }

    LOGGER.info("exit");

    return location;
  }

  /**
   * @return a row between the corners of a continent's side.
   */
  public int calculateOriginStartingPointRow(ASCII_Continent continent) {

    LOGGER.info("enter");

    int tpRow;
    int btRow;
    int rowAxis;
    tpRow = continent.get_handleRow() + 1; // get top row, not corner
    btRow = continent.get_handleRow() - 1 + continent.get_continentHeight() - 1;// set bot row, not corner
    rowAxis = (tpRow + (int) (Math.random() * ((btRow - tpRow) + 1)));// generate random number ( tpRow, btRow) : rowAxis

    LOGGER.info("exit");
    return rowAxis;
  }

  /**
   * @return a column between the corners of a continent's side.
   */
  public int calcOriginStartingPointColumn(ASCII_Continent continent) {

    LOGGER.info("enter");

    int lfCol;
    int rtCol;
    int colAxis;
    lfCol = continent.get_handleCol() + 1; // get left most col, not corner :: lfCol
    rtCol = continent.get_handleCol() - 1 + continent.get_continentWidth() - 1;// get right most col, ditto	 :: rtCol
    colAxis = (lfCol + (int) (Math.random() * ((rtCol - lfCol) + 1))); // generate random number ( lfCol, rtCol) : colAxis

    LOGGER.info("exit");
    return colAxis;
  }

  /**
   * Returns a compass direction of the side of a continent.
   *
   * @return enum compass direction object of NORTH, SOUTH, EAST, or WEST.
   */
  public CompassDirection pickRandomContinentSide() {

    int n = 2 * (int) (Math.random() * 4);  // 0..3 * 2 yeilds 0,2,4,5

    CompassDirection[] compDir = CompassDirection.values();

    return compDir[n];
  }

  /**
   * Checks that there is at least one connection route from every continent
   * to every other continent.
   *
   * @param name the name of a continent
   * @return true if has a connection character attached to it, false
   * otherwise.
   */
  public boolean isContinentWithConnection(ASCII_Continent continent) {

    LOGGER.info("enter");
    boolean foo = false;

    foo = !(map.checkPerimeter(    // returns true if has empty perimeter
      continent,
      continent.get_handleRow(),
      continent.get_handleCol()
    ));

//		// TODO bm 3/22 Question: look at this line below to see why it does't work. seems to not be updated.
//		foo = ( continent.get_numberOfConnections() > 0);
//
//		System.out.println(continent.get_continentName() + " has " +
//				continent.get_numberOfConnections() + " connections. empty per? :"+ !foo );

    LOGGER.info("enter");

    return foo;
  }

  /**
   * Checks that there is at least one connection on every continent (details)
   * That all have one connection does not mean there is a path from any one
   * continent to any other continent. That requires a stronger check.
   *
   * @return a true if every continent has at least one connection.
   */
  public boolean isEveryContWithConnection() {

    LOGGER.info("enter");

    boolean areAllConnected = true;
    int numContsAvail = map.get_continentPlacementCnt();

    for (int i = 0; i < numContsAvail; i++) {
      areAllConnected = this.isContinentWithConnection(map.getContinentList().get(i));
      LOGGER.config("all continents have at least one connection.");

      if (!areAllConnected) {
        LOGGER.config("continent " + map.getContinentList().get(i).get_continentName() +
          " is not connected to any other continent");
        areAllConnected = false;
        break;
      }
    }

    if (numContsAvail == 1) {
      throw new IllegalArgumentException("ERROR: can't connect with just one continent!");
    }

    LOGGER.info("exit");

    return areAllConnected;
  }

  /**
   * Pulls a continent from the map at random and returns it.
   *
   * @return a continent object known to be on the map.
   */
  public ASCII_Continent pullAContientAtRandom() {

    LOGGER.info("enter");

    int numContsAvail = map.get_continentPlacementCnt();
    int n = (int) (Math.random() * numContsAvail);

    LOGGER.config("continent " + map.getContinentList().get(n).get_continentName() +
      " selected. list item#:" + n);

    LOGGER.info("exit");

    return map.getContinentList().get(n);
  }


  /**
   * Cycle through each continent on the map and for every group of territories within a continent,
   * derive their connections based on their proximity to one another.
   */
  public void deriveAllTerritoryConnectionsWithinAllContinents() {

    LOGGER.info("enter");

    ASCII_Continent continent = null;
    int orientation = UNDEFINED;

    for (int iContinents = 0; iContinents < map.get_continentPlacementCnt(); iContinents++) {// loop thru continents
      continent = map.getContinentList().get(iContinents);  // pull a continent
      orientation = continent.get_orientation();// get common orientation of all terr's on this continent.
      for (int iTerr = 0; iTerr < continent.get_numberOfTerritories(); iTerr++) {// loop thru all its territories
        findConnectingTerritory(continent, orientation, iTerr);
      } // end territories loop
    } // end continents loop

    LOGGER.info("exit");
  }

  /**
   * Pull a territory from the continent, and use its handles to find another, connecting territory, whether by
   * looking south or east, depending on orientation of territories in continent.
   *
   * @param continent   a supplied continent on the map.
   * @param orientation either horizontal or vertical.
   * @param iTerr       an index to the territory within the supplied continent.
   */
  public void findConnectingTerritory(ASCII_Continent continent, int orientation, int iTerr) {


    LOGGER.info("enter");

    ASCII_Territory territory;
    String[] row_col_array;
    String foundTerrName;
    String originTerrNam;
    int row;
    int col;

    territory = continent.get_territories().get(iTerr); // pull a territory on that continent
    if (orientation == this.HORIZONTAL_ORIENTED) { // if HORZ,
      /* move down from terr. handle location until border or shore line found. */
      row_col_array = reconSouthForTerritoryBorderOrShoreline(territory.get_handleRow(), territory.get_handleCol()).split(",");
      row = Integer.parseInt(row_col_array[0]); // get row, col vals to work with.
      col = Integer.parseInt(row_col_array[1]);
      if (isCharacter(this.BORDER_CHARACTER, row, col)) {// found a border?
        foundTerrName = map.get_allTerritoryPointsHashMap().get((row + 1) + "," + col);// grab location immediately south of the border and derive territory name
        placeTerritoryConnectionIntoConnectionsList(territory, foundTerrName);
      } // end if
    } else { // else must be VERT.
      // move east from terr. location until border or shore line found.
      row_col_array = reconEastForTerritoryBorderOrShoreline(territory.get_handleRow(), territory.get_handleCol()).split(",");
      row = Integer.parseInt(row_col_array[0]); // get row, col vals to work with.
      col = Integer.parseInt(row_col_array[1]);
      if (isCharacter(this.BORDER_CHARACTER, row, col)) {// found a border?
        foundTerrName = map.get_allTerritoryPointsHashMap().get(row + "," + (col + 1));// grab location immediately east of the border and derive territory name
        placeTerritoryConnectionIntoConnectionsList(territory, foundTerrName);
      } // end if
    } // end if..else

    LOGGER.info("exit");


  } // end method

  /**
   * Place newly found connections between two territories into the territory connections list.
   *
   * @param territory     the territory of origin
   * @param foundTerrName the name of the new territory found connected to the territory of origin.
   */
  public void placeTerritoryConnectionIntoConnectionsList(
    ASCII_Territory territory, String foundTerrName) {

    LOGGER.info("enter");

    String originTerrNam = territory.get_territoryName();

    String key_value = originTerrNam + "," + foundTerrName;  // from origin to found
    map.get_connectionsList().add(key_value); // add  to territory connections list.

    LOGGER.config("Placing: " + "\r\n" +
      originTerrNam + "," + foundTerrName + "\r\n" +
      "current map: " + map.get_connectionsList().toString()
    );
    LOGGER.info("exit");

  }


  /**
   * Given a starting row and column location in a territory, move south along the rows until a
   * border or shoreline character is detected.
   *
   * @return location a CSV string ( e.g. "3,5") showing of the location where the character
   * is found.
   */
  public String reconSouthForTerritoryBorderOrShoreline(int StRow, int col) {

    LOGGER.info("enter");

    int row = StRow;
    boolean haltedAtTerritorylimit = true;

    do {
      row++;
      haltedAtTerritorylimit = (isCharacter(BORDER_CHARACTER, row, col)) || this.isCharacter(SHORELINE_CHARACTER, row, col);
    } while (!haltedAtTerritorylimit);

    LOGGER.config(isCharacter(BORDER_CHARACTER, row, col) ? "border found" : "shore line found");
    LOGGER.info("exit");

    return (row + "," + col);
  }

  /**
   * Given a starting row and column location in a territory, move east along the cols until a
   * border or shoreline character is detected.
   *
   * @return location a CSV string ( e.g. "3,5") showing of the location where the character
   * is found.
   */
  public String reconEastForTerritoryBorderOrShoreline(int row, int StCol) {

    LOGGER.info("enter");

    int col = StCol;
    boolean haltedAtTerritorylimit = true;

    do {
      col++;
      haltedAtTerritorylimit = (isCharacter(BORDER_CHARACTER, row, col)) || isCharacter(SHORELINE_CHARACTER, row, col);
    } while (!haltedAtTerritorylimit);

    LOGGER.config(isCharacter(BORDER_CHARACTER, row, col) ? "border found" : "shore line found");

    LOGGER.info("exit");

    return (row + "," + col);
  }


  /**
   * Returns a true if matrix location contains the indicated character type,
   * false otherwise (details).
   * <p>
   * Example: boolean flag = isCharacter(SHORELINE_CHARACTER,23,76)
   *
   * @param row a map row location
   * @param col a map col location
   * @return a true if matrix location contains a territory shore line character,
   * false otherwise
   */
  public boolean isCharacter(String character, int row, int col) {

    String[][] tempmap = map.get_mapMatrix();
    return (tempmap[row][col].equals(character));

  }


  /**
   * Given a compass ordinal direction, return a compass enum name.<p>
   * Example: 0 --> NORTH, 1-->NEAST, etc.
   *
   * @return
   */
  public CompassDirection calcCompassDirection(int ordinalDirection) {

    CompassDirection[] compDirNames = CompassDirection.values();  // 0-7, clockwise NORTH - NWEST

    return compDirNames[ordinalDirection];
  }


  // Getters & Setters

  public ASCII_RixkMap getMap() {
    return map;
  }


  public void setMap(ASCII_RixkMap map) {
    this.map = map;
  }
}

