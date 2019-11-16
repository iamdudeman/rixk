|  | Player attacks |
| :------------------ | :------------------ |
| Description | One Player attacks another Player's territory. This ends the game if it is the last territory not owned by the Attacking Player. |
| Actor | Attacking Player, Defending Player |
| Assumptions | None |
| Preconditions | Attacking player owns at least one territory with more than one army. |
| Postconditions | Player is ready to fortify. |


**Main Success Scenario**

**1.** System notifies player that attack phase has begun.

**2.** Player selects owned territory to attack from.

**3.** Player enters, n, amount of armies to commit to attack (1-3).

**4.** System alerts that territory is being attacked.

**5.** Defending player enters, f, amount of troops to defend (1-2).

**6.** Player enters, x, number of times to attack.

**7.** System rolls n attack dice.

**8.** System rolls f defense dice.

**9.** System calculates how many armies each side lost.

**10.** System notifies players of armies lost.

**11.** System asserts attack can still continue.

*Repeat 7-10 until Defending troops is zero or Attacking troops is one or x number of attacks happened.*

**12.** System moves n troops to territory, if conquered.

**13.** System asserts that enemy territories are still available

*Repeat steps 2-12 until Player chooses End or game ends*

**14** System notifies player that attack phase is complete.

**Extensions**

**2a.** If owned territory does not have valid, adjacent attack target. System notifies of invalid choice and re-enter territory choice.

**2b.** If owned territory only has one army, System notifies of not enough armies to attack and re-enter owned territory choice.

**2c.** If player selects to pass, system notifies player that attack phase is complete. 

**3a.** Player selects invalid amount of armies.

- **1.** If no armies remain on owned territory. Notify player of error. Re-enter territory choice.
    
- **2.** If more than 3 armies, notify player of error and re-enter territory choice.

**5a.** Player selects invalid amount of armies.

- **1.** If less than 1 army, notify player of error and re-enter territory choice.
    
- **2.** If more than 2 armies, notify player of error and re-enter territory choice.

**11a.** Attack cannot still continue.

- **1.** System notifies player that current attack cannot continue.

- **2.** Player selects owned territory to attack from (i.e., for new attack).
