package velox.api.layer1.simpledemo;

public class Vwap {

    public double priceSize = 0;
    public long volume = 0;

    public void onTrade(double price, int size) {
        priceSize += price * size;
        volume += size;
    }
}
