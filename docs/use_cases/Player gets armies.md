|  | Player gets armies |
| :------------------ | :------------------ |
| Description | Receive armies from territory and continent bonuses. Minimum of 3 armies received. Place these armies on owned territories. |
| Actor | Player |
| Assumptions | Game has been setup and players selected. |
| Preconditions | Player's turn. Own at least one territory. |
| Postconditions | Player has acquired all territory and bonus continent armies, and is ready to begin attack phase. |


**Main Success Scenario**

**1.** System notifies player that getting armies phase has begun.

**2.** System calculates number of player territories occupied.

**3.** System calculates number of territory armies to disperse to player (armies received = num. of owned territories / 3). 

**4.** System calculates number of player continents wholly occupied (armies received = token continent value).

**5.** System disperses territory armies and bonus continent armies to player.

**6.** System notifies player number of armies available.

**7.** Player places armies in controlled territories.

*Player repeats steps 6-7 until all armies placed.*

**8.** System notifies player that getting armies phase is complete.


**Extensions**

**5a.** If system calculated less than 3 territory armies for dispersion, then system disperses 3 (the minimum) territory armies.

**7a.** If player tries to place any army in non-controlled territory, system signals an error.


[end]



 