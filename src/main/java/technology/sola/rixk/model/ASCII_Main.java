package technology.sola.rixk.model;

import java.util.Scanner;

/**
 * Just a top level run method that lets the user input map parameters manually.
 *
 * @author Brint
 * @version Created 3/19/15
 */
public class ASCII_Main {


  public static void main_ASCIIMap(String[] args) {  // used to be the main() class.

    System.out.println("ASCII_map program started.");

    String userText = receiveInputFromUser("Enter map parameters (name,h,w,# continents):") ; // name,h,w,#_continents
    //userText = "foo,50,180,10";	System.out.println( "NOTE: predefined user text being used.");

    ASCII_RixkMap map = new ASCII_RixkMap(userText); // build map

    System.out.println( map.toString() ); // show the map in the console.



  }

  /**
   * Use the console to get user input
   *
   * @param message
   *            text to communicate to the user.
   * @return the text the user enters.
   */
  public static String receiveInputFromUser(String message) {

    String userText;
    Scanner scanIn = new Scanner(System.in);

    System.out.println(message);

    userText = scanIn.nextLine();
    scanIn.close();

    return userText;
  }

}

