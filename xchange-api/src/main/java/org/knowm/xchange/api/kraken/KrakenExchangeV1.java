package org.knowm.xchange.api.kraken;

import org.knowm.xchange.kraken.KrakenExchange;

public class KrakenExchangeV1 extends KrakenExchange {

  @Override
  protected void initServices() {
    super.initServices();

    this.accountService = new KrakenAccountServiceV1(this);
    this.tradeService = new KrakenTradeServiceV1(this);
  }
}
