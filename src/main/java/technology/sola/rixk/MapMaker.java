package technology.sola.rixk;

import technology.sola.rixk.model.ASCII_RixkMap;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MapMaker {
  public static void main(String[] args) throws IOException {
    System.setProperty("SHOULD_LOG", "true");
    boolean done = true;

    do {
      try {
        ASCII_RixkMap map = new ASCII_RixkMap("Tim's Visual Map", 200, 150, 9);
        RixkUtils.saveASCIIRixkMap(map);
        Scanner sc = new Scanner(new File("resources/RixkMaps/Tim's Visual Map.json"));
        if (sc.useDelimiter("\\Z").next().contains("n/a")) {
          sc.close();
          System.out.println("Contained n/a");
          throw new Exception();
        }
        sc.close();
      } catch (Exception e) {
        System.out.println("Map creation failed");
        done = false;
      }
    } while (!done);

    System.out.println("Map created");
  }
}
