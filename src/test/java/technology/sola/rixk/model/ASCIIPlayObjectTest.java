package technology.sola.rixk.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import technology.sola.rixk.RixkUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class ASCIIPlayObjectTest {

  private final int HORIZONTAL_ORIENTED = 1;
  private final int VERTICAL_ORIENTED = 2;


  // ----------------------Start Test methods.

  /**
   * Constructor used to rebuild ASCII_RixkMaps.
   *
   * @param asciiMapInfo
   *            Holds the map name, rows, and cols, and then player, bonus army values
   *            for 2 - players.
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
   * @param startingArmies
   *            Holds information about how many bonus armies a player
   *            gets, depending on number of players.  Elements 0-1 are
   *            undefined, since Rixk requires at least 2 players.
   */


  /**
   * save as above, but with a -2 generated map.
   */
  @Test
  public void testTimsBugWithPlayObject1() {
    String asciiMapInfo = "generated,40,80,2,40,3,35,4,30,5,25,6,20";
    String continentSizesAndOriensKVs = "C0=16,21,2,2 ; C1=11,17,2,2 ; C2=12,17,1,2";
    String contHandlesHashMapKVs = "C0=11,49 ; C1=8,30 ; C2=17,4";
    String connectionsRays = "16,49,6,3 ; 24,20,2,29";
    String connectionsListKVs = "C0.T0,C0.T1 ; C1.T0,C1.T1 ; C2.T0,C2.T1 ; C0.T0,n/a ; C2.T1,C0.T0";

    ASCIIPlayObject playObject = new ASCIIPlayObject(asciiMapInfo, continentSizesAndOriensKVs, contHandlesHashMapKVs, connectionsListKVs, connectionsRays);
    System.out.println(playObject.toString());
  }

  /**
   * save as above, but with a -2 generated map.
   */
  @Test
  public void testTimsBugWithPlayObject2() {
    String asciiMapInfo = "generated,40,80,2,40,3,35,4,30,5,25,6,20";
    String continentSizesAndOriensKVs = "C0=11,8,1,2 ; C1=13,17,2,2 ; C2=16,14,1,3";
    String contHandlesHashMapKVs = "C0=12,24 ; C1=22,3 ; C2=5,4";
    String connectionsRays = "22,15,0,2 ; 17,24,6,7";
    String connectionsListKVs = "C0.T0,C0.T1 ; C1.T0,C1.T1 ; C2.T0,C2.T1 ; C2.T1,C2.T2 ; C1.T1,n/a ; C0.T1,C2.T2";

    ASCIIPlayObject playObject = new ASCIIPlayObject(asciiMapInfo, continentSizesAndOriensKVs, contHandlesHashMapKVs, connectionsListKVs, connectionsRays);
    System.out.println(playObject.toString());
  }

  /**
   * Pull the three things from the asciiMapInfo parameter.
   */
  @Test
  public void test_initializeAsciiMapInfo() {

    // SETUP
    ASCIIPlayObject apo = new ASCIIPlayObject();
    apo.set_asciiMapInfo("testMap, 40,80,2,30,3,40,4,50,5,60,6,70");
    apo.set_continentSizesAndOriensKVs("C0=20,18,2 ; C21=23,21,2");

    // TEST
    apo.initializeAsciiMapInfo();

    // OBSERVE
    String str = (
      apo.get_asciimap().get_name() + "" + // \r\n
        apo.get_asciimap().get_mapHeight() + "" +
        apo.get_asciimap().get_mapWidth() +
        apo.get_asciimap().get_startingArmies().length

    );

    toConsole(str);

    // VERIFY
    assertTrue(str.equals("testMap40807"));
  }


  /**
   * pull the relevant parameter, see if the count is right.
   */
  @Test
  public void test_inferNumberOfContinentsToPlace() {

    // SETUP
    ASCIIPlayObject apo = new ASCIIPlayObject();
    apo.set_asciiMapInfo("testMap, 47,47,2,30,3,40,4,50,5,60,6,70");
    apo.set_continentSizesAndOriensKVs("C0=20,18,2 ; C2=23,21,2 ; C3 = 24,24,1");
    apo.initializeAsciiMapInfo();

    // TEST
    apo.inferNumberOfContinentsToPlace();

    // OBSERVE
    int value = apo.get_numberOfContientsToPlace();
    toConsole(value + "");

    // VERIFY
    assertTrue(value == 3);


  }

  /**
   * make an empty map ready to receive whatever.
   */
  @Test
  public void test_buildEmptyMap() {

    // SETUP
    ASCIIPlayObject apo = new ASCIIPlayObject();
    apo.set_asciiMapInfo("testMap, 47,47,2,30,3,40,4,50,5,60,6,70");
    apo.set_continentSizesAndOriensKVs("C0=20,18,2 ; C21=23,21,2");

    apo.initializeAsciiMapInfo();
    apo.inferNumberOfContinentsToPlace();

    // TEST
    apo.buildEmptyMap();

    // OBSERVE
    apo.get_asciimap().toString();

    // VERIFY
    assertTrue(apo.get_asciimap().peek(0, 0).equals("."));

  }

  /**
   *01234567890123456789012345678901234567890123456
   00...............................................
   01.##################.......##################...
   02.#C0.T0  |C0.T1   #.......#C1.T0  |C1.T1   #...
   03.#n/a    |n/a     #.......#n/a    |n/a     #...
   04.#0      |0       #.......#0      |0       #...
   05.#       |        #.......#       |        #...
   06.#       |        #.......#       |        #...
   07.#       |        #.......#       |        #...
   08.#       |        #.......#       |        #...
   09.#       |        #.......#       |        #...
   10.#       |        #.......#       |        #...
   11.#       |        #.......#       |        #...
   12.#       |        #.......#       |        #...
   13.#       |        #.......#       |        #...
   14.#       |        #.......#       |        #...
   15.#       |        #.......#       |        #...
   16.#       |        #.......#       |        #...
   17.#       |        #.......#       |        #...
   18.#       |        #.......#       |        #...
   19.#       |        #.......#       |        #...
   20.##################.......##################...
   21...............................................
   22...............................................
   */

  /**
   * Reconstruct the above map from data only provided thru the
   * ASCIIPlayObject parameters.
   */
  @Test
  public void test_placeAllContinents() {

    // SETUP
    ASCIIPlayObject apo = new ASCIIPlayObject();
    apo.set_asciiMapInfo("testMap, 47,47,2,30,3,40,4,50,5,60,6,70");  // name, rows, cols, player cnt, player army bonus
    apo.set_continentSizesAndOriensKVs("C0=20,18,2,2 ; C21=20,18,2,2"); // rows,cols,orien, teritories
    apo.set_contHandlesHashMapKVs("C0=1,1 ; C1=1,26"); // handles for each continent.

    apo.initializeAsciiMapInfo();
    apo.inferNumberOfContinentsToPlace();
    apo.buildEmptyMap();

    // TEST
    apo.placeAllContinents();

    // OBSERVE
    toConsole(apo.get_asciimap().toString());

    // VERIFY
    assertTrue(apo.get_asciimap().getContinentList().size() == 2);


  }

  /**
   *01234567890123456789012345678901234567890123456
   00...............................................
   01.##################.......##################...
   02.#C0.T0  |C0.T1   #@@@@@@@#C1.T0  |C1.T1   #...
   03.#n/a    |n/a     #.......#n/a    |n/a     #...
   04.#0      |0       #.......#0      |0       #...
   05.#       |        #.......#       |        #...
   06.#       |        #.......#       |        #...
   07.#       |        #.......#       |        #...
   08.#       |        #.......#       |        #...
   09.#       |        #.......#       |        #...
   10.#       |        #.......#       |        #...
   11.#       |        #.......#       |        #...
   12.#       |        #.......#       |        #...
   13.#       |        #.......#       |        #...
   14.#       |        #.......#       |        #...
   15.#       |        #.......#       |        #...
   16.#       |        #.......#       |        #...
   17.#       |        #.......#       |        #...
   18.#       |        #.......#       |        #...
   19.#       |        #.......#       |        #...
   20.##################.......##################...
   21...............................................
   22...............................................
   */

  /**
   * Reconstruct the above map, now with all connections
   * in place.
   */
  @Test
  public void test_placeAllConnections() {


    // SETUP
    ASCIIPlayObject apo = new ASCIIPlayObject();
    apo.set_asciiMapInfo("testMap, 47,47,2,30,3,40,4,50,5,60,6,70");  // name, rows, cols, player cnt, player army bonus
    apo.set_continentSizesAndOriensKVs("C0=20,18,2,2 ; C21=20,18,2,2"); // rows,cols,orien, teritories
    apo.set_contHandlesHashMapKVs("C0=1,1 ; C1=1,26"); // handles for each continent.
    apo.set_connectionsListKVs("C0.T1,C1.T0");
    apo.set_connectionsRays("2,18,2,7");

    apo.initializeAsciiMapInfo();
    apo.inferNumberOfContinentsToPlace();
    apo.buildEmptyMap();
    apo.placeAllContinents();

    // TEST
    apo.placeAllConnections();


    // OBSERVE
    toConsole(apo.get_asciimap().toString());

    // VERIFY
    assertTrue(apo.get_asciimap().getContinentList().size() == 2);

  }

  /**
   * Make simple two continent map with connections, and update
   * the player and armies values.
   */
  @Test
  public void test_update() {

    // SETUP
    ASCIIPlayObject apo = new ASCIIPlayObject();
    apo.set_asciiMapInfo("testMap, 50,50,2,30,3,40,4,50,5,60,6,70");  // name, rows, cols, player cnt, player army bonus
    apo.set_continentSizesAndOriensKVs("C0=20,18,1,2 ; C21=20,18,1,2"); // rows,cols,orien, teritories
    apo.set_contHandlesHashMapKVs("C0=1,1 ; C1=1,26"); // handles for each continent.
    apo.set_connectionsRays("2,18,2,7");
    apo.set_connectionsListKVs("C0.T1,C1.T0");


    apo.initializeAsciiMapInfo();
    apo.inferNumberOfContinentsToPlace();
    apo.buildEmptyMap();
    apo.placeAllContinents();
    apo.placeAllConnections();

    // TEST

    apo.update("C1.T1", "XYZ", 789);
    apo.update("C0.T0", "ABC", 123);

    // OBSERVE
    toConsole(apo.get_asciimap().toString());

    // VERIFY
    assertTrue(apo.get_asciimap().getContinentList().size() == 2);


  }


  // -------------------------------
  // Utility methods
  // -------------------------------


  /**
   * quick and single place to turn on/off when all tests have passed. Simply
   * place a // in front of the line println below.
   *
   * @param stringForConsole a string to be passed to the console
   */
  private void toConsole(String stringForConsole) {
    System.out.println(stringForConsole);
  }


  // -------------------------------
  // test methods.
  // -------------------------------

  /**
   * build larger maps with n of continents. Observe results.
   * Test might occasionally fail with high density.
   */
  @Test
  public void testMap_n() {

    String name = "generated";
    int height = 40;
    int width = 80;
    int numToPlace = 3;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

  }

  /**
   * Setup 3 continents on a map automatically, then save the map using Solumns
   * method: saveASCIIRixkMap( ASCII_RixkMap map )
   *
   * @throws IOException
   */
  @Test
  public void test_saveIt() throws IOException {

    //if (1>0) return; // bm 3/24 force test to pass until solum updates json file.

    // SETUP
    String name = "generated_foo";
    int height = 50;
    int width = 100;
    int numToPlace = 3;


    ASCII_RixkMap generated = new ASCII_RixkMap(name, height, width, numToPlace); // normal map
    try {
      // TEST
      RixkUtils.saveASCIIRixkMap(generated);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // OBSERVE
    toConsole(generated.toString());


  }


  /**
   * TODO bm 3/26 figure out array out of bounds error in reconstructing of maps.
   * <p>
   * Take info cut-n-pasted from Solums jstor map and reconstruct it.
   * example:
   *
   * </pre>
   * "ascii": {
   * "asciiMapInfo": "test,100,502,40,3,35,4,30,5,25,6,20",
   * "continentSizesHashMapKVs": "C0=21,45,2,4 ; C1=28,26,1,2 ; C2=2,56,2,3",
   * "contHandlesHashMapKVs": "C0=21,45 ; C1=28,26 ; C2=2,56",
   * "connectionRays": "33,41,2,4 ; 14,59,4,7",
   * "connectionsListKVs": "C0.T0,C0.T1 ; C0.T1,C0.T2 ; C0.T2,C0.T3 ; C1.T0,C1.T1 ; C2.T0,C2.T1 ; C2.T1,C2.T2 ; C1.T0,C0.T0 ; C2.T0,C0.T1"
   * }
   * </pre>
   */
  @Test
  public void test_reconstruct_it() {

    //if (1>0) return; // force test to pass.


    ASCIIPlayObject apo = new ASCIIPlayObject();

    // hand built 1; two continents. works.
		/*
		apo.set_asciiMapInfo("testMap,22,50,2,30,3,40,4,50,5,60,6,70");  // name, rows, cols, player cnt, player army bonus
		apo.set_continentSizesAndOriensKVs("C0=20,18,1,2 ; C21=20,18,1,2"); // rows,cols,orien, teritories
		apo.set_contHandlesHashMapKVs("C0=1,1 ; C1=1,26"); // handles for each continent.
		apo.set_connectionsRays("2,18,2,7");
		apo.set_connectionsListKVs("C0.T1,C1.T0");
		*/

    // hand built 2 ; 3 continents. does NOT work.
    apo.set_asciiMapInfo("test,51,51,2,30,3,40,4,50,5,60,6,70");  // name, rows, cols, player cnt, player army bonus
    apo.set_continentSizesAndOriensKVs("C0=10,21,1,2 ; C1=18,20,1,4 ; C2=11,11,1,2"); // rows,cols,orien, teritories
    apo.set_contHandlesHashMapKVs("C0=38,30 ; C1=3,31 ; C2=13,18"); // handles for each continent.
    apo.set_connectionsRays("38,40,0,18 ; 16,28,2,3");
    apo.set_connectionsListKVs("C0.T0,C0.T1 ; C1.T0,C1.T1 ; C1.T1,C1.T2 ; C1.T2,C1.T3 ; C2.T0,C2.T1 ; C0.T0,C1.T3 ; C2.T0,C1.T3");


    // test.json -- probably needs to be redone
		/*
		apo.set_asciiMapInfo("test,100,502,40,2,30,3,35,4,30,5,25,6,20");  // name, rows, cols, player cnt, player army bonus
		apo.set_continentSizesAndOriensKVs("C0=21,45,2,4 ; C1=28,26,1,2 ; C2=2,56,2,3"); // rows,cols,orien, teritories
		apo.set_contHandlesHashMapKVs("C0=21,45 ; C1=28,26 ; C2=2,56"); // handles for each continent.
		apo.set_connectionsRays("33,41,2,4 ; 14,59,4,7");
		apo.set_connectionsListKVs( "C0.T0,C0.T1 ; C0.T1,C0.T2 ; C0.T2,C0.T3 ; C1.T0,C1.T1 ; C2.T0,C2.T1 ; C2.T1,C2.T2 ; C1.T0,C0.T0 ; C2.T0,C0.T1");
		*/

    apo.initializeAsciiMapInfo();
    apo.inferNumberOfContinentsToPlace();
    apo.buildEmptyMap();
    apo.placeAllContinents();
    apo.placeAllConnections();


    toConsole(apo.get_asciimap().toString());


  }


}

