package org.knowm.xchange.api.kraken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.kraken.service.KrakenTradeService;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

public class KrakenTradeServiceV1 extends KrakenTradeService {

  public KrakenTradeServiceV1(Exchange exchange) {
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
}
