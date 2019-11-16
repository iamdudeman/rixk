package technology.sola.rixk;

import technology.sola.rixk.view.RixkView;

import java.util.HashMap;
import java.util.Map;

/**
 * GameViewManager This class keeps track of the currently active View and
 * handles the starting of Views.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class GameViewManager {
  public static final String SETUP_VIEW_KEY = "setup_view";
  public static final String GET_ARMIES_VIEW_KEY = "get_armies_view";
  public static final String ATTACK_VIEW_KEY = "attack_view";
  public static final String FORTIFY_VIEW_KEY = "fortify_view";
  public static final String GAME_SUMMARY_VIEW_KEY = "game_summary_view";
  public static final String RIXK_MAP_VIEW_KEY = "rixk_map_view";

  private static GameViewManager gameViewManager = new GameViewManager();

  private RixkView nextView = null;

  private boolean viewChanged = false;

  private boolean isPlaying = false;

  private final Map<String, RixkView> viewMap;

  private GameViewManager() {
    viewMap = new HashMap<>();
  }

  /**
   * Get the instance of the GameViewManager for the game of Rixk.
   *
   * @return The game's instance of the GameViewManager
   */
  public static GameViewManager getInstance() {
    return gameViewManager;
  }

  /**
   * Adds a mapping of a key to a RixkView. Use the static view constants
   * defined in the GameViewManager class.
   *
   * @param key  The key mapped to a View
   * @param view The View the key maps to
   */
  public void addViewToMap(String key, RixkView view) {
    viewMap.put(key, view);
  }

  /**
   * Returns a RixkView from the view map. Use the static view constants
   * defined in the GameViewManager class to access these views.
   *
   * @param key The key mapped to a RixkView
   * @return The RixkView
   */
  public RixkView getViewFromMap(String key) {
    return viewMap.get(key);
  }

  /**
   * This method queues up the View that will be next once the active View
   * releases control.
   *
   * @param nextView The next active View
   */
  public void setNextActiveView(RixkView nextView) {
    this.nextView = nextView;
    viewChanged = true;
  }

  /**
   * Starts the game with firstView as the first active View.
   *
   * @param firstView The first active View
   */
  public void startGame(RixkView firstView) {
    // Set is playing to true and set the active view to the first view
    isPlaying = true;
    RixkView activeView = firstView;

    // The game loop
    while (isPlaying) {
      // Start the active view first
      activeView.startView();

      // If the active View should be changed change it
      if (viewChanged) {
        activeView = nextView;
        viewChanged = false;
      }
      // Otherwise the active View was not updated so flag the game to end
      else {
        isPlaying = false;
      }
    }
  }

  /**
   * This triggers a flag where the game will end as soon as the active View
   * releases control.
   */
  public void endGame() {
    isPlaying = false;
  }
}

