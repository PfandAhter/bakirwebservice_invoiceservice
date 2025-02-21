package com.bakirwebservice.invoiceservice.service;

import java.util.List;

public interface IMapperService {

    <T,D> List<D> map(List<T> source, Class<D> destination);

    <D> D map(Object source, Class<D> destinationType);

    void map(Object source, Object destination);
}

