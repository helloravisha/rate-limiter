package com.tripaction.storage;

import com.tripaction.data.RateLimitState;
import com.tripaction.data.RatelimitConfig;
import com.tripaction.request.ApiThrotllingKey;
import org.springframework.stereotype.Component;

import java.util.List;

//TO DO : We can use this  in future for persistent implementation.
@Component("persistent")
public class PersistentRateLimiter implements  RateLimiter{
    @Override
    public void configure(RatelimitConfig enforceRequest) {
    }
    @Override
    public RatelimitConfig getConfig(String key) {
        return null;
    }

    @Override
    public RateLimitState getApiCurrentRateLimitState(ApiThrotllingKey apiThrotllingKey) {
        return null;
    }

    @Override
    public List<RateLimitState> getAllAPICurrentThrottlingState() {
        return null;
    }

    @Override
    public Boolean IsApiCallAllowed(RateLimitState rateLimitState, RatelimitConfig ratelimitConfig) {
        return null;
    }
}
