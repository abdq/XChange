package org.knowm.xchange.api.okex;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.okcoin.OkexAdaptersV3;
import org.knowm.xchange.okcoin.OkexExchangeV3;
import org.knowm.xchange.okcoin.v3.dto.account.OkexFundingAccountRecord;
import org.knowm.xchange.okcoin.v3.dto.account.OkexSpotAccountRecord;
import org.knowm.xchange.okcoin.v3.service.OkexAccountService;

public class OkexAccountServiceV1 extends OkexAccountService {

  public OkexAccountServiceV1(OkexExchangeV3 exchange) {
    super(exchange);
  }

  @Override
  public AccountInfo getAccountInfo() throws IOException {

    // return super.getAccountInfo();

    List<OkexFundingAccountRecord> funding = super.fundingAccountInformation();
    Collection<Balance> fundingBalances =
        funding.stream().map(OkexAdaptersV3::convert).collect(Collectors.toList());
    List<OkexSpotAccountRecord> spotTradingAccount = super.spotTradingAccount();
    Collection<Balance> tradingBalances =
        spotTradingAccount.stream().map(OkexAdaptersV3::convert).collect(Collectors.toList());

    return new AccountInfo(
        Wallet.Builder.from(fundingBalances)
            .features(Stream.of(Wallet.WalletFeature.FUNDING).collect(Collectors.toSet()))
            .id(Wallet.WalletFeature.FUNDING.name())
            .build(),
        Wallet.Builder.from(tradingBalances)
            .features(Stream.of(Wallet.WalletFeature.TRADING).collect(Collectors.toSet()))
            .id(Wallet.WalletFeature.TRADING.name())
            .build());
  }
}
