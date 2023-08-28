package velox.api.layer1.simpledemo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import velox.api.layer1.simplified.MarketByOrderDepthDataListener;

public class OrderBook implements MarketByOrderDepthDataListener{
	public static class Order {

        public String orderId;
        public boolean isBid;
        public int price;
        public int size;

        public Order(String orderId, boolean isBid, int price, int size) {
            this.orderId = orderId;
            this.isBid = isBid;
            this.price = price;
            this.size = size;
        }
    }
	public final TreeMap<Integer, Map<String, Order>> asks = new TreeMap<>((a, b) -> a - b);
    public final TreeMap<Integer, Map<String, Order>> bids = new TreeMap<>((a, b) -> b - a);
    public final Map<String, Order> orders = new HashMap<>();
    
    @Override
    public void cancel(String orderId) {
        Order order = orders.remove(orderId);
        Map<String, Order> priceLevel = (order.isBid ? bids : asks).get(order.price);
        priceLevel.remove(orderId);
        if (priceLevel.isEmpty()) {
            (order.isBid ? bids : asks).remove(order.price);
        }
    }

	@Override
    public void replace(String orderId, int price, int size) {
        Order order = orders.get(orderId);
        if (price == order.price && size < order.size) {
            order.size = size;
        } else { // if order size increased, move the order to the end of the queue regardless of price change
            cancel(orderId);
            send(orderId, order.isBid, price, size);
        }
    }

	@Override
    public void send(String orderId, boolean isBid, int price, int size) {
        Order order = new Order(orderId, isBid, price, size);
        orders.put(orderId, order);
        (isBid ? bids : asks).computeIfAbsent(price, k -> new LinkedHashMap<>()).put(orderId, order);
    }
}
