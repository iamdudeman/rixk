|  | Game setup |
| :------------------ | :------------------ |
| Description | Player selects the map to play on, the number of players in game, and the system initializes the map. |
| Actor | Player |
| Assumptions | None |
| Preconditions | A valid map file exists. A valid AI module exists. |
| Postconditions | First player's turn is ready to begin. |


**Main Success Scenario**

**1.** System notifies Player of map choices.

**2.** Player enters map choice.

**3.** System loads and initializes the map. *(possible sub-usecase)*

**4.** System notifies player of player count.

**5.** System determines player rotation order for placing armies.

**6.** System notifies each player in turn of remaining initial armies to place.

**7.** Player places army on an owned territory.

*Repeat steps **6-7** until all players' armies have been placed.*

**8.** System determines turn order for playing the game.

**9.** System notifies all players of turn order. 

**10.** System notifies first player to begin.


**Extensions**

**4a.** If amount entered is invalid then system signals an error. Play re-enters count.

**7a.** If territory chosen is invalid, then system signals an error. Play re-enters territory.

 [end]