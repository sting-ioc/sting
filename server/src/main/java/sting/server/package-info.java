/**
 * JTA-backed transaction annotations for Sting service-interface boundaries.
 *
 * <p>Applications provide a {@link javax.transaction.TransactionManager} as a Sting service and annotate published
 * service interfaces or injectable implementation types with {@link sting.server.Transactional}.</p>
 */
package sting.server;
