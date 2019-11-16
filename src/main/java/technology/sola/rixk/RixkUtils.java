package technology.sola.rixk;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import technology.sola.rixk.model.*;
import technology.sola.rixk.model.player.Player;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RixkUtils {
  private static Random rng = new Random(System.currentTimeMillis());

  /**
   * Creates a new random order of Players for play.
   *
   * @param playerOrder The current order of Players
   */
  public static void shufflePlayers(List<Player> playerOrder) {
    Collections.shuffle(playerOrder, rng);
  }

  /**
   * Gets the names of all of the files in the directory on file system. An
   * extension can be specified to only select files of that extension. If the
   * extension is null, all files in the directory will be displayed.
   *
   * @param directoryPath The path to the directory on file system
   * @param extension     The file extension wanted
   * @return The list of filenames
   */
  public static String[] getFilenamesInDirectory(String directoryPath, String extension) {
    boolean checkExtension = false;
    List<String> filenames = new LinkedList<>();

    // See if we care about extension
    if (extension != null) {
      checkExtension = true;
    }

    // Create the directory as a File object
    File directory = new File(directoryPath);
    // Get a list of all of the files in the directory (including folders)
    File[] allFiles = directory.listFiles();
    for (int i = 0; i < allFiles.length; i++) {
      // Check if the File is indeed a file and not a folder
      if (allFiles[i].isFile()) {
        // Get the current File's name
        String currentFilename = allFiles[i].getName();
        if (checkExtension) {
          // Get the last index of a period, that's where the
          // extension will be
          int extensionIndex = currentFilename.lastIndexOf(".");
          // Make sure there was an extension
          if (extensionIndex != -1) {
            // Check that the case insensitive extension matches
            String fileExtension = currentFilename.substring(extensionIndex);
            if (fileExtension.toLowerCase().equals(
              extension.toLowerCase())) {
              // Add the file with the checked extension
              filenames.add(currentFilename);
            }
          }
        } else {
          // Add the filename if we don't care about extension
          filenames.add(currentFilename);
        }
      }
    }

    return filenames.toArray(new String[filenames.size()]);
  }

  /**
   * Returns a randomly generated int from range min to max including both min
   * and max.
   *
   * @param min The minimum random int
   * @param max The maximum random int
   * @return A random int
   */
  public static int getRandomInteger(int min, int max) {
    int num = 0;

    // Add 1 at the end of the range so it includes the max
    num = rng.nextInt(max - min + 1) + min;

    return num;
  }

  public static void savePlayObjectToFile(ASCIIPlayObject playObject) {
    try {
      PrintWriter output = new PrintWriter(new BufferedOutputStream(new FileOutputStream(GameState.MAP_DIRECTORY + playObject.get_asciimap().get_name() + ".txt", false)));
      output.write(playObject.toString());
      output.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static boolean isASCIIRixkMap(String filename) {
    JsonParser jsonParser = new JsonParser();
    JsonObject root = null;
    try {
      root = jsonParser.parse(new FileReader(new File(filename))).getAsJsonObject();
    } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
      e.printStackTrace();
    }

    return root.has("ascii");
  }

  public static ASCIIPlayObject loadASCIIPlayObject(String filename) {
    if (filename == null) {
      return null;
    }

    // Create variables
    ASCIIPlayObject playObject = null;
    String asciiMapInfo = null;
    String continentSizesAndOriensKVs = null;
    String contHandlesHashMapKVs = null;
    String connectionsListKVs = null;
    String connectionRays = null;

    // Begin parsing
    JsonParser jsonParser = new JsonParser();
    JsonObject root = null;
    try {
      root = jsonParser.parse(new FileReader(new File(filename))).getAsJsonObject();
    } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
      e.printStackTrace();
    }

    JsonObject ascii = root.get("ascii").getAsJsonObject();
    asciiMapInfo = ascii.get("asciiMapInfo").getAsString();
    continentSizesAndOriensKVs = ascii.get("continentSizesAndOriensKVs").getAsString();
    contHandlesHashMapKVs = ascii.get("contHandlesHashMapKVs").getAsString();
    connectionsListKVs = ascii.get("connectionsListKVs").getAsString();
    connectionRays = ascii.get("connectionsRays").getAsString();

    // Create the object and return
    playObject = new ASCIIPlayObject(asciiMapInfo,
      continentSizesAndOriensKVs, contHandlesHashMapKVs,
      connectionsListKVs, connectionRays);
    return playObject;
  }

  /**
   * Saves an ASCII_RixkMap object to a file in JSON format. The file it is
   * saved to is the name_of_map.json.
   *
   * @param map The map to save
   * @throws IOException
   */
  public static void saveASCIIRixkMap(ASCII_RixkMap map) throws IOException {
    JsonWriter jsonWriter = new JsonWriter(new FileWriter(new File(
      GameState.MAP_DIRECTORY + map.get_name()
        + GameState.MAP_EXTENSION)));
    jsonWriter.setIndent("\t");

    // Begin our root json object
    jsonWriter.beginObject();

    // Add starting army values for 2-6 players
    for (int i = 2; i <= 6; i++) {
      jsonWriter.name(i + "player").value(map.get_startingArmies()[i]);
    }

    // Begin continents array
    jsonWriter.name("continents").beginArray();

    Iterator<ASCII_Continent> continentIter = map.getContinentList().iterator();
    while (continentIter.hasNext()) {
      // Begin this continent object
      jsonWriter.beginObject();

      ASCII_Continent currentContinent = continentIter.next();
      jsonWriter.name("name").value(
        currentContinent.get_continentName());
      jsonWriter.name("bonus").value(
        currentContinent.get_bonusArmies());
      // Begin territories array
      jsonWriter.name("territories").beginArray();
      Iterator<ASCII_Territory> territoryIter = currentContinent.get_territoriesList().iterator();
      while (territoryIter.hasNext()) {
        // Begin territory object
        jsonWriter.beginObject();
        ASCII_Territory currentTerritory = territoryIter.next();
        String currentTerritoryName = currentTerritory.get_territoryName();
        jsonWriter.name("name").value(currentTerritoryName);

        // Begin connections array
        jsonWriter.name("connections").beginArray();
        Iterator<String> adjacentIter = map.get_connectionsList().iterator();
        while (adjacentIter.hasNext()) {
          String currentConnectionElement = adjacentIter.next();
          String[] connections = currentConnectionElement.split(" ; ");
          for (int i = 0; i < connections.length; i++) {
            String[] connection = connections[i].split(",");
            if (connection[0].equals(currentTerritoryName)) {
              // Begin adjacent object
              jsonWriter.beginObject();
              jsonWriter.name("name").value(connection[1]);

              // End adjacent object
              jsonWriter.endObject();
            }
            if (connection[1].equals(currentTerritoryName)) {
              // Begin adjacent object
              jsonWriter.beginObject();
              jsonWriter.name("name").value(connection[0]);
              // End adjacent object
              jsonWriter.endObject();
            }
          }
        }

        // End adjacent array
        jsonWriter.endArray();

        // End territory object
        jsonWriter.endObject();
      }
      // End territories array
      jsonWriter.endArray();
      // End this continent object
      jsonWriter.endObject();
    }

    // End continents array
    jsonWriter.endArray();

    // Begin ASCII object
    jsonWriter.name("ascii").beginObject();

    // Map info value
    StringBuilder mapInfoBuilder = new StringBuilder();
    mapInfoBuilder.append(map.get_name() + "," + map.get_mapHeight() + ","
      + map.get_mapWidth() + ",");
    for (int i = 2; i <= 6; i++) {
      mapInfoBuilder.append(i + "," + map.get_startingArmies()[i] + ",");
    }
    mapInfoBuilder.deleteCharAt(mapInfoBuilder.length() - 1);
    jsonWriter.name("asciiMapInfo").value(mapInfoBuilder.toString());

    // Continent sizes value
    StringBuilder continentStuff = new StringBuilder();
    Iterator<ASCII_Continent> continents = map.getContinentList().iterator();
    while (continents.hasNext()) {
      ASCII_Continent continent = continents.next();
      int rows = continent.get_continentHeight();
      int columns = continent.get_continentWidth();
      int orientation = continent.get_orientation();

      continentStuff.append(continent.get_continentName() + "=" + rows
        + "," + columns + "," + orientation + ","
        + continent.get_territories().size());
      continentStuff.append(" ; ");
    }
    continentStuff.deleteCharAt(continentStuff.length() - 1);
    continentStuff.deleteCharAt(continentStuff.length() - 1);
    continentStuff.deleteCharAt(continentStuff.length() - 1);
    jsonWriter.name("continentSizesAndOriensKVs").value(
      continentStuff.toString());

    // Continent handles stuff
    jsonWriter.name("contHandlesHashMapKVs").value(
      map.get_contHandlesHashMap().toString().replaceAll("\\{", "").replaceAll(
        "\\}", "").replaceAll(", ", " ; "));

    // ConnectionRays value
    StringBuilder connectionRaysBuilder = new StringBuilder();
    Iterator<String> connectionRaysIter = map.get_connectionsRays().iterator();
    while (connectionRaysIter.hasNext()) {
      connectionRaysBuilder.append(connectionRaysIter.next().toString()
        + " ; ");
    }
    connectionRaysBuilder.deleteCharAt(connectionRaysBuilder.length() - 1);
    connectionRaysBuilder.deleteCharAt(connectionRaysBuilder.length() - 1);
    connectionRaysBuilder.deleteCharAt(connectionRaysBuilder.length() - 1);
    jsonWriter.name("connectionsRays").value(
      map.get_connectionsRays().toString().replaceAll("\\[", "").replaceAll(
        "\\]", "").replaceAll(", ", " ; "));

    // Connections value
    StringBuilder connectionsBuilder = new StringBuilder();
    Iterator<String> connectionsStringIter = map.get_connectionsList().iterator();
    while (connectionsStringIter.hasNext()) {
      connectionsBuilder.append(connectionsStringIter.next().toString()
        + " ; ");
    }
    connectionsBuilder.deleteCharAt(connectionsBuilder.length() - 1);
    connectionsBuilder.deleteCharAt(connectionsBuilder.length() - 1);
    connectionsBuilder.deleteCharAt(connectionsBuilder.length() - 1);
    jsonWriter.name("connectionsListKVs").value(
      map.get_connectionsList().toString().replaceAll("\\[", "").replaceAll(
        "\\]", "").replaceAll(", ", " ; "));

    // End ASCII object
    jsonWriter.endObject();

    // End root object
    jsonWriter.endObject();

    jsonWriter.close();
  }

  /*
   * 5,5,0,20 Which legend is: [stRow,stCol,compassDirection,length
   */

  /**
   * This method saves a RixkMap object to a file in JSON format.
   *
   * @param rixkMap  The object to be saved
   * @param filename The file to save to
   * @throws IOException Bad things that happened instead
   */
  public static void saveRixkMap(RixkMap rixkMap, String filename) throws IOException {
    JsonWriter jsonWriter = new JsonWriter(new FileWriter(new File(
      filename)));
    jsonWriter.setIndent("\t");

    // begin our root json object
    jsonWriter.beginObject();

    // Add starting army values for 2-6 players
    for (int i = 2; i <= 6; i++) {
      jsonWriter.name(i + "player").value(
        rixkMap.getStartingArmiesPerPlayer(i));
    }

    // Begin continents array
    jsonWriter.name("continents").beginArray();

    Iterator<Continent> continentIter = rixkMap.getContinentIterator();
    while (continentIter.hasNext()) {
      // Begin this continent object
      jsonWriter.beginObject();

      Continent currentContinent = continentIter.next();
      jsonWriter.name("name").value(currentContinent.getName());
      jsonWriter.name("bonus").value(currentContinent.getBonus());
      // Begin territories array
      jsonWriter.name("territories").beginArray();
      Iterator<Territory> territoryIter = currentContinent.getTerritoryIterator();
      while (territoryIter.hasNext()) {
        // Begin territory object
        jsonWriter.beginObject();
        Territory currentTerritory = territoryIter.next();
        jsonWriter.name("name").value(currentTerritory.getName());

        // Begin connections array
        jsonWriter.name("connections").beginArray();
        Iterator<Territory> adjacentIter = currentTerritory.getAdjacentTerritoryIterator();
        while (adjacentIter.hasNext()) {
          // Begin adjacent object
          jsonWriter.beginObject();
          Territory currentAdjacent = adjacentIter.next();
          jsonWriter.name("name").value(currentAdjacent.getName());

          // End adjacent object
          jsonWriter.endObject();
        }

        // End adjacent array
        jsonWriter.endArray();

        // End territory object
        jsonWriter.endObject();
      }
      // End territories array
      jsonWriter.endArray();
      // End this continent object
      jsonWriter.endObject();
    }

    // End continents array
    jsonWriter.endArray();

    // End root object
    jsonWriter.endObject();
    // Close writer
    jsonWriter.close();
  }

  /**
   * Validates that a map file is correctly formatted and then loads the
   * contents into a RixkMap object. Returns null if the RixkMap load failed.
   *
   * @param filename The map file to load in
   * @return A RixkMap as specified in the map file
   */
  public static RixkMap loadRixkMap(String filename) {
    // If filename is null return null right away
    if (filename == null) {
      return null;
    }

    RixkMap rixkMap = new RixkMap();
    Map<String, Territory> nameToTerritoryMap = new HashMap<>();
    JsonParser jsonParser = new JsonParser();
    JsonObject root = null;

    try {
      root = jsonParser.parse(new FileReader(new File(filename))).getAsJsonObject();

      // Set the starting Player army counts
      rixkMap.setStartingArmiesPerPlayer(2,
        root.get("2player").getAsInt());
      rixkMap.setStartingArmiesPerPlayer(3,
        root.get("3player").getAsInt());
      rixkMap.setStartingArmiesPerPlayer(4,
        root.get("4player").getAsInt());
      rixkMap.setStartingArmiesPerPlayer(5,
        root.get("5player").getAsInt());
      rixkMap.setStartingArmiesPerPlayer(6,
        root.get("6player").getAsInt());

      // Create Continent objects
      JsonArray continents = root.get("continents").getAsJsonArray();
      Iterator<JsonElement> continentIter = continents.iterator();
      while (continentIter.hasNext()) {
        // Get the current continent json object
        JsonObject currentContinentJson = continentIter.next().getAsJsonObject();

        // Get the name and bonus attributes
        String continentName = currentContinentJson.get("name").getAsString();
        int bonus = currentContinentJson.get("bonus").getAsInt();
        // Create a new Continent
        Continent continent = new Continent(continentName, bonus);

        // Create the Territory objects that belong to this Continent
        JsonArray territories = currentContinentJson.get("territories").getAsJsonArray();
        Iterator<JsonElement> territoryIter = territories.iterator();
        while (territoryIter.hasNext()) {
          // Get the current territory json object
          JsonObject currentTerritoryJson = territoryIter.next().getAsJsonObject();
          // Get the territory name
          String territoryName = currentTerritoryJson.get("name").getAsString();
          // Create the Territory object
          Territory territory = new Territory(territoryName);

          // Add the territory to the map by its name for ease of
          // access when adding territory connections
          nameToTerritoryMap.put(territoryName, territory);
          rixkMap.addNameToTerritoryMapping(territoryName, territory);

          // Add the Territory to the Continent
          continent.addTerritory(territory);
        }

        // Add the Continent to the RixkMap
        rixkMap.addContinent(continent);
      }

      // Add Territory connections
      continentIter = continents.iterator();
      while (continentIter.hasNext()) {
        // Get the current continent json object
        JsonObject currentContinentJson = continentIter.next().getAsJsonObject();

        // Create the Territory objects that belong to this Continent
        JsonArray territories = currentContinentJson.get("territories").getAsJsonArray();
        Iterator<JsonElement> territoryIter = territories.iterator();
        while (territoryIter.hasNext()) {
          // Get the current territory json object
          JsonObject currentTerritoryJson = territoryIter.next().getAsJsonObject();
          // Get the territory name
          String territoryName = currentTerritoryJson.get("name").getAsString();
          Territory currentTerritory = nameToTerritoryMap.get(territoryName);

          // Iterate through connections and add them
          Iterator<JsonElement> adjacentIter = currentTerritoryJson.get(
            "connections").getAsJsonArray().iterator();
          while (adjacentIter.hasNext()) {
            String adjacentName = adjacentIter.next().getAsJsonObject().get(
              "name").getAsString();
            currentTerritory.addAdjacentTerritory(nameToTerritoryMap.get(adjacentName));
          }
        }
      }
    } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
      e.printStackTrace();
    }

    return rixkMap;
  }

  /**
   * Returns a list of class names from a jar file.
   *
   * @param filename The jar file
   * @return The list of class names
   */
  public static List<String> getClassnamesFromJar(String filename) {
    List<String> classnames = new LinkedList<String>();

    JarFile jarFile;
    try {
      // Create a JarFile object
      jarFile = new JarFile(filename);
      // Loop through the files in the Jar file
      Enumeration<JarEntry> jarEntries = jarFile.entries();
      while (jarEntries.hasMoreElements()) {
        // Check if the current file is a .class file
        JarEntry current = jarEntries.nextElement();
        if (current.getName().endsWith(".class")) {
          // If it is then replace all slashes with dots and
          // remove the extension to put it in Class formatting
          String classname = current.getName().replaceAll("/", ".").replace(
            ".class", "");
          classnames.add(classname + " in " + filename);
        }
      }
      // Close jar file
      jarFile.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    return classnames;
  }

  /**
   * Loads and instantiates an AI Player object from a jar file as specified
   * by a file name. The file name should also be the name of the AI class
   * desired.
   *
   * @param filename The file name for the AI to load
   * @return A Player object that is an AI
   */
  public static Player loadAIFromJar(String filename) {
    // Get the filenames in the resources/RixkAI directory for .jar files
    String[] filenames = getFilenamesInDirectory(GameState.AI_DIRECTORY, GameState.AI_EXTENSION);

    // Loop through the files
    for (int i = 0; i < filenames.length; i++) {
      // Get the current file name
      String currentFilename = filenames[i];

      String classname = null;
      JarFile jarFile;
      try {
        // Create a JarFile object
        jarFile = new JarFile(filename);
        // Loop through the files in the Jar file
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
          // Check if the current file is a .class file
          JarEntry current = jarEntries.nextElement();
          if (current.getName().contains(".class")) {
            // If it is then replace all slashes with dots and
            // remove the extension to put it in Class formatting
            classname = current.getName().replaceAll("/", ".").replace(
              ".class", "");
          }
        }
        // Close jar file
        jarFile.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      try {
        // Get a Class object from the jar file and class name
        // String
        Class<?> clazz = loadClassFromJar(GameState.AI_DIRECTORY
          + currentFilename, classname);
        // Instantiate a Player from the Class object
        Player player = instantiatePlayerFromClass(clazz);
        // Return the Player
        return player;
      } catch (ClassNotFoundException | IOException
        | InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    // No Player class of the specified class name exists so return null
    return null;
  }

  public static Player loadAIFromClassname(String classname) {
    try {
      // Get a Class object from the jar file and class name
      // String
      Class<?> clazz = Class.forName(classname);
      // Instantiate a Player from the Class object
      Player player = instantiatePlayerFromClass(clazz);
      // Return the Player
      return player;
    } catch (ClassNotFoundException | InstantiationException
      | IllegalAccessException e) {
      e.printStackTrace();
    }

    // No Player class of the specified class name exists so return null
    return null;
  }

  public static Random getRng() {
    return rng;
  }

  /**
   * Instantiates a Player object specified by the class name found in a jar
   * file.
   *
   * @param filename  The jar file the class is found in
   * @param classname The class name of the class
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public static Player insantiatePlayerFromJar(String filename,
                                               String classname) throws InstantiationException,
    IllegalAccessException,
    ClassNotFoundException,
    IOException {
    return instantiatePlayerFromClass(loadClassFromJar(filename,
      classname));
  }

  private static Class<?> loadClassFromJar(String filename, String classname) throws ClassNotFoundException,
    IOException {
    // Make a File object for the jar file we want
    File file = new File(filename);
    // Get a URI for the file
    URI uri = file.toURI();
    URL[] urls = new URL[]{uri.toURL()};
    // Get a ClassLoader from the URI
    URLClassLoader cl = new URLClassLoader(urls);

    // Load a class with the loader using the class name
    Class<?> clazz = cl.loadClass(classname);
    // Close the class loader
    cl.close();
    // Return the Class object
    return clazz;
  }

  private static Player instantiatePlayerFromClass(Class<?> classname) throws InstantiationException,
    IllegalAccessException {
    // Return a new instance of class name and cast it to a Player
    return (Player) classname.newInstance();
  }


  private RixkUtils() {
  }
}
