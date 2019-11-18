package org.knowm.xchange.api.okex;

import org.knowm.xchange.okcoin.OkexExchangeV3;

public class OkexExchangeV3V1 extends OkexExchangeV3 {

  @Override
  protected void initServices() {
    super.initServices();

    this.accountService = new OkexAccountServiceV1(this);
    this.tradeService = new OkexTradeServiceV1(this);
  }
}
