package org.knowm.xchange.api.huobi;

public enum HuobiOrderType {
  BUY_LIMIT("buy-limit"),
  SELL_LIMIT("sell-limit"),
  BUY_IOC("buy-ioc"),
  SELL_IOC("sell-ioc");

  private String value;

  HuobiOrderType(String value) {

    this.value = value;
  }

  public String getValue() {

    return value;
  }

  @Override
  public String toString() {

    return this.getValue();
  }
}
