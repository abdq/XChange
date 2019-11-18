package org.knowm.xchange.api.huobi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.api.constant.OrderFlags;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.trade.HuobiCreateOrderRequest;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrderResult;
import org.knowm.xchange.huobi.service.HuobiAccountServiceRaw;
import org.knowm.xchange.huobi.service.HuobiDigest;
import org.knowm.xchange.huobi.service.HuobiTradeService;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

public class HuobiTradeServiceV1 extends HuobiTradeService {

  public HuobiTradeServiceV1(Exchange exchange) {
    super(exchange);
  }

  @Override
  public Collection<Order> getOrder(OrderQueryParams... orderQueryParams) throws IOException {

    List<String> orderIs = new ArrayList<>();
    for (OrderQueryParams queryParams : orderQueryParams) {
      orderIs.add(queryParams.getOrderId());
    }

    return getOrder(orderIs.toArray(new String[0]));
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {

    // return super.placeLimitOrder(limitOrder);

    String type = adaptOrderType(limitOrder.getType(), limitOrder.getOrderFlags()).getValue();

    HuobiOrderResult result =
        huobi.placeLimitOrder(
            new HuobiCreateOrderRequest(
                getAccountId(),
                limitOrder.getOriginalAmount().toString(),
                limitOrder.getLimitPrice().toString(),
                HuobiUtils.createHuobiCurrencyPair(limitOrder.getCurrencyPair()),
                type),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);

    return checkResult(result);
  }

  private HuobiOrderType adaptOrderType(Order.OrderType orderType, Set<Order.IOrderFlags> flags) {
    if (orderType == Order.OrderType.BID) {
      if (flags == null || flags.isEmpty()) {
        return HuobiOrderType.BUY_LIMIT;
      } else if (flags.contains(OrderFlags.IOC)) {
        return HuobiOrderType.BUY_IOC;
      }
    } else if (orderType == Order.OrderType.ASK) {
      if (flags == null || flags.isEmpty()) {
        return HuobiOrderType.SELL_LIMIT;
      } else if (flags.contains(OrderFlags.IOC)) {
        return HuobiOrderType.SELL_IOC;
      }
    }

    throw new ExchangeException("Unsupported order type.");
  }

  private String getAccountId() throws IOException {
    return String.valueOf(
        ((HuobiAccountServiceRaw) exchange.getAccountService()).getAccounts()[0].getId());
  }
}
