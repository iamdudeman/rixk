package technology.sola.rixk.view;

import technology.sola.rixk.controller.FortifyController;

/**
 * FortifyView This interface defines the functionality of a FortifyView.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public interface FortifyView extends RixkView {
  /**
   * Adds a FortifyController to this View.
   *
   * @param controller The FortifyController to add
   */
  void addFortifyController(FortifyController controller);
}
