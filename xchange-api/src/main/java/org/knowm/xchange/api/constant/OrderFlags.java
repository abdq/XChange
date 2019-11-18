package org.knowm.xchange.api.constant;

import org.knowm.xchange.dto.Order;

public enum OrderFlags implements Order.IOrderFlags {

  /**
   * This is an order which does not appear in the orderbook, and thus doesn't influence other
   * market participants. the taker fee will apply to any trades.
   */
  HIDDEN,

  /** Immediate or Cancel */
  IOC,

  /** Fill or Kill */
  FOK;
}
