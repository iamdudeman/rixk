package technology.sola.rixk.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class ASCII_RixkMapTest {
  @Test
  public void testFillMapWithPlaceHolderCharacter() {

    String name = "test";
    int height = 4;
    int width = 5;
    String[][] mapMatrix = {

      {"?", "?", "?", "?", "?"},
      {"?", "?", "?", "?", "?"},
      {"?", "?", "?", "?", "?"},
      {"?", "?", "?", "?", "?"}
    };


    ASCII_RixkMap map = new ASCII_RixkMap();
    map.set_name(name);
    map.set_mapHeight(height);
    map.set_mapWidth(width);
    map.set_mapMatrix(mapMatrix);

    map.fillMapWithPlaceHolderCharacter();

    toConsole(map.toString());

  }


  /**
   * build a mpa only with 0 continents.
   */
  @Test
  public void testMap_0() {

    String name = "test";
    int height = 22;
    int width = 42;
    int numToPlace = 0;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

  }


  /**
   * put a continent down and check it's perimeter is clear.
   */
  @Test
  public void testCheckPerimeter1() {

    String name = "test";
    int height = 22;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    ASCII_Continent continent = new ASCII_Continent("C1", 10, 10);
    continent.set_handleRow(1);
    continent.set_handleCol(1);

    map.placeContinentAtMapLocation(continent, continent.get_handleRow(), continent.get_handleCol());

    toConsole(map.toString());

    boolean isClearPerimeter = map.checkPerimeter(continent, continent.get_handleRow(), continent.get_handleCol());

    assertTrue(isClearPerimeter);

  }

  /**
   * case: corner touchers
   * put two continents down and check that each's perimeter is NOT clear.
   */
  @Test
  public void testCheckPerimeter2() {

    String name = "test";
    int height = 22;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    /* throw one down */
    ASCII_Continent continent1 = new ASCII_Continent("C0", 10, 10);
    continent1.set_handleRow(1);
    continent1.set_handleCol(1);
    map.placeContinentAtMapLocation(continent1, continent1.get_handleRow(), continent1.get_handleCol());

    /* throw one just east of the first.*/
    ASCII_Continent continent2 = new ASCII_Continent("C1", 10, 10);
    continent2.set_handleRow(10);
    continent2.set_handleCol(11);
    map.placeContinentAtMapLocation(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    toConsole(map.toString());

    boolean isClearPerimeter;

    /* check west one */
    isClearPerimeter = map.checkPerimeter(continent1, continent1.get_handleRow(), continent1.get_handleCol());
    assertFalse(isClearPerimeter);

    /* check east one */
    isClearPerimeter = map.checkPerimeter(continent2, continent2.get_handleRow(), continent2.get_handleCol());
    assertFalse(isClearPerimeter);

  }

  /**
   * case: huggers
   * C0 on nw, C1 on se.
   * put two continents down, one on top the other. Perimenter NOT clera
   */
  @Test
  public void testCheckPerimeter2b() {

    String name = "test";
    int height = 35;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    /* throw small one down */
    ASCII_Continent continent1 = new ASCII_Continent("C0", 11, 9);
    continent1.set_handleRow(16);
    continent1.set_handleCol(10);
    map.placeContinentAtMapLocation(continent1, continent1.get_handleRow(), continent1.get_handleCol());

    /* throw one just big on on top of the first.*/
    ASCII_Continent continent2 = new ASCII_Continent("C1", 11, 8);
    continent2.set_handleRow(19);
    continent2.set_handleCol(19);
    map.placeContinentAtMapLocation(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    toConsole(map.toString());

    boolean isClearPerimeter;

    /* check west one */
    isClearPerimeter = map.checkPerimeter(continent1, continent1.get_handleRow(), continent1.get_handleCol());
    assertFalse(isClearPerimeter);

    /* check east one */
    isClearPerimeter = map.checkPerimeter(continent2, continent2.get_handleRow(), continent2.get_handleCol());
    assertFalse(isClearPerimeter);

  }

  /**
   * case: stackers
   * C0 on top, C1 on bottom
   * put two continents down, one on top the other.
   */
  @Test
  public void testCheckPerimeter2c() {

    String name = "test";
    int height = 35;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    /* throw small one down */
    ASCII_Continent continent1 = new ASCII_Continent("C0", 11, 9);
    continent1.set_handleRow(1);
    continent1.set_handleCol(1);
    map.placeContinentAtMapLocation(continent1, continent1.get_handleRow(), continent1.get_handleCol());

    /* throw one just big on on top of the first.*/
    ASCII_Continent continent2 = new ASCII_Continent("C1", 11, 8);
    continent2.set_handleRow(12);
    continent2.set_handleCol(1);
    map.placeContinentAtMapLocation(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    toConsole(map.toString());

    boolean isClearPerimeter;

    /* check west one */
    isClearPerimeter = map.checkPerimeter(continent1, continent1.get_handleRow(), continent1.get_handleCol());
    assertFalse(isClearPerimeter);

    /* check east one */
    isClearPerimeter = map.checkPerimeter(continent2, continent2.get_handleRow(), continent2.get_handleCol());
    assertFalse(isClearPerimeter);

  }

  /**
   * case: close-ies
   * C0 on top, C1 on bottom, but still just one perimeter unit separtes them, which is ok.
   */
  @Test
  public void testCheckPerimeter2d() {

    String name = "test";
    int height = 35;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    /* throw small one down */
    ASCII_Continent continent1 = new ASCII_Continent("C0", 11, 9);
    continent1.set_handleRow(1);
    continent1.set_handleCol(1);
    map.placeContinentAtMapLocation(continent1, continent1.get_handleRow(), continent1.get_handleCol());

    /* throw one just big on on top of the first.*/
    ASCII_Continent continent2 = new ASCII_Continent("C1", 11, 9);
    continent2.set_handleRow(13);
    continent2.set_handleCol(1);
    map.placeContinentAtMapLocation(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    toConsole(map.toString());

    boolean isClearPerimeter;

    /* check west one */
    isClearPerimeter = map.checkPerimeter(continent1, continent1.get_handleRow(), continent1.get_handleCol());
    assertTrue(isClearPerimeter);

    /* check east one */
    isClearPerimeter = map.checkPerimeter(continent2, continent2.get_handleRow(), continent2.get_handleCol());
    assertTrue(isClearPerimeter);

  }

  /**
   * case: close-ies
   * left right version.
   */
  @Test
  public void testCheckPerimeter2e() {

    String name = "test";
    int height = 35;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    /* throw small one down */
    ASCII_Continent continent1 = new ASCII_Continent("C0", 11, 9);
    continent1.set_handleRow(1);
    continent1.set_handleCol(1);
    map.placeContinentAtMapLocation(continent1, continent1.get_handleRow(), continent1.get_handleCol());

    /* throw one just big on on top of the first.*/
    ASCII_Continent continent2 = new ASCII_Continent("C1", 11, 9);
    continent2.set_handleRow(1);
    continent2.set_handleCol(11);
    map.placeContinentAtMapLocation(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    toConsole(map.toString());

    boolean isClearPerimeter;

    /* check west one */
    isClearPerimeter = map.checkPerimeter(continent1, continent1.get_handleRow(), continent1.get_handleCol());
    assertTrue(isClearPerimeter);

    /* check east one */
    isClearPerimeter = map.checkPerimeter(continent2, continent2.get_handleRow(), continent2.get_handleCol());
    assertTrue(isClearPerimeter);

  }


  /**
   * case: overlappers
   * C2 overlaps C1
   */
  @Test
  public void test_isEmptyForContinent() {

    String name = "test";
    int height = 22;
    int width = 42;
    int numToPlace = 0; // zero, so just a blank map.

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    /* throw small one down */
    ASCII_Continent continent1 = new ASCII_Continent("C1", 10, 10);
    continent1.set_handleRow(1);
    continent1.set_handleCol(1);
    map.placeContinentAtMapLocation(continent1, continent1.get_handleRow(), continent1.get_handleCol());

    /* make another to see if it will fit.*/
    ASCII_Continent continent2 = new ASCII_Continent("C2", 10, 15);
    continent2.set_handleRow(5);
    continent2.set_handleCol(5);

    /* check it first */
    boolean isEmpty = map.isEmptyForContinent(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    /* then put it there anyway to verify correct */
    map.placeContinentAtMapLocation(continent2, continent2.get_handleRow(), continent2.get_handleCol());

    toConsole(map.toString());
    assertFalse(isEmpty);

  }

  @Test
  public void test_stipulateHandle() {

    ASCII_RixkMap map = new ASCII_RixkMap();

    int handle = map.stipulateContinentHandle(10, 8);

    toConsole("handle: " + handle);

  }


  /**
   * build a map with 2 continents.
   */
  @Test
  public void testMap_2() {

    String name = "test";
    int height = 40;
    int width = 80;
    int numToPlace = 2;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

  }

  /**
   * build a map with n continents.
   * 47 x 198 fits nicely on a monitor
   */
  @Test
  public void testMap_3() {

    String name = "test";
    int height = 47;
    int width = 198;
    int numToPlace = 10;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

  }

  /**
   * build larger maps with lots of continents. Observe results.
   * Test might occasionally fail with high density.
   */
  @Test
  public void testMap_lots() {

    String name = "test";
    int height = 40;
    int width = 80;
    int numToPlace = 3;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

  }

  /**
   * question: what happens if i place the same key,value element twice?
   * answer: happily, nothing. Acts like set theory.
   */
  @Test
  public void hashdupe() {

    HashMap<String, String> foo = new HashMap<>(); // make a hashmpa.
    foo.put("k1", "v1");// put in 1 key,value
    foo.put("k2", "v2");// put in 2 key, value

    toConsole(foo.toString());

    foo.put("k1", "v1");// put in 1 key,value again
    foo.put("k1", "v1");// put in 1 key,value again
    foo.put("k1", "v1");// put in 1 key,value again
    foo.put("k1", "v1");// put in 1 key,value again
    foo.put("k1", "v1");// put in 1 key,value again

    toConsole(foo.toString()); // show it.
  }

  /**
   * put down a couple of contients in a map and then observe the handle
   * hash map looks right.
   */
  @Test
  public void test_updateContHandlesHashMap() {
    String name = "test";
    int height = 40;
    int width = 42;
    int numToPlace = 2;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

    String hashmap = map.get_contHandlesHashMap().toString();

    toConsole(hashmap);

    assertFalse(hashmap.equals("{}"), "hashmap value should not be empty!");

  }

  /**
   * put down a couple of continents in a map and observe the shorline
   * values are all stored in the shorline hashmap.
   */
  @Test
  public void test_updatecontinentShorelinesHashMap() {

    String name = "test";
    int height = 40;
    int width = 42;
    int numToPlace = 2;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

    String hashmap = map.get_continentShorelinesHashMap().toString();

    toConsole(hashmap);

    assertFalse(hashmap.equals("{}"), "hashmap value should not be empty!");

  }


  /**
   * Put down a couple of continents in a map, and make observe territory coordinates
   * are correctly converted to map coordinates.
   */
  @Test
  public void test_convertTerritoryHandleHashMapCoordsToMapCoords() {


    String name = "test";
    int height = 40;
    int width = 82;
    int numToPlace = 2;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

    String hashmap = map.get_terrHandlesHashMap().toString();

    toConsole(hashmap);

    assertFalse(hashmap.equals("{}"), "hashmap value should not be empty!");

  }

  /**
   * Put down a couple of continents in a map, and observe territory points
   * are correctly converted to map coordinates.
   */
  @Test
  public void test_convertAllTerritoryPointsHashMapToMapCoords() {


    String name = "test";
    int height = 40;
    int width = 42;
    int numToPlace = 2;

    ASCII_RixkMap map = new ASCII_RixkMap(name, height, width, numToPlace);

    toConsole(map.toString());

    String hashmap = map.get_allTerritoryPointsHashMap().toString();

    toConsole(hashmap);

    assertFalse(hashmap.equals("{}"), "hashmap value should not be empty!");

  }


  // Utility methods

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

