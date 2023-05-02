package Fines;

/**
 * Interface used for testing Fines problem from the 2022 CITS2200 Project
 *
 * DO NOT MODIFY THIS FILE
 */
public interface Fines {
  /**
   * Given the priorities of each ship that passed through a narrows in the
   * order they passed, compute how many times a lower priority ship cut off a
   * higher priority ship. Each ship cut off counts as a separate instance.
   * Each instance will result in a fine.
   * @param  priorities the priorities of each ship in the order they passed
   * @return            the total number of fines to be issued
   */
  public int countFines(int[] priorities);
}