package technology.sola.rixk.view;

import technology.sola.rixk.controller.GameSummaryController;

/**
 * GameSummaryView This interface defines the functionality of a
 * GameSummaryView.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public interface GameSummaryView extends RixkView {
  /**
   * Adds a GameSummaryController to this view.
   *
   * @param controller The GameSummaryController to add
   */
  void addGameSummaryController(GameSummaryController controller);
}
