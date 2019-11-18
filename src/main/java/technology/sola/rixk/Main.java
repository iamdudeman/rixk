package technology.sola.rixk;

import technology.sola.rixk.controller.*;
import technology.sola.rixk.model.GameState;
import technology.sola.rixk.view.*;
import technology.sola.rixk.view.cli.*;

public class Main {
  public static void main(String[] args) {
    GameViewManager gvm = GameViewManager.getInstance();

    // Model initialization
    GameState gameState = new GameState();

    // View initialization
    SetupView setupView = new CliSetupView(gameState, System.in, System.out);
    GetArmiesView getArmiesView = new CliGetArmiesView(gameState, System.in, System.out);
    AttackView attackView = new CliAttackView(gameState, System.in, System.out);
    FortifyView fortifyView = new CliFortifyView(gameState, System.in, System.out);
    GameSummaryView gameSummaryView = new CliGameSummaryView(gameState, System.in, System.out);
    RixkMapView rixkMapView = new CliRixkMapView(gameState, System.in, System.out);

    // Controller initialization
    SetupController setupController = new SetupController(gameState, setupView);
    GetArmiesController getArmiesController = new GetArmiesController(gameState, getArmiesView);
    AttackController attackController = new AttackController(gameState, attackView);
    FortifyController fortifyController = new FortifyController(gameState, fortifyView);
    GameSummaryController gameSummaryController = new GameSummaryController(gameState, gameSummaryView);
    RixkMapController rixkMapController = new RixkMapController(gameState, rixkMapView);

    // Add controllers to views
    setupView.addSetupController(setupController);
    getArmiesView.addGetArmiesController(getArmiesController);
    attackView.addAttackController(attackController);
    fortifyView.addFortifyController(fortifyController);
    gameSummaryView.addGameSummaryController(gameSummaryController);
    rixkMapView.addRixkMapController(rixkMapController);


    // Register the views to their keys in the keyToViewMap of the GVM
    gvm.addViewToMap(GameViewManager.SETUP_VIEW_KEY, setupView);
    gvm.addViewToMap(GameViewManager.GET_ARMIES_VIEW_KEY, getArmiesView);
    gvm.addViewToMap(GameViewManager.ATTACK_VIEW_KEY, attackView);
    gvm.addViewToMap(GameViewManager.FORTIFY_VIEW_KEY, fortifyView);
    gvm.addViewToMap(GameViewManager.GAME_SUMMARY_VIEW_KEY, gameSummaryView);
    gvm.addViewToMap(GameViewManager.RIXK_MAP_VIEW_KEY, rixkMapView);

    // start the game with the setupView as our first active view
    gvm.startGame(setupView);
  }
}
