package org.knowm.xchange.api.binance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.api.constant.OrderFlags;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.BinanceErrorAdapter;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.trade.BinanceNewOrder;
import org.knowm.xchange.binance.dto.trade.OrderType;
import org.knowm.xchange.binance.dto.trade.TimeInForce;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;

public class BinanceTradeServiceV1 extends BinanceTradeService {

  public BinanceTradeServiceV1(Exchange exchange) {
    super(exchange);
  }

  @Override
  public String placeLimitOrder(LimitOrder lo) throws IOException {

    // return super.placeLimitOrder(lo);

    TimeInForce tif = adaptOrderType(lo.getOrderFlags());

    try {
      Long recvWindow =
          (Long)
              exchange.getExchangeSpecification().getExchangeSpecificParametersItem("recvWindow");

      BigDecimal quantity = lo.getOriginalAmount();
      BigDecimal icebergQty = null;
      if (lo.getCumulativeAmount() != null
          && lo.getCumulativeAmount().compareTo(lo.getOriginalAmount()) > 0) {
        quantity = lo.getCumulativeAmount();
        icebergQty = lo.getOriginalAmount();
      }

      BinanceNewOrder newOrder =
          newOrder(
              lo.getCurrencyPair(),
              BinanceAdapters.convert(lo.getType()),
              OrderType.LIMIT,
              tif,
              quantity,
              lo.getLimitPrice(),
              lo.getId(),
              null,
              icebergQty,
              recvWindow,
              getTimestamp());
      return Long.toString(newOrder.orderId);
    } catch (BinanceException e) {
      throw BinanceErrorAdapter.adapt(e);
    }
  }

  private TimeInForce adaptOrderType(Set<Order.IOrderFlags> flags) {

    if (flags == null || flags.isEmpty()) {
      return TimeInForce.GTC;
    } else if (flags.contains(OrderFlags.IOC)) {
      return TimeInForce.IOC;
    }

    throw new ExchangeException("Unsupported order type.");
  }
}
