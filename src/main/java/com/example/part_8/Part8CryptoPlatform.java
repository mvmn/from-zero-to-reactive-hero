package com.example.part_8;

import com.example.part_8.external.CryptoConnectionHolder;
import com.example.part_8.service.PriceService;
import com.example.part_8.service.TradeService;
import com.example.part_8.utils.JsonUtils;
import com.example.part_8.utils.LoggerConfigurationTrait;
import com.example.part_8.utils.NettyUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.http.websocket.WebsocketInbound;
import reactor.ipc.netty.http.websocket.WebsocketOutbound;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

import static com.example.part_8.utils.HttpResourceResolver.resourcePath;

public class Part8CryptoPlatform extends LoggerConfigurationTrait {
	private static final Logger logger = Logger.getLogger("http-server");

	public static void main(String[] args) {
		CryptoConnectionHolder externalSystemDataStream = new CryptoConnectionHolder();
		PriceService priceService = new PriceService();
		TradeService tradeService = new TradeService();

		HttpServer.create(8080)
				.startRouterAndAwait(hsr -> hsr
						.ws("/stream", handleWebsocket(externalSystemDataStream, priceService, tradeService))
						.file("/favicon.ico", resourcePath("ui/favicon.ico"))
						.file("/main.js", resourcePath("ui/main.js")).file("/**", resourcePath("ui/index.html")));
	}

	private static BiFunction<WebsocketInbound, WebsocketOutbound, Publisher<Void>> handleWebsocket(
			CryptoConnectionHolder externalSystemDataStream, PriceService priceService, TradeService tradeService) {
		return (req, res) -> externalSystemDataStream.listenForExternalEvents().transform(tradingDataStream -> {
			Flux<Long> priceAverageIntervalCommands = NettyUtils.prepareInput(req)
					.doOnNext(inMessage -> logger.info("[WS PA] >> " + inMessage))
					.transform(Part8CryptoPlatform::handleRequestedAveragePriceIntervalValue);

			return Flux.merge(priceService.calculatePriceAndAveragePrice(tradingDataStream, priceAverageIntervalCommands

			), tradeService.tradingEvents(tradingDataStream));
		}).map(JsonUtils::writeAsString).doOnNext(outMessage -> logger.info("[WS] << " + outMessage))
				.transform(Part8CryptoPlatform::handleOutgoingStreamBackpressure)
				.transform(NettyUtils.prepareOutbound(res));
	}

	// Visible for testing
	public static Flux<Long> handleRequestedAveragePriceIntervalValue(Flux<String> requestedInterval) {
		// XTODO: input may be incorrect, pass only correct interval
		// XTODO: ignore invalid values (empty, non number, <= 0, > 60)
		return requestedInterval.map(new Function<String, Long>() {
			@Override
			public Long apply(String t) {
				Long result = -1L;
				try {
					result = Long.parseLong(t);
				} catch (NumberFormatException e) {
					System.err.println("Non integer input provided for interval value: " + t);
				}
				return result;
			}
		}).filter(v -> v != null && v.longValue() > 0 && v.longValue() <= 60);
	}

	// Visible for testing
	public static Flux<String> handleOutgoingStreamBackpressure(Flux<String> outgoingStream) {
		// XTODO: Add backpressure handling
		// It is possible that writing data to output may be slower than rate of
		// incoming output data

		return outgoingStream.onBackpressureBuffer();
	}

}
