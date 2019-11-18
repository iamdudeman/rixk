package technology.sola.rixk.view;

import technology.sola.rixk.controller.AttackController;

/**
 * AttackView This interface establishes the required functionality of an
 * AttackView for the game of Rixk.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public interface AttackView extends RixkView {
  /**
   * This adds the AttackController to this View.
   *
   * @param controller The AttackController to add
   */
  void addAttackController(AttackController controller);
}
