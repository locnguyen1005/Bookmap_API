package velox.api.layer1.simpledemo;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.print.attribute.Size2DSyntax;

import velox.api.layer1.annotations.Layer1ApiVersion;
import velox.api.layer1.annotations.Layer1ApiVersionValue;
import velox.api.layer1.annotations.Layer1SimpleAttachable;
import velox.api.layer1.annotations.Layer1StrategyName;
import velox.api.layer1.common.Log;
import velox.api.layer1.data.InstrumentInfo;
import velox.api.layer1.data.TradeInfo;
import velox.api.layer1.layers.utils.OrderBook;
import velox.api.layer1.messages.historicalserver.Layer1ApiRestartHistoricalServer;
import velox.api.layer1.messages.indicators.Layer1ApiUserMessageModifyIndicator.GraphType;
import velox.api.layer1.simplified.Api;
import velox.api.layer1.simplified.Bar;
import velox.api.layer1.simplified.BarDataListener;
import velox.api.layer1.simplified.BboListener;
import velox.api.layer1.simplified.CustomModule;
import velox.api.layer1.simplified.DepthDataListener;
import velox.api.layer1.simplified.HistoricalModeListener;
import velox.api.layer1.simplified.Indicator;
import velox.api.layer1.simplified.InitialState;
import velox.api.layer1.simplified.IntervalListener;
import velox.api.layer1.simplified.Intervals;
import velox.api.layer1.simplified.MarketByOrderDepthDataListener;
import velox.api.layer1.simplified.Parameter;
import velox.api.layer1.simplified.TradeDataListener;

@Layer1SimpleAttachable
@Layer1StrategyName("Hello Bookmap API with ")
@Layer1ApiVersion(Layer1ApiVersionValue.VERSION1)
public class HelloBookmapApiWithLogs
		implements CustomModule, TradeDataListener, HistoricalModeListener, BarDataListener {

	private Indicator indicator;
	private Indicator indicator2;
	private LocalDate date = LocalDate.now();
	@Override
	public void initialize(String alias, InstrumentInfo info, Api api, InitialState initialState) {
		indicator = api.registerIndicator("ema", GraphType.PRIMARY);
		indicator.setColor(Color.green);
	}

	@Override
	public void onTrade(double price, int size, TradeInfo tradeInfo) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CompletableFuture<Void> getData = CompletableFuture.runAsync(() -> {
			if (tradeInfo.aggressorOrderId != null) {
				writeToFile("D:\\aggregated.txt", tradeInfo.aggressorOrderId, size, price, tradeInfo.isBidAggressor );
				indicator.addPoint(Double.parseDouble(tradeInfo.aggressorOrderId));
			}
		});

	}
	@Override
	public void stop() {

	}
	@Override
	public void onRealtimeStart() {

	}
	public static void writeToFile(String filename, String value, int size, double price, boolean isbid) {
		try (FileWriter writer = new FileWriter(filename, true)) {
			LocalDate gDate = LocalDate.now();
			writer.write("Date: "+ LocalTime.now()+"-"+LocalTime.now().getHour()+":"+LocalTime.now().getMinute() +":"+LocalTime.now().getSecond() +":"+ "  |id: " +value + "   |price: " + price + "  |size: " + size + "  |isbid:" + isbid + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long getInterval() {
		return Intervals.INTERVAL_15_MINUTES;
	}
	
	@Override
	public void onBar(OrderBook arg0, Bar arg1) {
		try (FileWriter writer = new FileWriter("D:\\BAR.txt", true)) {
			LocalDate gDate = LocalDate.now();
			writer.write("Date: "+ LocalTime.now()+"-"+LocalTime.now().getHour()+":"+LocalTime.now().getMinute() +":"+LocalTime.now().getSecond() +":"+ "  |Open: " +arg1.getOpen() + "   |high: " + arg1.getHigh() + "  |Close: " +arg1.getClose() + "  |Low: " +arg1.getLow() + "  |Volume: " +  arg1.getVolumeTotal()+ "  |BuyVolume: " +arg1.getVolumeBuy() + "  |SellVolume: " +arg1.getVolumeSell() +  "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

}