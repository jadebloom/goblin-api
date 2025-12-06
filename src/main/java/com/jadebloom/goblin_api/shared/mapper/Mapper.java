package com.jadebloom.goblin_api.shared.mapper;

public interface Mapper<T, S> {

    S mapTo(T t);

    T mapFrom(S s);

}
