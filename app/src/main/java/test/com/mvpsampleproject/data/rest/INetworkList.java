package com.tcs.pickupapp.data.rest;

import com.tcs.pickupapp.data.rest.response.ErrorResponse;

import java.util.List;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public interface INetworkList<T> {
    void onSuccessList(List<T> response);
    void onErrorList(ErrorResponse errorResponse);
    void onFailureList(Throwable t);
}
