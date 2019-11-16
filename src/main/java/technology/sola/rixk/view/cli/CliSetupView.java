package technology.sola.rixk.view.cli;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.controller.SetupController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.model.player.CliHumanPlayer;
import technology.sola.rixk.model.player.Player;
import technology.sola.rixk.view.GetArmiesView;
import technology.sola.rixk.view.SetupView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * CliSetupView SetupView meant for the command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliSetupView extends CliAbstractView implements SetupView {
  private static final Logger LOGGER = Logger.getLogger(CliSetupView.class.getName());

  private SetupController setupController = null;

  /**
   * Builds this CliSetupView object
   *
   * @param gameState    The Model
   * @param inputStream  Typically System.in or FileInputStream
   * @param outputStream Typically System.out or FileOutputStream
   */
  public CliSetupView(GameState gameState, InputStream inputStream, OutputStream outputStream) {
    super(gameState, inputStream, outputStream);
  }

  @Override
  public void addSetupController(SetupController controller) {
    setupController = controller;
  }

  @Override
  public void startView() {
    LOGGER.info("Enter");
    // Setup the RixkMap
    setupRixkMap();

    // Add Players
    addPlayers();

    // Divide up the territories
    divideTerritories();

    // Place starting armies
    placeStartingArmies();

    // Finish up and start next view
    finishSetup();

    LOGGER.info("Exit");
  }

  private void setupRixkMap() {
    LOGGER.info("Enter");

    setupController.addPlayerEvent(CliHumanPlayer.class.getName());

    String filename = null;
    int choice = -1;
    String[] mapChoices = gameState.getRixkMapChoices();

    // Get the Player's choice
    do {
      // Display the map choices
      for (int i = 0; i < mapChoices.length; i++) {
        getCliOutput().write(String.format("[%2d]: %s\n", i + 1, mapChoices[i]));
      }

      // Prompt user for choice
      getCliOutput().write("Choice? ");
      // Get the choice from user
      choice = getCliInput().getInteger();
      // Check if it is a valid choice
      if (choice > mapChoices.length) {
        getCliOutput().write("Not a valid choice!\n");
      } else {
        // Get the filename associated with the choice and add the
        // directory to it
        filename = GameState.MAP_DIRECTORY + mapChoices[choice - 1];
      }
    }
    while (!setupController.selectMapEvent(filename));

    LOGGER.info("Exit");
  }

  private void addPlayers() {
    LOGGER.info("Enter");

    // Get the Player's choice of how many players
    Integer playerCountChoice = null;
    do {
      getCliOutput().write("How many AI players?\nChoice? ");

      playerCountChoice = getCliInput().getInteger();
      if (playerCountChoice == null) {
        getCliOutput().write("Invalid choice!\n");
      }
    }
    while (playerCountChoice == null);

    Integer choice = null;
    // Get the Player's choice for the types of Players
    for (int i = 0; i < playerCountChoice; i++) {
      String[] aiChoices = gameState.getAiTypes().toArray(
        new String[gameState.getAiTypes().size()]);
      // Get the Player's choice
      do {
        getCliOutput().write(
          "AI left to choose: " + (playerCountChoice - i)
            + "\n");
        getCliOutput().write("AI Choices\n");
        // Display the ai choices
        for (int j = 0; j < aiChoices.length; j++) {
          getCliOutput().write(String.format("[%2d]: %s\n", j + 1, aiChoices[j]));
        }

        // Prompt user for choice
        getCliOutput().write("Choice? ");
        // Get the choice from user
        choice = getCliInput().getInteger();
        // Check if it is a valid choice
        if (choice == null || choice > aiChoices.length) {
          getCliOutput().write("Not a valid choice!\n");
          choice = null;
        } else {
          String filename = aiChoices[choice - 1].split(" in ")[1];
          String classname = aiChoices[choice - 1].split(" in ")[0];
          if (setupController.addPlayerEvent(filename, classname)) {
            getCliOutput().write(classname + " player added\n");
          }
        }
      }
      while (choice == null);
    }

    LOGGER.info("Exit");
  }

  private void divideTerritories() {
    LOGGER.info("Enter");

    setupController.assignPlayerTerritoriesEvent();

    LOGGER.info("Exit");
  }

  private void placeStartingArmies() {
    LOGGER.info("Enter");

    // Get the first player from the player order
    int placingIndex = 0;
    List<Player> players = gameState.getPlayerOrder();
    Player currentPlacingPlayer = players.get(placingIndex);
    while (currentPlacingPlayer.getArmiesToPlace() > 0) {
      // Current Player should be told to place one army
      setupController.placeArmyEvent(currentPlacingPlayer);

      // Set to the next Player to place army
      placingIndex = (placingIndex + 1) % players.size();
      currentPlacingPlayer = players.get(placingIndex);
    }

    // Go through once more to get the odd numbered ones if there were any
    // This ensures no one is left with 1 army to place
    Iterator<Player> playerIter = gameState.getPlayerOrder().iterator();
    while (playerIter.hasNext()) {
      setupController.placeArmyEvent(playerIter.next());
    }

    LOGGER.info("Exit");
  }

  private void finishSetup() {
    LOGGER.info("Enter");

    // Get the GameViewManager instance
    GameViewManager gvm = GameViewManager.getInstance();
    // Get the key for the GetArmiesView
    String getArmiesViewKey = GameViewManager.GET_ARMIES_VIEW_KEY;
    // Get the next view from the GameViewManager
    GetArmiesView nextView = (GetArmiesView) gvm.getViewFromMap(getArmiesViewKey);
    // Trigger the startGetArmiesEvent for the nextView
    setupController.startGetArmiesEvent(nextView);

    LOGGER.info("Exit");
  }
}

