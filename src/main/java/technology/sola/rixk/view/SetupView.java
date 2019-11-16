package technology.sola.rixk.view;

import technology.sola.rixk.controller.SetupController;

/**
 * SetupView This interface defines the required functionality of a SetupView.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public interface SetupView extends RixkView {
  /**
   * Adds the SetupController to this View.
   *
   * @param controller The Controller that handles this View's events
   */
  void addSetupController(SetupController controller);
}
