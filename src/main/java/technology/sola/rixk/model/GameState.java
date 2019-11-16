package technology.sola.rixk.model;

import technology.sola.rixk.GameViewManager;
import technology.sola.rixk.RixkUtils;
import technology.sola.rixk.model.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This is Model object holds the major state of the game. This includes the
 * current Player objects, the RixkMap, the current Phase of the game, the
 * acting Player, the order of play, the game statistics, and the two sets of
 * Dice.
 */
public class GameState {
  public static final String AI_DIRECTORY = "resources/RixkAI/";
  public static final String AI_EXTENSION = ".jar";
  public static final String MAP_DIRECTORY = "resources/RixkMaps/";
  public static final String MAP_EXTENSION = ".json";

  private List<Player> _players = null;

  private List<Player> _playerOrder = null;

  private RixkMap _rixkMap = null;

  private ASCIIPlayObject _playObject = null;

  private Player _actingPlayer = null;

  private GameStats _gameStats = null;

  private Phase _gamePhase = null;

  /**
   * Build this GameState object.
   */
  public GameState() {
    _gamePhase = Phase.SETUP;
    _players = new ArrayList<Player>();
    _playerOrder = new ArrayList<Player>();
  }

  public void setPlayObject(ASCIIPlayObject playObject) {
    _playObject = playObject;
  }

  public void updatePlayObject(Player player) {
    if (_playObject != null) {
      Iterator<Territory> territoryIter = player.getOwnedTerritories().iterator();
      while (territoryIter.hasNext()) {
        Territory territory = territoryIter.next();
        Iterator<ASCII_Continent> continents = _playObject.get_asciimap().getContinentList().iterator();
        String continentName = territory.getName().split("\\.")[0];
        int territoryWidth = 0;
        while (continents.hasNext()) {
          ASCII_Continent continent = continents.next();
          if (continent.get_continentName().equals(continentName)) {
            Iterator<ASCII_Territory> asciiTerrIter = continent.get_territories().iterator();
            while (asciiTerrIter.hasNext()) {
              ASCII_Territory asciiTerr = asciiTerrIter.next();
              if (asciiTerr.get_territoryName().equals(territory.getName())) {
                territoryWidth = asciiTerr.get_width();
              }
            }
            break;
          }
        }
        String playerName = territory.getOwningPlayer().getName();
        if (playerName.length() >= territoryWidth - 1) {
          playerName = playerName.substring(0, territoryWidth - 1);
        }
        while (playerName.length() < territoryWidth - 1) {
          playerName += " ";
        }
        _playObject.update(territory.getName(), playerName, territory.getArmies());
      }
      RixkUtils.savePlayObjectToFile(_playObject);
    }
  }

  public ASCIIPlayObject getPlayObject() {
    return _playObject;
  }

  /**
   * Getter for the acting Player.
   *
   * @return The acting Player
   */
  public Player getActingPlayer() {
    return _actingPlayer;
  }

  /**
   * Setter for the acting Player.
   *
   * @param actingPlayer The new acting Player
   */
  public void setActingPlayer(Player actingPlayer) {
    _actingPlayer = actingPlayer;
  }

  /**
   * Getter for the RixkMap being played on.
   *
   * @return The RixkMap being played on
   */
  public RixkMap getRixkMap() {
    return _rixkMap;
  }

  /**
   * Setter for the RixkMap currently being played on.
   *
   * @param rixkMap The RixkMap to play on
   */
  public void setRixkMap(RixkMap rixkMap) {
    _rixkMap = rixkMap;
  }

  /**
   * Getter for the GameStats object for this GameState.
   *
   * @return The GameStats object
   */
  public GameStats getGameStats() {
    return _gameStats;
  }

  /**
   * Returns the current Phase of the game.
   *
   * @return The current Phase
   */
  public Phase getCurrentPhase() {
    return _gamePhase;
  }

  /**
   * Return the current Player order.
   *
   * @return The Player order
   */
  public List<Player> getPlayerOrder() {
    return _playerOrder;
  }

  /**
   * Returns the possible AI types that are found on the file system.
   *
   * @return The possible AI types
   */
  public List<String> getAiTypes() {
    List<String> aiTypes = new LinkedList<>();
    String[] jarFiles = RixkUtils.getFilenamesInDirectory(AI_DIRECTORY, AI_EXTENSION);

    for (int i = 0; i < jarFiles.length; i++) {
      List<String> classnames = RixkUtils.getClassnamesFromJar(AI_DIRECTORY + jarFiles[i]);
      Iterator<String> classnameIter = classnames.iterator();
      while (classnameIter.hasNext()) {
        aiTypes.add(classnameIter.next());
      }
    }
    return aiTypes;
  }

  /**
   * Returns the possible RixkMap choices that are found on the file system.
   *
   * @return The possible RixkMap choices
   */
  public String[] getRixkMapChoices() {
    return RixkUtils.getFilenamesInDirectory(MAP_DIRECTORY, MAP_EXTENSION);
  }

  /**
   * Rolls the dice and returns the result of the battle.
   *
   * @param attacking The number attacking
   * @param defending The number defending
   * @return The battle result
   */
  public BattleResult rollDice(int attacking, int defending) {
    return new BattleResult(attacking, defending);
  }

  /**
   * Sets the current phase to the next Phase. Also updates the currently
   * active Player.
   */
  public void nextPhase() {
    // Get the index of the active Player in the player order list
    int currentPlayerIndex = _playerOrder.indexOf(_actingPlayer);

    // The next game Phase logic switch
    switch (_gamePhase) {
      case SETUP:
        // The acting Player should be set to the first Player in the list
        _actingPlayer = _playerOrder.get(0);
        // Set the Phase to GET_ARMIES
        _gamePhase = Phase.GET_ARMIES;
        break;
      case GET_ARMIES:
        // Set the Phase to ATTACK
        _gamePhase = Phase.ATTACK;
        break;
      case ATTACK:
        // If acting Player owns all territories go to game summary Phase
        if (_actingPlayer.getTotalOwnedTerritories() == _rixkMap.getTotalTerritoryCount()) {
          _gamePhase = Phase.GAME_SUMMARY;
        }
        // Otherwise go to Fortify Phase
        else {
          _gamePhase = Phase.FORTIFY;
        }
        break;
      case FORTIFY:
        // Try to switch acting Players to another Player that has a
        // Territory
        do {
          // New acting Player should be next one in list
          // Modulus of size loops around list if we go over
          currentPlayerIndex = (currentPlayerIndex + 1) % _playerOrder.size();
          _actingPlayer = _playerOrder.get((currentPlayerIndex));
        }
        while (_actingPlayer.getTotalOwnedTerritories() == 0);

        // Set the Phase to GET_ARMIES
        _gamePhase = Phase.GET_ARMIES;
        break;
      case GAME_SUMMARY:
        // Game summary is over so trigger the end game
        GameViewManager.getInstance().endGame();
        break;
    }
  }

  /**
   * Adds a new Player to the list of players in this game of Rixk.
   *
   * @param player The new Player
   */
  public void addPlayer(Player player) {
    _players.add(player);
    _playerOrder.add(player);
  }

  /**
   * Shuffle the order of Player objects for play.
   */
  public void shufflePlayerOrder() {
    RixkUtils.shufflePlayers(_playerOrder);
  }

  /**
   * Phase This enum holds the possible Phase values for the game of Rixk.
   *
   * @author Tim Solum
   * @version $Revision$ ($Author$)
   */
  public enum Phase {
    SETUP, GET_ARMIES, ATTACK, FORTIFY, GAME_SUMMARY
  }
}
