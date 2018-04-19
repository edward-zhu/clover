package org.rikarika.pqs.ps4.model;

import java.util.List;

/**
 * Interfaces defines how a model talks to a view
 * 
 * <pre>
 * View	                Model
 * newGame()    ---->	
 *              <----   readyListChanged()
 *              <----	gameIsStarted()
 * startGame()  ---->
 * #move from this player/view#
 * move()       ---->	
 *              <----	chipIsAdded()
 * requestNextMove() ---->
 *              <----   readyListChanged()
 *              <----   allPlayersAreReadyForNextMove()
 * #move from other player/view#
 *              <---- chipIsAdded()
 * requestNextMove() ---->
 *              ...
 * #game is over#
 *              <---- 	gameIsOver()
 * </pre>
 * 
 * @author Jiadong Zhu
 */
public interface FourInARowListener {
  /**
   * New game is created.
   * 
   * @param first id of the player goes first
   */
  void newGameCreated(int first);

  /**
   * All players are ready to start the game.
   * 
   * @param first id of the player goes first
   */
  void gameIsStarted(int first);

  /**
   * The event that a new chip is added on the board
   * 
   * @param row
   * @param col
   * @param color
   */
  void chipIsAdded(int row, int col, int player);

  /**
   * A player is ready for next action
   * 
   * (now only used in the net client view)
   * 
   * @param readyList updated ready list (list of players' IDs)
   */
  void readyListChanged(List<Integer> readyList);

  /**
   * All players are ready for next move
   * 
   * @param next next player to move
   */
  void allPlayersAreReadyForNextMove(int next);

  /**
   * This round of game is over
   * 
   * @param winner the winner of this round, {@code winner = -1} when it's a tie.
   */
  void gameIsOver(int winner);
}
