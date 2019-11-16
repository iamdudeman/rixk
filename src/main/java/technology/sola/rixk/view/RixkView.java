package technology.sola.rixk.view;

/**
 * All Views to need implement this interface. When a View requires focus it
 * will gain it through startView().
 */
public interface RixkView {
  /**
   * Start this View. Before the execution of this method inside of the View
   * finishes the instance of GameViewManager should have it's next active
   * view set through setNextActiveView(View).
   */
  void startView();

  /**
   * Notify this view to display a message.
   *
   * @param message The message to display
   */
  void displayNotification(String message);
}
