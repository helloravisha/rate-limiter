package com.tripaction.storage;

import com.tripaction.data.RateLimitState;
import com.tripaction.data.RatelimitConfig;
import com.tripaction.request.ApiThrotllingKey;

import java.util.List;

public interface RateLimiter {
    void configure(RatelimitConfig enforceRequest) throws Exception;
    RatelimitConfig getConfig(String  key) throws Exception;
    RateLimitState getApiCurrentRateLimitState(ApiThrotllingKey apiThrotllingKey) throws Exception;
    List<RateLimitState> getAllAPICurrentThrottlingState();
    Boolean IsApiCallAllowed(RateLimitState rateLimitState,RatelimitConfig ratelimitConfig) throws Exception;
}
