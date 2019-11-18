package org.knowm.xchange.api.bitfinex;

import org.knowm.xchange.bitfinex.BitfinexExchange;

public class BitfinexExchangeV1 extends BitfinexExchange {

  @Override
  protected void initServices() {
    super.initServices();

    this.tradeService = new BitfinexTradeServiceV1(this);
  }
}
