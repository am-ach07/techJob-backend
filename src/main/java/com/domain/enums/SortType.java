package com.domain.enums;

import org.springframework.data.domain.Sort;

public enum SortType {
    AGE_ASC("age", Sort.Direction.ASC),
    AGE_DESC("age", Sort.Direction.DESC),
    NAME_ASC("fullName", Sort.Direction.ASC),
	NAME_DESC("fullName",Sort.Direction.DESC),
	DATE_ASC("createdAt",Sort.Direction.ASC),
	DATE_DESC("createdAt",Sort.Direction.DESC),
	PRICE_ASC("price",Sort.Direction.ASC),
	PRICE_DESC("price",Sort.Direction.DESC);


    private final String property;
    private final Sort.Direction direction;

    SortType(String property, Sort.Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    public Sort toSpringSort() {
        return Sort.by(direction, property);
    }
}
