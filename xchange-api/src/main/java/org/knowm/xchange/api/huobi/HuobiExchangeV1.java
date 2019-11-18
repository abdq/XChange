package org.knowm.xchange.api.huobi;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.meta.FeeTier;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.huobi.HuobiExchange;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.marketdata.HuobiAsset;
import org.knowm.xchange.huobi.dto.marketdata.HuobiAssetPair;
import org.knowm.xchange.huobi.service.HuobiMarketDataServiceRaw;

public class HuobiExchangeV1 extends HuobiExchange {

  private static BigDecimal fee = new BigDecimal("0.002"); // Trading fee at Huobi is 0.2 %

  @Override
  protected void initServices() {
    super.initServices();

    this.tradeService = new HuobiTradeServiceV1(this);
  }

  @Override
  public void remoteInit() throws IOException, ExchangeException {

    // super.remoteInit();

    HuobiAssetPair[] assetPairs =
        ((HuobiMarketDataServiceRaw) marketDataService).getHuobiAssetPairs();
    HuobiAsset[] assets = ((HuobiMarketDataServiceRaw) marketDataService).getHuobiAssets();

    exchangeMetaData = adaptToExchangeMetaData(assetPairs, assets, exchangeMetaData);
  }

  private ExchangeMetaData adaptToExchangeMetaData(
      HuobiAssetPair[] assetPairs, HuobiAsset[] assets, ExchangeMetaData staticMetaData) {

    HuobiUtils.setHuobiAssets(assets);
    HuobiUtils.setHuobiAssetPairs(assetPairs);

    Map<CurrencyPair, CurrencyPairMetaData> pairsMetaData = staticMetaData.getCurrencyPairs();
    Map<CurrencyPair, CurrencyPairMetaData> pairs = new HashMap<>();
    for (HuobiAssetPair assetPair : assetPairs) {

      CurrencyPair pair = HuobiUtils.translateHuobiCurrencyPair(assetPair.getKey());

      CurrencyPairMetaData metadata = pairsMetaData.get(pair);
      BigDecimal minQty =
          metadata == null
              ? null
              : metadata
                  .getMinimumAmount()
                  .setScale(Integer.parseInt(assetPair.getAmountPrecision()), RoundingMode.DOWN);
      FeeTier[] feeTiers = metadata == null ? null : metadata.getFeeTiers();

      CurrencyPairMetaData newMetaData =
          new CurrencyPairMetaData(
              fee,
              minQty,
              null,
              null,
              null,
              new Integer(assetPair.getAmountPrecision()),
              new Integer(assetPair.getPricePrecision()),
              feeTiers,
              null,
              null,
              true);

      pairs.put(pair, newMetaData);
    }

    Map<Currency, CurrencyMetaData> currenciesMetaData = staticMetaData.getCurrencies();
    Map<Currency, CurrencyMetaData> currencies = new HashMap<>();
    for (HuobiAsset asset : assets) {
      Currency currency = HuobiUtils.translateHuobiCurrencyCode(asset.getAsset());
      CurrencyMetaData metadata = currenciesMetaData.getOrDefault(currency, null);
      BigDecimal withdrawalFee = metadata == null ? null : metadata.getWithdrawalFee();
      int scale = metadata == null ? 8 : metadata.getScale();
      currencies.put(currency, new CurrencyMetaData(scale, withdrawalFee));
    }

    return new ExchangeMetaData(pairs, currencies, null, null, false);
  }
}
