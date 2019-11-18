package org.knowm.xchange.api.kraken;

import java.io.IOException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.kraken.KrakenAdapters;
import org.knowm.xchange.kraken.service.KrakenAccountService;

public class KrakenAccountServiceV1 extends KrakenAccountService {

  public KrakenAccountServiceV1(Exchange exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {

    // return super.getAccountInfo();

    Wallet tradingWallet = KrakenAdapters.adaptWallet(getKrakenBalance());

    return new AccountInfo(tradingWallet);
  }
}
