package Cargo;

/**
 * Interface used for testing Cargo problem from the 2022 CITS2200 Project
 *
 * DO NOT MODIFY THIS FILE
 */
public interface Cargo {
  /**
   * A query to add a new job delivering cargo from one port to a later port.
   */
  public static class Query {
    /**
     * The total mass of new cargo.
     * May be set to negative to, for example, undo a previous query.
     */
    public int cargoMass;

    /**
     * The stop along our route at which the cargo is collected.
     */
    public int collect;

    /**
     * The stop along our route to which the cargo is to be delivered.
     * Can not be less than {@collect}.
     */
    public int deliver;
  }

  /**
   * After each query, compute the total cargo mass that will be on board when
   * departing from the port where we collected the new cargo.
   * @param  stops   the number of stops on the route
   * @param  queries a list of queries to be performed
   * @return         the total cargo on board after the corresponding query
   */
  public int[] departureMasses(int stops, Query[] queries);
}