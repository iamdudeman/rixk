|  | Player fortifies territory |
| :------------------ | :------------------ |
| Description | Move armies from one owned territory to a connected player-controlled territory, leaving at least one army on the initial fortifying territory. |
| Actor | Player |
| Assumptions |  |
| Preconditions | Player's attack phase has ended. An owned territory has more than one army and is connected to another player controlled territory. |
| Postconditions | Both territories have at least one occupying army remaining. |


**Main Success Scenario**

**1.** System notifies player that fortifying phase has begun.

**2.** System asserts that at least one player-controlled territory contains two or more armies. 

**3.** Player enters a territory with two or more armies as starting point.

**4.** System asserts starting point territory is player-controlled.

**5.** System asserts starting point territory has at least one connecting player-controlled territory.

**6.** System asserts starting point territory has two or more armies. 

**7.** Player enters player-controlled territory as ending point.

**8.** System asserts ending point territory is player-controlled.  

**9.** System prompts player for amount of armies to move.

**10.** Player enters amount of armies to move.

**11.** System asserts requested army amount available.

**12.** System moves requested army amount to ending territory. 

**13.** System notifies player that fortifying phase is complete.


**Extensions**

**2a.** If no territory has two or more armies, system notifies player that no fortifications can be made. Fortification phase ends.

**4a.** If territory not player-controlled, system signals an error. 
   **1.** System notifies player that territory selected is not player controlled.
   **2.** Player enters new territory  
 
**5a.** If starting point territory does not have at least one connecting player controlled territory, system signals an error.
   **1.** System notifies player that starting point territory does not have at least one connecting player controlled territory.
   **2.** Player enters new territory. 

**6a.** if starting point territory starting point territory does not have two or more armies, system signals an error.
   **1.** System notifies player that starting point territory does not have two or more armies
   **2.** Player enters new territory

**8a.** same as 4a.  

**11a.** if requested army amount not available, system signals an error.
   **1.** System notifies player that requested army amount not available.
   **2.** Player enters new amount of armies.


[end]



 