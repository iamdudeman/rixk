package technology.sola.rixk.model;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * The ascii play object receives information so as to completely reconstruct an ascii map.
 * note that this object also contains in it the whole ASCII rixk map object too.
 *
 *
 * @author Brint
 *
 * @version Created 3/23/15
 *
 */
public class ASCIIPlayObject {

  private static final Logger LOGGER = Logger.getLogger(ASCIIPlayObject.class.getName());

  /**
   * Holds the map name, rows, and cols. E.g., "testMap, 40,80"
   */
  private String _asciiMapInfo = "";

  /**
   * Holds the tuples showing row and col sizes, territory orientations of continents, and number of territories contained.
   * <p>E.g. "C0=20,12,1,3 ; C1=23,21,1,3 ..."
   */
  private String _continentSizesAndOriensKVs = "";

  /**
   * Holds all internal continent id's and their handle location.
   * <p>E.g., "C0=14,4 ; C1=2,14 ..."
   */
  private String _contHandlesHashMapKVs = "";

  /**
   *  Holds all ordered pairs describing the row and col locations of territory to territory connections.
   *  <p> E.g. "C0.T1,C1.T4 ; C3.T2,C5.T0 ..."
   */
  private String _connectionsListKVs = "";

  /**
   * Holds all 4-tuples which supply information for continent connections.
   *
   * "connectionsRays" : 5,5,0,20 ; 12,12,2,12 ; ...
   * Which legend is: [stRow,stCol,compassDirection,length]
   */
  private String _connectionsRays = "";

  /**
   * A fully reconstructed ascii map.
   */
  private ASCII_RixkMap _asciimap = new ASCII_RixkMap();

  private int _numberOfContientsToPlace = 0;

//	/**
//	 * Holds information about how many bonus armies a player
//	 * gets, depending on number of players.  Elements 0-1 are
//	 * undefined, since rixk requires at least 2 players.
//	 */
//	private int[] _startingArmies;
//
//	private ASCII_Continent continent;
//

  public ASCIIPlayObject() {
    LOGGER_classSettings();

  }

  /**
   * Constructor used to rebuild ASCII_RixkMaps.
   *
   * @param asciiMapInfo
   *            Holds the map name, rows, and cols, and then ordered
   *            pairs of player #, bonus army values for 2 - 6 players
   *            <p>E.g., "testMap, 40,80,2,30,3,40,4,50,5,60,6,70"
   * @param continentSizesHashMapKVs
   *           Holds the 4-tuples showing row and col sizes, territory orientations of continents,
   *           and number of territories contained, all in order from C0 - Cn.
   *           <p>E.g. "C0=20,12,1,3 ; C1=23,21,1,3 ..."
   * @param contHandlesHashMapKVs
   *            Holds all internal continent id's and their handle location.
   *            <p>E.g., "C0=14,4 ; C1=2,14 ..."
   * @param connectionsListKVs
   *            Holds all ordered pairs describing the row and col locations
   *            of territory to territory connections.
   *            <p>E.g. "C0.T1,C1.T4 ; C3.T2,C5.T0 ..."
   * @param connectionsRays
   *            Holds all 4-tuples which supply information for continent connections.
   *           <p> e.g. 5,5,0,20 ; 12,12,2,12 ; ...
   *            Which legend is: [stRow,stCol,compassDirection,length]
   */
  public ASCIIPlayObject(
    String asciiMapInfo,
    String continentSizesAndOriensKVs,
    String contHandlesHashMapKVs,
    String connectionsListKVs,
    String connectionsRays)
  {

    LOGGER_classSettings();

    this._asciiMapInfo 					= asciiMapInfo;
    this._continentSizesAndOriensKVs 	= continentSizesAndOriensKVs;
    this._contHandlesHashMapKVs			= contHandlesHashMapKVs;
    this._connectionsListKVs 			= connectionsListKVs;
    this._connectionsRays 				= connectionsRays;

    go();

  }

  /**
   * Set logger stuff everything in this class.
   */
  private void LOGGER_classSettings() {
//		LOGGER.setLevel(Level.CONFIG);
    LOGGER.info("Logger Name: "+LOGGER.getName());
  }



  /**
   * Main algorithm starts here.
   */
  public void go() {

    LOGGER.info("enter");


    initializeAsciiMapInfo();
    inferNumberOfContinentsToPlace();
    buildEmptyMap();
    placeAllContinents();
    placeAllConnections();

    LOGGER.info("exit");
  }


  /**
   * Pulls all the info from the first asciiMapInfo parameter and initializes standard
   * map attributes.
   */
  public void initializeAsciiMapInfo() {

    LOGGER.info("enter");

    String[] values = null;

    values = _asciiMapInfo.split(",");// pull out into array

    this._asciimap.set_name( values[0] );	// name
    this._asciimap.set_mapHeight( Integer.parseInt(values[1].trim()) ); // height
    this._asciimap.set_mapWidth( Integer.parseInt(values[2].trim()) ); //  width

    int j = 1;
    int[] saArray = this._asciimap.get_startingArmies(); // 1st two elements ignored.
    for (int i = 0; i < 10; i +=2 ) { // n,r,c,2,x,3,x,4,x,5,x,6,x
      j++;
      saArray[ j ] = Integer.parseInt(values[4 + i].trim());
    }
    this._asciimap.set_startingArmies( saArray);

    LOGGER.info("exit");

  }

  /**
   * Pulls info from the _continentSizesAndOriensKVs parameter
   * and sets the number of continents to be placed attribute
   * in the ascii play object.
   */
  public void inferNumberOfContinentsToPlace() {

    LOGGER.info("enter");

    String[] values = _continentSizesAndOriensKVs.split(";"); // pull out to count.
    _numberOfContientsToPlace = values.length;

    LOGGER.info("exit");

  }

  /**
   * Use the ascii maps own parameters, set earlier, to build itself.
   */
  public void buildEmptyMap() {

    LOGGER.info("enter");

    int height = _asciimap.get_mapHeight();
    int width  = _asciimap.get_mapWidth();

    this._asciimap.set_mapMatrix(new String[height][width]);
    _asciimap.errorCheckMapParameters();
    _asciimap.fillMapWithPlaceHolderCharacter();

    LOGGER.info("exit");

  }

  /**
   * Place all the continents on the ascii map.
   */
  public void placeAllContinents() {

    LOGGER.info("enter");

    ASCII_Continent continent = null;

    int terrs = -1;
    int ori	  = -1;
    int ht    = -1;
    int wd    = -1;
    int rowHandle = -1;
    int colHandle = -1;

    for (int i = 0; i< this._numberOfContientsToPlace; i++ ) {// loop thru number of continents to place
      continent = new ASCII_Continent(); // make a new continent object.

      /* set the 4 attributes, as per quickCont() in test  area */
      continent.set_continentName("C"+ i); // uses internal name only.

      ht        = getContinentInfo(i,"height");
      wd        = getContinentInfo(i,"width");
      ori       = getContinentInfo(i,"orientation");
      terrs     = getContinentInfo(i,"territories");
      rowHandle = getContinentInfo(i, "row handle");
      colHandle = getContinentInfo(i, "col handle");

      continent.set_continentHeight(ht);
      continent.set_continentWidth(wd);
      continent.set_numberOfTerritories(terrs);
      continent.set_orientation(ori); // 1 = horz, 2 = vert
      continent.set_handleRow(rowHandle);
      continent.set_handleCol(colHandle);

      continent.calcWidthHeightOfEachTerritory();
      continent.establishHandlesForTerritories();
      continent.set_continentMatrix( new String[ht][wd] );

      continent.fillContinentWithPlaceHolderSymbol(); // fill with placeholder symbol (get from map attribute)
      continent.fillPerimeterWithEdgeSymbol(); // fill perimeter with shoreline symbol (get from map attribute)

      continent.createTerritories();// have continent create its own territories
      continent.placeTerritoriesIntoContinent(); // have continent place its own territories onto itself.

      LOGGER.info(continent.toString2());
      LOGGER.info("\r\nnow calling _asciimap object.");

      _asciimap.placeContinentAtMapLocation(continent, rowHandle, colHandle);// place continent at map location handle_row, handle_col

      LOGGER.info("exit");

    }// end loop

  }

  /**
   * Given an index and requested attribute, return the value of that attribute.
   *
   * @param attribute  An attribute of one of these: "height", "width", "orientation" or "territories"
   * @param index the index of continent, where 0 is first one in the paramater string _continentSizesAndOriensKVs
   *
   * @return the value of the attribute requested.
   */
  private int getContinentInfo(int index, String attribute) {

    LOGGER.info("enter");

    String[] allContValues = _continentSizesAndOriensKVs.split(";"); // e.g. [C0=20,12,1,3][...] etc.
    String[] oneContValueKV = allContValues[index].split("=");  // [C0][20,12,1,3]
    String[] contAttributeValues = oneContValueKV[1].split(",");     // [20,12,1,3]   height,width,orient,# terrs

    String[] allContHandleKVs = _contHandlesHashMapKVs.split(";"); // [C0=1,1][C1=1,19]
    String[] oneContHandleKV = allContHandleKVs[index].split("="); // [C0][1,1]
    String[] contHandles = oneContHandleKV[1].split(","); // [1][1]



    int attributeValue = -1;

    switch (attribute) {
      case "height":
        attributeValue = Integer.parseInt(contAttributeValues[0].trim());
        break;
      case "width":
        attributeValue = Integer.parseInt(contAttributeValues[1].trim());
        break;
      case "orientation":
        attributeValue = Integer.parseInt(contAttributeValues[2].trim());
        break;
      case "territories":
        attributeValue = Integer.parseInt(contAttributeValues[3].trim());
        break;
      case "row handle":
        attributeValue = Integer.parseInt(contHandles[0].trim());
        break;
      case "col handle":
        attributeValue = Integer.parseInt(contHandles[1].trim());
        break;
      default:
        throw new IllegalArgumentException("ERROR: Unknown attribute supplied: " + attribute);
    }

    LOGGER.info("exit");

    return attributeValue;

  }

  /**
   * Place all the connections between continents
   * connection format: "C0.T0,C0.T1, C1.T0,C1.T1, C2.T0,C2.T1, C2.T1,C2.T2, ... "
   */
  public void placeAllConnections() {

    LOGGER.info("enter");

    String[] allConnValues = this._connectionsListKVs.split(";"); // e.g. to [C0.T1,C1.T0][C2.T1,C3.T0] etc.
    ArrayList<String> connectionsList = new ArrayList<>(); // make a holder to collect (and watch)

    for (int i = 0; i < allConnValues.length; i++) { // run thru 'em all
      connectionsList.add(allConnValues[i]);		// pack 'em one by one.
    }

    _asciimap.set_connectionsList(connectionsList); // send 'em over.

    drawConnectionsFromList(_connectionsRays); // draw them on map.

    LOGGER.info("exit");
  }

  /**
   * Given string as follows:<p>
   * "connectionsRays" : 5,5,0,20 ; 12,12,2,12 ; ...
   *
   * which legend is: [stRow,stCol,compassDirection,length]
   * connectionsRays
   *
   */
  public void drawConnectionsFromList(String connectionsRays) {

    LOGGER.info("enter");

    String cmd 		= "draw";
    int stRow 		= -1;
    int stCol 		= -1;
    int reconUnits 	= -1;
    ASCII_ConnectionsBuilder.CompassDirection cd = null;

    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder();
    acb.setMap(_asciimap); // give it the map we've been working on

    String[] allRays = connectionsRays.split(";"); // split it into array by ';'  [allRays]
    for (int i = 0; i < allRays.length; i++) {// loop thru number you have
      String[] indRay = allRays[i].split(","); // split it into array by ','  [indRay]

      stRow = Integer.parseInt(indRay[0].trim());
      stCol = Integer.parseInt(indRay[1].trim());
      cd = acb.calcCompassDirection( Integer.parseInt(indRay[2]) );
      reconUnits = Integer.parseInt(indRay[3].trim());

      acb.travelConnectionFrom(cmd, stRow, stCol, cd, reconUnits);
    }// end loop

    LOGGER.info("exit");
  }

  /**
   * Update this information inside the ascii map as soon as i get this.
   * When the modification happens, it will immediately be available
   * for access from the toString() method.
   *
   * Given a territory name, place player name and armyCount at the
   * appropriate display spot within the territory.
   *
   * @param terrName the internal string name of a territory (e.g. "C3.T12")
   * @param playerName a string name, preferably short.
   * @param armyCount the number of armies to be shown in the territory.
   */
  public void update(String terrName, String playerName, int armyCount) {

    LOGGER.info("enter");

    ASCII_Continent continent = null;
    ASCII_Territory territory = null;

    String[] values = terrName.split(Pattern.quote("."));
    String continentName = values[0];
    String territoryName = values[1];

    continent = this._asciimap.pullAContinentByName(continentName);
    territory = continent.pullTerritoryByName(territoryName); // pull the territory out of the continent [territory]

    territory.set_playerNameWhoControls(playerName);
    territory.set_numberOfArmies(armyCount);

    territory.dropInString(playerName, 1, 0);
    territory.dropInString(armyCount+"",  2, 0);

    continent.placeTerritoryIntoContient(territory) ;// put it back into the continent

    _asciimap.drawContinentOntoMapLocation(continent,
      continent.get_handleRow(), continent.get_handleCol());// put the continent back into the map.

    LOGGER.info("exit");
  }

  /**
   * Return the the ascii version of the risk map.
   */
  public String toString() {

    String str = this._asciimap.toString();

    return str;
  }


  // Getters & Setters

  public String get_asciiMapInfo() {
    return _asciiMapInfo;
  }

  public void set_asciiMapInfo(String _asciiMapInfo) {
    this._asciiMapInfo = _asciiMapInfo;
  }

  public String get_continentSizesAndOriensKVs() {
    return _continentSizesAndOriensKVs;
  }

  public void set_continentSizesAndOriensKVs(String _continentSizesAndOriensKVs) {
    this._continentSizesAndOriensKVs = _continentSizesAndOriensKVs;
  }

  public String get_contHandlesHashMapKVs() {
    return _contHandlesHashMapKVs;
  }

  public void set_contHandlesHashMapKVs(String _contHandlesHashMapKVs) {
    this._contHandlesHashMapKVs = _contHandlesHashMapKVs;
  }

  public String get_connectionsListKVs() {
    return _connectionsListKVs;
  }

  public void set_connectionsListKVs(String _connectionsListKVs) {
    this._connectionsListKVs = _connectionsListKVs;
  }

  public ASCII_RixkMap get_asciimap() {
    return _asciimap;
  }

  public void set_asciimap(ASCII_RixkMap _asciimap) {
    this._asciimap = _asciimap;
  }

  public int get_numberOfContientsToPlace() {
    return _numberOfContientsToPlace;
  }

  public void set_numberOfContientsToPlace(int _numberOfContientsToPlace) {
    this._numberOfContientsToPlace = _numberOfContientsToPlace;
  }

  public String get_connectionsRays() {
    return _connectionsRays;
  }

  public void set_connectionsRays(String _connectionsRays) {
    this._connectionsRays = _connectionsRays;
  }




}

