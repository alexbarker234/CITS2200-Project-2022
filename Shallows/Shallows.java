package Shallows;

/**
 * Interface used for testing Shallows problem from the 2022 CITS2200 Project
 *
 * DO NOT MODIFY THIS FILE
 */
public interface Shallows {
  /**
   * A shipping lane that permits transit from one port to another with a known
   * minimum depth
   */
  public static class Lane {
    /**
     * The index of the port from which this shipping lane departs
     */
    public int depart;

    /**
     * The index of the port at which this shipping lane arrives
     */
    public int arrive;

    /**
     * The minimum depth along the shipping lane
     * Ships with a draught deeper than this are unable to use this lane
     */
    public int depth;
  }

  /**
   * Starting from a specified origin port, compute for each other port the
   * maximum draught a ship can have while still being able to travel from the
   * origin port to that port by some sequence of shipping lanes
   * @param  ports  the number of ports
   * @param  lanes  a list of shipping lanes
   * @param  origin the index of the origin port
   * @return        the maximum draught a ship can have while still being able
   *                to reach each port ({@code Integer.MAX_VALUE} for origin,
   *                and 0 for unreachable ports)
   */
  public int[] maximumDraughts(int ports, Lane[] lanes, int origin);
}