package org.knowm.xchange.api.bitfinex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.api.constant.OrderFlags;
import org.knowm.xchange.bitfinex.service.BitfinexTradeService;
import org.knowm.xchange.bitfinex.v1.dto.trade.BitfinexOrderFlags;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

public class BitfinexTradeServiceV1 extends BitfinexTradeService {

  public BitfinexTradeServiceV1(Exchange exchange) {
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

    Set<Order.IOrderFlags> orderFlags = limitOrder.getOrderFlags();
    if (orderFlags != null && orderFlags.contains(OrderFlags.FOK)) {
      orderFlags.add(BitfinexOrderFlags.FILL_OR_KILL);
    }
    if (orderFlags != null && orderFlags.contains(OrderFlags.HIDDEN)) {
      orderFlags.add(BitfinexOrderFlags.HIDDEN);
    }

    return super.placeLimitOrder(limitOrder);
  }
}
