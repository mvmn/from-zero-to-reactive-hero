package com.example.part_1.part1_extra_store_optional;

import java.util.Objects;

import rx.Observable;

public class Order {
	private final String id;
	private final String userId;
	private final Iterable<String> productsIds;

	public Order(String id, String userId, Iterable<String> productsIds) {
		this.id = Objects.requireNonNull(id);
		this.userId = Objects.requireNonNull(userId);
		this.productsIds = Objects.requireNonNull(productsIds);
	}

	public Observable<Long> getTotalPrice() {
		// TODO: use reduce to find the sum;
		// TODO: Use ProductCatalog#findById to find corresponded Product by its id
		// HINT: 3 lines of code
		// HINT: Observable.from + Observable#map + Observable#reduce

		return Observable.from(productsIds).map(ProductsCatalog::findById).map(Product::getPrice)
				.reduce((p1, p2) -> p1 + p2);
	}

	public String getId() {
		return this.id;
	}

	public String getUserId() {
		return this.userId;
	}

	public Iterable<String> getProductsIds() {
		return this.productsIds;
	}
}
