package technology.sola.rixk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RixkUtilsTest These tests test the methods within RixkUtils
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
class RixkUtilsTest {
  /**
   * This test verifies that random integers are being correctly generated.
   */
  @Test
  void testGetRandomInteger() {
    int rand;
    // Make 1000 random numbers and see if they are in the proper range
    for (int i = 0; i < 1000; i++) {
      rand = RixkUtils.getRandomInteger(0, 100);

      assertTrue(rand >= 0);
      assertTrue(rand <= 100);
    }

    // Generate number from 1 to 1 to make sure both min and max are
    // included
    rand = RixkUtils.getRandomInteger(1, 1);
    assertEquals(1, rand);
  }
}
