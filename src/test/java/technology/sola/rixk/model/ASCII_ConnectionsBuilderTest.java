package technology.sola.rixk.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ASCII_ConnectionsBuilderTest {

  public enum COMPASS {
    NORTH ,
    NEAST,
    EAST,
    SEAST,
    SOUTH,
    SWEST,
    WEST,
    NWEST;

  }


  // Piece in skeleton methods as needed.
  // 	    https://docs.google.com/document/d/13J8sVtLn6S5pP0mYavYV_HDUusMJBlDS0ZYHtHVl0Kw/edit?usp=sharing

  /**
   * Build contients.  Visually observe that all territory connections are
   * correct.
   */
  @Test
  public void test_deriveAllTerritoryConnectionsWithinAllContinents() {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 2;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, true); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and it a test map

    acb.deriveAllTerritoryConnectionsWithinAllContinents();


    this.toConsole( testMap.get_connectionsList().toString() );

    assertTrue( testMap.get_connectionsList().size() > 0);


  }

  /**
   * Grab a continent and see if it's got a connection.
   */
  @Test
  public void test_isContinentWithConnection() {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 2;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, true); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and it a test map

    ASCII_Continent continent = acb.getMap().getContinentList().get(0); // pull first continent in list

    this.toConsole( "continent "+continent.get_continentName()+" connected? "+acb.isContinentWithConnection(continent) );

    assertFalse( acb.isContinentWithConnection(continent) );

  }

  @Test
  public void test_isEveryContWithConnection() {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 2;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, true); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and it a test map

    assertFalse( acb.isEveryContWithConnection() );

  }

  @Test
  public void test_pullAContientAtRandom() {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 2;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, true); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and it a test map

    ASCII_Continent continent = acb.pullAContientAtRandom(); // pull it

    assertTrue( !(continent == null), "continent not found!" );

    toConsole( continent.toString()); // show it

  }

  /**
   * Review of handy enum stuff.
   */
  @Test
  public void enumLook() {

    COMPASS compass;
    compass = COMPASS.NORTH;

    // if..else and switch treat it nice too.
    if (compass == COMPASS.WEST) {
      toConsole("yes, compass == NORTH");
    } else {
      toConsole("no, compass not == NORTH");
    }

    toConsole( ".valueOf() "+ COMPASS.valueOf("NORTH") ); // gives NORTH
    toConsole(".ordinal() "+ COMPASS.NWEST.ordinal() );  // gives 7

    COMPASS[] foo =  COMPASS.values();
    toConsole( foo[1].toString() ); // gives  NEAST compass object back


  }

  @Test
  /**
   * give me a random compass direction back
   */
  public void test_pickRandomContinentSide() {

    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( );

    toConsole( acb.pickRandomContinentSide() +"");

  }


  /**
   *
   * put a continent down, observe that origin points are correctly picked.
   */
  @Test
  public void test_pickOriginStartingPoint( ) {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 2;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, true); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and it a test map
    ASCII_Continent continent = acb.getMap().getContinentList().get(0); // pull first continent in list


    //CompassDirection cd = acb.pickRandomContinentSide(); // randomly pick a side
    ASCII_ConnectionsBuilder.CompassDirection cd = ASCII_ConnectionsBuilder.CompassDirection.WEST;  // set a side

    String location = acb.pickOriginStartingPoint(continent, cd);

    toConsole( location );

  }


  /**
   *
   * Put down a map and set a compass direction. see what happens.
   *
   * Given a starting row,col, compass direction (defined clockwise from 0
   * North to 7 Northwest), and how many units to extend, recon a line of of
   * characters until something encountered.. Returns a csv string formatted
   * ordered pair of the last location reconned (e.g. "5,8"), or negated
   * ordered pair ("-5,-8") of last location reconned if encountered something
   * at that location, or "off,map" if recon extended off of map.
   */
  @Test
  public void test_travelConnectionFrom () {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 2;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, true); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and it a test map

    ASCII_Continent continent = acb.getMap().getContinentList().get(1); // pull first continent in list
    ASCII_ConnectionsBuilder.CompassDirection side = ASCII_ConnectionsBuilder.CompassDirection.NORTH;  					// set a side
    String location = acb.pickOriginStartingPoint(continent, side);	// pick a origin st. pnt.

    int stRow = testMap.cvtAxis(location, "row");
    int stCol = testMap.cvtAxis(location, "col");
    ASCII_ConnectionsBuilder.CompassDirection cd = ASCII_ConnectionsBuilder.CompassDirection.NWEST;
    int reconUnits = 10;

    String reconInfo = acb.travelConnectionFrom("recon", stRow, stCol, cd, reconUnits);

    toConsole ( testMap.toString());
    toConsole( reconInfo );

  }

  /**
   *  Slap two manually placed continents down and verify by observation that
   *  line is drawn correctly.
   */
  @Test
  public void test_travelConnectionFrom2() {

    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 0;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, false); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and give it the test map

    // place first cont.
    ASCII_Continent contA = quickCont("C0",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contA, 1, 1);
    // place second cont.
    ASCII_Continent contB = quickCont("C1",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contB, 1, 26);


    toConsole ( "before:\r\n" + testMap.toString() );


    ASCII_ConnectionsBuilder.CompassDirection side = ASCII_ConnectionsBuilder.CompassDirection.EAST; 	// set a side to draw from
    String location = acb.pickOriginStartingPoint(contA, side);	// pick a origin st. pnt.

    int stRow = testMap.cvtAxis(location, "row");
    int stCol = testMap.cvtAxis(location, "col");
    ASCII_ConnectionsBuilder.CompassDirection drawDirection = ASCII_ConnectionsBuilder.CompassDirection.NEAST;  // draw moving this direction
    int reconUnits = 15;							// for up to this many units

    String reconInfo = acb.travelConnectionFrom("draw", stRow, stCol, drawDirection, reconUnits);

    toConsole ( "after:\r\n" + testMap.toString() );
    toConsole( reconInfo );

  }

  /**
   * Put a couple of contients on and verify by observation that
   * the thing works by changing values of row,col.
   */
  @Test
  public void test_inferTerritoryAt() {

    int height 		= 22;
    int width 		= 47;
    int numToPlace  = 0;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, false); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and give it the test map

    // place first cont.
    ASCII_Continent contA = quickCont("C0",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contA, 1, 2);
    // place second cont.
    ASCII_Continent contB = quickCont("C1",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contB, 1, 26);

    int row = 2;
    int col = 2;

    String terrName = acb.inferTerritoryAt(row ,col);
    toConsole( terrName );
    toConsole ( testMap.toString() );

  }

  /**
   * Setup a couple of continents on a map, then see if a connection gets made,
   * and if everything is subsequenlty updated to show it.
   */
  @Test
  public void test_deriveTerritoryConnectionsBetweenContinents() {

    // SETUP
    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 0;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, false); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and give it the test map

    // place first cont.
    ASCII_Continent contA = quickCont("C0",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contA, 1, 1);
    // place second cont.
    ASCII_Continent contB = quickCont("C1",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contB, 1, 26);
    testMap.set_continentsRequested(2); // required b/c naked constructor used.
    testMap.set_continentPlacementCnt(2); // required b/c manually placed by human.

    toConsole ( "before:\r\n" + testMap.toString() );

    // TEST
    int connectionsCount = acb.deriveTerritoryConnectionsBetweenContinents();

    // OBSERVE
    toConsole ( testMap.toString() );
    toConsole( "connections count: " + connectionsCount );


  }

  /**
   * Put two contients on the map, and then observe that looking at
   * shore line characters in all for corners returns a false, otherwise
   * a true.
   */
  @Test
  public void test_isShoreLineCorner() {

    // SETUP
    int height 		= 47;
    int width 		= 47;
    int numToPlace  = 0;
    ASCII_RixkMap testMap = quickMap(numToPlace, height, width, false); // make a map
    ASCII_ConnectionsBuilder acb = new ASCII_ConnectionsBuilder( ); // and a builder
    acb.setMap(testMap); // and give it the test map

    // place first cont.
    ASCII_Continent contA = quickCont("C0",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contA, 1, 1);
    // place second cont.
    ASCII_Continent contB = quickCont("C1",2,2,20,18,false);
    testMap.placeContinentAtMapLocation(contB, 1, 28);
    testMap.set_continentsRequested(2); // required b/c naked constructor used.
    testMap.set_continentPlacementCnt(2); // required b/c manually placed by human.

    //OBSERVE
    toConsole ( testMap.toString() );

    //TEST
    boolean isCorner = acb.isShoreLineCorner(1,45);
    assertTrue(isCorner, "ERROR: row,col is not corner shore line character");


  }

  /**
   * Setup 2 continents on a map automatically, then see if connections get made,
   * and if everything is subsequenlty updated to show it.
   */
  @Test
  public void test_deriveTerritoryConnectionsBetweenContinents_auto() { // TODO bm x/xx main test to run for maps.

    // SETUP
    String name     = "test";
    int height 		= 50;
    int width 		= 100;
    int numToPlace  = 3;

    // TEST
    ASCII_RixkMap testMap = new ASCII_RixkMap(name,height,width,numToPlace); // normal map

    // OBSERVE
    toConsole ( testMap.toString() );

    toConsole ( "asciiMapInfo:            "+ name+","+testMap.get_mapHeight()+","+testMap.get_mapHeight()+",2,30,3,40,4,50,5,60,6,70");
    toConsole ( "cont szes, orent, terrs: "+ calc_contHandlesHashMapKVs (testMap)); //" E.g. C0=20,12,1,3 ; C1=23,21,1,3 ..." );
    toConsole ( "contHandlesHashMapKVs:   " + testMap.get_contHandlesHashMap().toString() );
    toConsole ( "connectionRays:          " + testMap.get_connectionsRays().toString() );
    toConsole ( "connectionsListKVs:      " +testMap.get_connectionsList().toString() );






//		apo.set_asciiMapInfo("testMap,22,50,2,30,3,40,4,50,5,60,6,70");  // name, rows, cols, player cnt, player army bonus
//		apo.set_continentSizesAndOriensKVs("C0=20,18,1,2 ; C21=20,18,1,2"); // rows,cols,orien, teritories
//		apo.set_contHandlesHashMapKVs("C0=1,1 ; C1=1,26"); // handles for each continent.
//		apo.set_connectionsRays("2,18,2,7");
//		apo.set_connectionsListKVs("C0.T1,C1.T0");
  }

  private String calc_contHandlesHashMapKVs (ASCII_RixkMap testMap) {

    int numConts = testMap.getContinentList().size();
    String str = "";
    ASCII_Continent continent = null;

    for (int i = 0 ; i < numConts ; i++) {
      continent = testMap.getContinentList().get(i);
      str += (" ; " + "C"+ i +"," +
        continent.get_continentHeight()+"," +
        continent.get_continentWidth() +"," +
        continent.get_orientation()+"," +
        continent.get_numberOfTerritories()
      );
    }

    str = str.substring(3);

    return str;
  }



	/*
	-----------------------------------------
	            Utility methods
	-----------------------------------------
	*/

  /**
   * Create a continent for use. No map or lists or anything are done to the map, so
   * do those yourself.
   * <p>
   * To use: <code> ASCII_Continent cont = quickCont("C0",2,2,25,18,false); </code>
   * <p>
   * To place: <code> map.placeContinentAtMapLocation(cont, handleRow, handleCol) </code>
   *  to add it to the map's continent list.
   *
   * @param toConsole if true, send it to the console for viewing.
   * @return a fully made continent object
   */
  public ASCII_Continent quickCont(String name, int terrs, int ori, int ht, int wd, boolean toConsole) {

    ASCII_Continent continent = new ASCII_Continent();
    continent.set_continentName(name);
    continent.set_numberOfTerritories(terrs);
    continent.set_orientation(ori); // 1 = horz, 2 = vert
    continent.set_continentHeight(ht);
    continent.set_continentWidth(wd);
    continent.calcWidthHeightOfEachTerritory();
    continent.establishHandlesForTerritories();
    continent.set_continentMatrix( new String[ht][wd] );

    continent.fillContinentWithPlaceHolderSymbol();
    continent.fillPerimeterWithEdgeSymbol();

    continent.createTerritories();
    continent.placeTerritoriesIntoContinent();

    //toConsole(continent.toString_Territories() );
    if (toConsole) { toConsole(continent.toString() ); }

    return continent;
  }

  /**
   * Utility method to manually build an ascii map with some number of continents. Automatically
   * names map (e.g., "2015/03/21 21:59:48 quick map"), and sends it to the console. Does
   * not attempt to build the connections as a normal map would. Does not set map's 'continents requested'
   * field or 'continents placed' field either.
   * <p>
   * Example use: ASCII_RiskMap map = quickmap(2,40,50)
   *
   * @return a map as requested.
   */
  public ASCII_RixkMap quickMap(int numToPlace, int height, int width, boolean toConsole) {

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();

    String name = (dateFormat.format(date)) + " quick map";

    ASCII_RixkMap map = new ASCII_RixkMap();
    map.set_name(name);
    map.set_mapHeight(height);
    map.set_mapWidth(width);
    map.set_continentsRequested(numToPlace);
    map.set_mapMatrix( new String[height][width] );

    map.errorCheckMapParameters();
    map.fillMapWithPlaceHolderCharacter();
    map.placeAllContinents();

    if (toConsole) { this.toConsole( map.toString() ); }

    return map;

  }

  /**
   * Quick and single place to turn on/off when all tests have passed. Simply
   * place a // in front of the line println below.
   *
   * @param stringForConsole
   *            a string to be passed to the console
   */
  private void toConsole ( String stringForConsole) {
    System.out.println ( stringForConsole );
  }


}

