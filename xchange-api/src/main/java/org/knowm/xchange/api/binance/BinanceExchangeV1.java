package org.knowm.xchange.api.binance;

import org.knowm.xchange.binance.BinanceExchange;

public class BinanceExchangeV1 extends BinanceExchange {

  @Override
  protected void initServices() {
    super.initServices();

    this.tradeService = new BinanceTradeServiceV1(this);
  }
}
