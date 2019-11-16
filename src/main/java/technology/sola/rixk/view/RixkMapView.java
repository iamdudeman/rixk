package technology.sola.rixk.view;

import technology.sola.rixk.controller.RixkMapController;

/**
 * RixkMapView This interface defines the functionality of a RixkMapView.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public interface RixkMapView extends RixkView {
  /**
   * Adds a RixkMapController to this view.
   *
   * @param controller The RixkMapController to add
   */
  void addRixkMapController(RixkMapController controller);
}
