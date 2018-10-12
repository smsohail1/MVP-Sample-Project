package com.tcs.pickupapp.data.rest;

import com.tcs.pickupapp.data.rest.response.ErrorResponse;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public interface INetwork<T> {
    void onSuccess(T response);
    void onError(ErrorResponse errorResponse);
    void onFailure(Throwable t);
}
