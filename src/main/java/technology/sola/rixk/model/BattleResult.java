package technology.sola.rixk.model;

import technology.sola.rixk.RixkUtils;

import java.util.Arrays;
import java.util.Collections;

/**
 * BattleResult This class represents the result of a battle between an
 * attacking Player and a defending Player Territory.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class BattleResult {
  private Integer[] attackRoll = null;
  private Integer[] defendRoll = null;
  private Integer attackLoss = 0;
  private Integer defendLoss = 0;

  /**
   * Builds this BattleResult object.
   *
   * @param attacking The number of attacking armies
   * @param defending The number of defending armies
   */
  public BattleResult(int attacking, int defending) {
    init(attacking, defending);
  }

  /**
   * Getter for the armies lost by attacking Player.
   *
   * @return The attacking armies lost
   */
  public Integer getAttackingLoss() {
    return attackLoss;
  }

  /**
   * Getter for the armies lost by defending Player.
   *
   * @return The defending armies lost
   */
  public Integer getDefendingLoss() {
    return defendLoss;
  }

  /**
   * Getter for the attacking Player's roll.
   *
   * @return The attacking Player's roll
   */
  public Integer[] getAttackingRoll() {
    return attackRoll;
  }

  /**
   * Getter for the defending Player's roll.
   *
   * @return The defending Player's roll
   */
  public Integer[] getDefendingRoll() {
    return defendRoll;
  }

  private void init(int attacking, int defending) {
    // Create arrays
    attackRoll = new Integer[attacking];
    defendRoll = new Integer[defending];

    // Loop through each array and assign a random int to element
    for (int i = 0; i < attackRoll.length; i++) {
      attackRoll[i] = RixkUtils.getRandomInteger(1, 6);
    }

    for (int i = 0; i < defendRoll.length; i++) {
      defendRoll[i] = RixkUtils.getRandomInteger(1, 6);
    }

    // Sort each array
    Arrays.sort(attackRoll, Collections.reverseOrder());
    Arrays.sort(defendRoll, Collections.reverseOrder());

    // Compare the dice rolls
    for (int i = 0; i < Math.min(attacking, defending); i++) {
      // If attack roll is higher than defend roll
      if (attackRoll[i] > defendRoll[i]) {
        // Defender loses one
        defendLoss++;
      } else {
        // Otherwise attacker loses one
        attackLoss++;
      }
    }
  }
}
