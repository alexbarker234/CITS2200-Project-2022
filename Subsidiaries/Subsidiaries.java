package Subsidiaries;

/**
 * Interface used for testing Subsidiaries problem from the 2022 CITS2200 Project
 *
 * DO NOT MODIFY THIS FILE
 */
public interface Subsidiaries {
  /**
   * A query to find what company, if any, a payment is internal to
   */
  public static class Query {
    /**
     * The id of the paying company
     */
    public int payer;

    /**
     * The id of the company being paid
     */
    public int payee;
  }

  /**
   * Given the company that owns each other company, and a list of queries
   * about payments from one company to another, compute what company those
   * payments are internal to, if any.
   * @param  owners  the company id of each company's owner, or -1 if none
   * @param  queries a list of payments from one company to another
   * @return         the smallest company the payment is internal to, or -1
   *                 if there is no such company
   */
  public int[] sharedOwners(int[] owners, Query[] queries);
}