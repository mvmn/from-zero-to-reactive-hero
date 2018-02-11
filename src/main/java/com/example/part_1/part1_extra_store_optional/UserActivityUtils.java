package com.example.part_1.part1_extra_store_optional;

import rx.Observable;

public class UserActivityUtils {

	public static Observable<Product> findMostExpansivePurchase(Observable<Order> ordersHistory) {
		// TODO: flatten all Products inside Orders and using reduce find one with the
		// highest price

		return ordersHistory.map(order -> Observable.from(order.getProductsIds())).flatMap(o -> o)
				.map(ProductsCatalog::findById).reduce((p1, p2) -> p1.getPrice() > p2.getPrice() ? p1 : p2);
	}
}
