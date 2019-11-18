package technology.sola.rixk.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BattleResultTest These tests test the methods within BattleResult
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
class BattleResultTest {
  /**
   * This test verifies that the i element in each of the attack roll and
   * defend roll are greater than or equal to i+1 element.
   */
  @Test
  void testRollsAreSorted() {
    // Use a large number of attacking and defending for testing purposes
    BattleResult result = new BattleResult(30, 30);

    // Get the attack roll
    Integer[] attackRoll = result.getAttackingRoll();
    // Go through and compare i and i+1 elements
    for (int i = 0; i < attackRoll.length - 1; i++) {
      assertTrue(attackRoll[i] >= attackRoll[i + 1]);
    }

    // Do the same for defend roll
    Integer[] defendRoll = result.getDefendingRoll();
    for (int i = 0; i < defendRoll.length - 1; i++) {
      assertTrue(defendRoll[i] >= defendRoll[i + 1]);
    }
  }

  /**
   * This test verifies that the correct number of armies lost is calculated
   * for attack and defend.
   */
  @Test
  void testCheckBattleLosses() {
    // We will have a high and low value for each since there are two
    // defending
    BattleResult result = new BattleResult(3, 2);

    // Get the high and low roll values for each
    Integer attackRollHigh = result.getAttackingRoll()[0];
    Integer attackRollLow = result.getAttackingRoll()[1];
    Integer defendRollHigh = result.getDefendingRoll()[0];
    Integer defendRollLow = result.getDefendingRoll()[1];

    // Calculate whether or not attacking wins high or low
    boolean attackWonHigh = attackRollHigh > defendRollHigh;
    boolean attackWonLow = attackRollLow > defendRollLow;

    // Declare the expected loss values
    int expectedAttackLoss = 0;
    int expectedDefendLoss = 0;

    // If attack wins then defend loses one and vice versa
    if (attackWonHigh) {
      expectedDefendLoss++;
    } else {
      expectedAttackLoss++;
    }

    if (attackWonLow) {
      expectedDefendLoss++;
    } else {
      expectedAttackLoss++;
    }

    assertEquals(expectedAttackLoss, result.getAttackingLoss(), "Wrong attack loss");
    assertEquals(expectedDefendLoss, result.getDefendingLoss(), "Wrong defend loss");
  }
}

