package org.knowm.xchange.api.okex;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.knowm.xchange.api.constant.OrderFlags;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.okcoin.OkexAdaptersV3;
import org.knowm.xchange.okcoin.OkexExchangeV3;
import org.knowm.xchange.okcoin.v3.dto.trade.*;
import org.knowm.xchange.okcoin.v3.service.OkexTradeService;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

public class OkexTradeServiceV1 extends OkexTradeService {

  public OkexTradeServiceV1(OkexExchangeV3 exchange) {
    super(exchange);
  }

  @Override
  public Collection<Order> getOrder(OrderQueryParams... orderQueryParams) throws IOException {

    Collection<Order> orders = new ArrayList<>();
    for (OrderQueryParams orderQueryParam : orderQueryParams) {

      OrderQueryParamCurrencyPair orderQueryParamCurrencyPair =
          (OrderQueryParamCurrencyPair) orderQueryParam;
      final String instrumentId =
          OkexAdaptersV3.toSpotInstrument(orderQueryParamCurrencyPair.getCurrencyPair());
      OkexOpenOrder okexOpenOrder =
          okex.getSpotOrder(
              apikey, digest, timestamp(), passphrase, orderQueryParam.getOrderId(), instrumentId);
      System.out.println(okexOpenOrder);
      orders.add(adaptOrder(okexOpenOrder));
    }

    return orders;
  }

  @Override
  public String placeLimitOrder(LimitOrder o) throws IOException {

    // return super.placeLimitOrder(o);

    OrderPlacementType orderType = adaptOrderType(o.getOrderFlags());

    SpotOrderPlacementRequest req =
        SpotOrderPlacementRequest.builder()
            .instrumentId(OkexAdaptersV3.toSpotInstrument(o.getCurrencyPair()))
            .price(o.getLimitPrice())
            .size(o.getOriginalAmount())
            .side(o.getType() == Order.OrderType.ASK ? Side.sell : Side.buy)
            .orderType(orderType)
            .build();
    OrderPlacementResponse placed = spotPlaceOrder(req);
    return placed.getOrderId();
  }

  @Override
  public String placeMarketOrder(MarketOrder o) throws IOException {
    SpotOrderPlacementRequest req =
        SpotOrderPlacementRequest.builder()
            .instrumentId(OkexAdaptersV3.toSpotInstrument(o.getCurrencyPair()))
            /*.size(o.getOriginalAmount())
            .notional(o.getOriginalAmount())*/
            .side(o.getType() == Order.OrderType.ASK ? Side.sell : Side.buy)
            .type("market")
            .build();

    if (req.getSide() == Side.buy) {
      req.setNotional(o.getOriginalAmount());
    } else {
      req.setSize(o.getOriginalAmount());
    }

    OrderPlacementResponse placed = spotPlaceOrder(req);
    return placed.getOrderId();
  }

  private OrderPlacementType adaptOrderType(Set<Order.IOrderFlags> flags) {

    if (flags == null || flags.isEmpty()) {
      return OrderPlacementType.normal;
    } else if (flags.contains(OrderFlags.IOC)) {
      return OrderPlacementType.immediate_or_cancel;
    }

    throw new ExchangeException("Unsupported order type.");
  }

  private static Order adaptOrder(OkexOpenOrder order) {
    Order.OrderType type = order.getSide() == Side.sell ? Order.OrderType.ASK : Order.OrderType.BID;
    CurrencyPair currencyPair = OkexAdaptersV3.toPair(order.getInstrumentId());
    Order.Builder builder;
    if ("market".equalsIgnoreCase(order.getType())) {
      builder = new MarketOrder.Builder(type, currencyPair);
    } else {
      builder = new LimitOrder.Builder(type, currencyPair).limitPrice(order.getPrice());
    }
    builder
        .orderStatus(adaptOrderStatus(order.getState()))
        .originalAmount(order.getSize())
        .id(order.getOrderId())
        .timestamp(order.getTimestamp())
        .cumulativeAmount(order.getFilledSize());
    if (order.getFilledSize() != null
        && order.getFilledNotional() != null
        && order.getFilledSize().compareTo(BigDecimal.ZERO) > 0
        && order.getFilledNotional().compareTo(BigDecimal.ZERO) > 0) {
      builder.averagePrice(
          order.getFilledSize().divide(order.getFilledNotional(), 8, RoundingMode.HALF_EVEN));
    }
    if (order.getOrderType() != null) {
      builder.flag(order.getOrderType() == OrderPlacementType.normal ? null : OrderFlags.IOC);
    }
    return builder.build();
  }

  /**
   * @param orderStatus
   * @return
   */
  public static Order.OrderStatus adaptOrderStatus(String orderStatus) {
    switch (orderStatus) {
      case "-2":
        return Order.OrderStatus.EXPIRED;
      case "-1":
        return Order.OrderStatus.CANCELED;
      case "0":
        return Order.OrderStatus.NEW;
      case "1":
        return Order.OrderStatus.PARTIALLY_FILLED;
      case "2":
        return Order.OrderStatus.FILLED;
      case "3":
        return Order.OrderStatus.PENDING_NEW;
      case "4":
        return Order.OrderStatus.PENDING_CANCEL;
      default:
        return Order.OrderStatus.UNKNOWN;
    }
  }
}
