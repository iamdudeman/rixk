package technology.sola.rixk;

import technology.sola.rixk.controller.SetupController;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.SetupView;
import technology.sola.rixk.view.cli.CliSetupView;

public class Main {
  public static void main(String[] args) {
    GameViewManager gvm = GameViewManager.getInstance();

    // Model initialization
    GameState gameState = new GameState();

    // View initialization
    SetupView setupView = new CliSetupView(gameState, System.in, System.out);
    // TODO


    // Controller initialization
    SetupController setupController = new SetupController(gameState, setupView);
    // TODO

    // Add controllers to views
    setupView.addSetupController(setupController);
    // TODO

    // Register the views to their keys in the keyToViewMap of the GVM
    gvm.addViewToMap(GameViewManager.SETUP_VIEW_KEY, setupView);
    // TODO

    // start the game with the setupView as our first active view
    gvm.startGame(setupView);
  }
}
