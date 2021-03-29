package com.tripaction.storage;

import com.tripaction.controller.RateLimitController;
import com.tripaction.data.RateLimitState;
import com.tripaction.data.RatelimitConfig;
import com.tripaction.request.ApiThrotllingKey;
import com.tripaction.util.RateLimitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This is in-memory rate limiter.
 *
 */
@Component("inmemory")
public class InMemoryRateLimiter implements RateLimiter {

    private static final Logger logger = LogManager.getLogger(RateLimitController.class);
    private ConcurrentHashMap<String, RatelimitConfig> rateLimiterRegisty = new ConcurrentHashMap<String, RatelimitConfig>();
    private ConcurrentHashMap<String, RateLimitState> rateLimiterState = new ConcurrentHashMap<String, RateLimitState>();

    public InMemoryRateLimiter() {
    }

    @Override
    public void configure(RatelimitConfig ratelimitConfig) {
        String key = ratelimitConfig.getApiKey() + ratelimitConfig.getApiPath();
        RatelimitConfig currentRatelimitConfig = rateLimiterRegisty.get(key);
        if (currentRatelimitConfig != null) {
            logger.warn(" rate limit config updated for apiKey={} apiPath={}", ratelimitConfig.getApiKey(), ratelimitConfig.getApiPath());
        }
        rateLimiterRegisty.put(key, ratelimitConfig);
    }

    @Override
    public RatelimitConfig getConfig(String key) {
        return rateLimiterRegisty.get(key);
    }

    @Override
    public RateLimitState getApiCurrentRateLimitState(ApiThrotllingKey apiThrotllingKey) {
        String key = apiThrotllingKey.getApiKey() + apiThrotllingKey.getApiPath();
        RateLimitState rateLimitState = rateLimiterState.get(key);
        if (rateLimitState == null) {
            RateLimitState rateLimitStat = RateLimitState.builder().api(apiThrotllingKey.getApiKey()).api(apiThrotllingKey.getApiPath()).
                    throttled(false).apiCount(0l).build();
            rateLimiterState.put(key, rateLimitStat);
            return rateLimitStat;
        }
        return rateLimitState;
    }

    @Override
    public List<RateLimitState> getAllAPICurrentThrottlingState() {
        if(rateLimiterState != null && rateLimiterState.size() > 0 )
        {
            return rateLimiterState.values().stream().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean IsApiCallAllowed(RateLimitState rateLimitState, RatelimitConfig ratelimitConfig) {
        return !RateLimitUtil.isThrottled(rateLimitState, ratelimitConfig);
    }
}
