package technology.sola.rixk.model.player;

import technology.sola.rixk.model.Territory;
import technology.sola.rixk.view.cli.CliInput;
import technology.sola.rixk.view.cli.CliOutput;

import java.util.*;

/**
 * CliHumanPlayer This represents a human player playing the game from the
 * command line.
 *
 * @author Tim Solum
 * @version $Revision$ ($Author$)
 */
public class CliHumanPlayer implements Player {
  private List<Territory> territories;

  private int armiesToPlace = 0;

  private CliInput input;

  private CliOutput output;

  public CliHumanPlayer() {
    territories = new LinkedList<>();
    input = new CliInput(System.in);
    output = new CliOutput(System.out);
  }

  @Override
  public boolean ownsTerritory(Territory territory) {
    return territories.contains(territory);
  }

  @Override
  public int getArmiesToPlace() {
    return armiesToPlace;
  }

  @Override
  public void removeArmiesToPlace(int armiesPlaced) {
    armiesToPlace -= armiesPlaced;
  }

  @Override
  public int getTotalOwnedTerritories() {
    return territories.size();
  }

  @Override
  public void addTerritory(Territory territory) {
    territories.add(territory);
  }

  @Override
  public void addArmiesToPlace(int armiesAdded) {
    armiesToPlace += armiesAdded;
  }

  @Override
  public void placeArmies() {
    while (getArmiesToPlace() > 0) {
      Integer choice = null;
      do {
        output.write("You have " + getArmiesToPlace() + " armies to place\n");
        printTerritoryOptions(getOwnedTerritories());
        choice = getIntegerChoice();
        if (choice < 0 || choice > getOwnedTerritories().size()) {
          output.write("Invalid choice!\n");
          choice = null;
        }
      } while (choice == null);

      getOwnedTerritories().get(choice - 1).addArmies(1);
      removeArmiesToPlace(1);
    }
  }

  @Override
  public void placeOneArmy() {
    Integer choice = null;
    do {
      output.write("You have " + getArmiesToPlace() + " armies to place\n");
      printTerritoryOptions(getOwnedTerritories());
      choice = getIntegerChoice();
      if (choice < 0 || choice > getOwnedTerritories().size()) {
        output.write("Invalid choice!\n");
        choice = null;
      }
    } while (choice == null);

    getOwnedTerritories().get(choice - 1).addArmies(1);
    removeArmiesToPlace(1);
  }

  @Override
  public List<Territory> getOwnedTerritories() {
    return territories;
  }

  @Override
  public String getName() {
    return "Human";
  }

  @Override
  public Territory chooseTarget(Territory[] territories) {
    Integer choice = null;
    do {
      output.write("Choose target to attack\n");
      printTerritoryOptions(new ArrayList<Territory>(Arrays.asList(territories)));

      choice = getIntegerChoice();
      if (choice < 0 || choice > territories.length) {
        output.write("Invalid choice!\n");
        choice = null;
      }
    } while (choice == null);

    return territories[choice - 1];

  }

  @Override
  public Territory chooseAttacker(List<Territory> attackPoints) {
    Integer choice = null;
    do {
      output.write("Choose attacking territory\n");
      printTerritoryOptions(attackPoints);
      choice = getIntegerChoice();
      if (choice < 0 || choice > attackPoints.size()) {
        output.write("Invalid choice!\n");
        choice = null;
      }
    } while (choice == null);

    return attackPoints.get(choice - 1);
  }

  @Override
  public int takeOver(int min, int max) {
    output.write("You have taken over a territory\n");
    Integer choice = null;
    do {
      output.write("You can move " + min + " to " + max + " armies\n");
      output.write("Choice? ");
      choice = input.getInteger();
      if (choice != null) {
        if (choice < min || choice > max) {
          output.write("Invalid choice!\n");
          choice = null;
        }
      }
    }
    while (choice == null);
    return choice;
  }

  @Override
  public boolean wantToAttack() {
    output.write("Do you want to attack? ");
    return input.getBoolean();
  }

  @Override
  public int chooseRoll(boolean attacking,
                        int min,
                        int max,
                        Territory attacker,
                        Territory defender) {
    if (attacking) {
      output.write("Your " + attacker.getName() + " is attacking "
        + defender.getName() + "\n");
    } else {
      output.write("Your " + defender.getName()
        + " is under attack by " + attacker.getName() + "\n");
    }

    Integer choice = null;
    do {
      output.write("You can roll " + min + " to " + max + " dice\n");
      output.write("Choice? ");
      choice = input.getInteger();
      if (choice != null) {
        if (choice < min || choice > max) {
          output.write("Invalid choice!\n");
          choice = null;
        }
      }
    }
    while (choice == null);
    return choice;
  }

  @Override
  public Territory selectFortifyTarget(List<Territory> fortifiableTerritories) {
    Integer choice = null;
    do {
      output.write("Choose a territory to fortify\n");
      printTerritoryOptions(fortifiableTerritories);
      choice = getIntegerChoice();
      if (choice < 0 || choice > fortifiableTerritories.size()) {
        output.write("Invalid choice!\n");
        choice = null;
      }
    } while (choice == null);
    return fortifiableTerritories.get(choice - 1);
  }

  @Override
  public Territory selectFortifyingTerritory(List<Territory> possibleFortifiers) {
    Integer choice = null;
    do {
      output.write("Choose a territory to fortify from\n");
      printTerritoryOptions(possibleFortifiers);
      choice = getIntegerChoice();
      if (choice < 0 || choice > possibleFortifiers.size()) {
        output.write("Invalid choice!\n");
        choice = null;
      }
    } while (choice == null);
    return possibleFortifiers.get(choice - 1);
  }

  @Override
  public int fortify(int min, int max) {
    output.write("Choose amount of armies to fortify with\n");
    Integer choice = null;
    do {
      output.write("You can move " + min + " to " + max + " armies\n");
      output.write("Choice? ");
      choice = input.getInteger();
      if (choice != null) {
        if (choice < min || choice > max) {
          output.write("Invalid choice!\n");
          choice = null;
        }
      }
    }
    while (choice == null);
    return choice;
  }

  @Override
  public void removeTerritory(Territory territory) {
    territories.remove(territory);
  }


  private void printTerritoryOptions(List<Territory> territories) {
    int index = 0;
    Iterator<Territory> territoryIter = territories.iterator();
    while (territoryIter.hasNext()) {
      Territory current = territoryIter.next();
      output.write(String.format("[%2d]: %s\n", index + 1,
        current.getName()));
      index++;
    }
  }

  private Integer getIntegerChoice() {
    Integer choice = null;
    do {
      output.write("Choice? ");
      choice = input.getInteger();
      if (choice == null) {
        output.write("Invalid choice!\n");
      }
    }
    while (choice == null);

    return choice;
  }
}
