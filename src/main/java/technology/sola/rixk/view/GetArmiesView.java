package technology.sola.rixk.view;

import technology.sola.rixk.controller.GetArmiesController;

/**
 * GetArmiesView This interface defines the functionality of a GetArmiesView.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public interface GetArmiesView extends RixkView {
  /**
   * Adds the GetArmiesController to this View.
   *
   * @param controller The Controller that handles this View's events
   */
  void addGetArmiesController(GetArmiesController controller);
}

