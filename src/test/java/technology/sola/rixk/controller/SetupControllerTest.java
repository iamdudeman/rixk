package technology.sola.rixk.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import technology.sola.rixk.model.GameState;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SetupControllerTest These tests test the methods in SetupController.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
class SetupControllerTest {
  private SetupController setupController = null;

  /**
   * Get the SetupController ready before each test.
   */
  @BeforeEach
  void setup() {
    GameState gameState = new GameState();
    // Give setupController the gameState and a SetupView
    // TODO For now null for the SetupView should be fine
    // TODO If we had a test view we would register the controller in it
    setupController = new SetupController(gameState, null);
  }

  /**
   * This test verifies that the selectMapEvent returns false whenever a null
   * filename is used.
   */
  @Test
  void testSelectMapEventNull() {
    Boolean expected = false;

    Boolean success = setupController.selectMapEvent(null);

    assertEquals(expected, success);
  }
}

