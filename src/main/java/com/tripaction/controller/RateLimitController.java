package com.tripaction.controller;

import com.tripaction.data.RateLimitState;
import com.tripaction.data.RatelimitConfig;
import com.tripaction.request.ApiThrotllingKey;
import com.tripaction.request.DownstreamRequest;
import com.tripaction.response.RateLimiterResponse;
import com.tripaction.service.DownStreamService;
import com.tripaction.storage.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Rate limit controller , deals with all the required API used for configuring
 * throtlling.
 *
 * @author ravi Katta.
 */
@RestController
@RequestMapping("/ratelimit")
public class RateLimitController {

    private static final Logger logger = LogManager.getLogger(RateLimitController.class);

    // This can be configurable and can be  changed as per the storage implementation
    @Autowired
    @Qualifier("inmemory")
    private RateLimiter rateLimiter;

    // This is downstream service , which is configurable and can connect to any downstream service.
    @Autowired
    @Qualifier("rest")
    private DownStreamService downStreamService;


    /**
     * Takes care of confguring the rate limit with the given configuraiton
     * @param ratelimitConfig
     * @return
     */
    @RequestMapping(value = "/enforce", method = RequestMethod.POST)
    public RateLimiterResponse enforceRateLimit(@RequestBody RatelimitConfig ratelimitConfig) {
        RateLimiterResponse rateLimiterResponse = null;
        try {
            if (ratelimitConfig == null) {
                logger.error(" rate limit configuration not present");
                throw new Exception(" rate limit not configured for the API. ");
            }
            if (ratelimitConfig.getApiKey() == null || ratelimitConfig.getApiPath() == null || ratelimitConfig.getBanTime() == null
                    || ratelimitConfig.getMaxRequest() == null) {
                logger.error(" rate limit configuration missing.");
                throw new Exception(" rate limit configuration missing.");
            }
            logger.info(" enforce rate limiter for the API={}", ratelimitConfig.getApiPath());
            rateLimiter.configure(ratelimitConfig);
        } catch (Exception exception) {
            logger.error(" exception enforcing rate limiter for the API={}", ratelimitConfig.getApiPath());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), exception);

        }
        return rateLimiterResponse;
    }

    /**
     * This method acts as a API Gateway to the underlying API calls,  in a distributed environment
     * this complete component can sit either in load balancer , API Gateway or
     * if its kubernetes cluster we can leverage service mesh and have it as a sidecar
     * component with the
     * <p>
     * in the best interest of the time , i am using this as an API here.
     *
     * @param apiThrotllingKey
     * @return
     */
    @RequestMapping(value = "/apiGateway", method = RequestMethod.GET)
    public String apiGateway(@RequestBody ApiThrotllingKey apiThrotllingKey) {
        if (apiThrotllingKey == null || apiThrotllingKey.getApiKey() == null || apiThrotllingKey.getApiPath() == null) {
            logger.info(" invalid input , please check for valid input. ");
        }
        String throtllingKey = apiThrotllingKey.getApiKey() + apiThrotllingKey.getApiPath();
        String status;
        try {
            RateLimitState rateLimitState = rateLimiter.getApiCurrentRateLimitState(apiThrotllingKey);
            RatelimitConfig ratelimitConfig = rateLimiter.getConfig(throtllingKey);
            if (ratelimitConfig == null) {
                logger.error(" rate limit not configured for the given apiKey={} apiPath={}", apiThrotllingKey.getApiKey(), apiThrotllingKey.getApiPath());
                throw new Exception(" rate limit not configured for the API. ");
            }

            if (rateLimiter.IsApiCallAllowed(rateLimitState, ratelimitConfig)) {
                status = downStreamService.invokeAPI(DownstreamRequest.builder().payload("101").build());
            } else {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS, " api throttled ", new Exception("api throttled"));
            }
        } catch (ResponseStatusException responseStatusException) {
            logger.error(" too many request  , apiKey={} apiPath={} exception={}", apiThrotllingKey.getApiKey(), apiThrotllingKey.getApiPath(), responseStatusException);
            throw responseStatusException;
        } catch (Exception exception) {
            logger.error(" error triggering api call , apiKey={} apiPath={} exception={}", apiThrotllingKey.getApiKey(), apiThrotllingKey.getApiPath(), exception);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
        }
        return status;
    }


    /**
     * This API is to check whats the RateLimit state of all the API's
     * @return
     */
    @RequestMapping(value = "/state", method = RequestMethod.GET)
    public List<RateLimitState> getCurrentRateLimits() {
        List<RateLimitState> rateLimitState = new ArrayList<>();
        try {
            logger.info(" get all current api limits");
            rateLimitState = rateLimiter.getAllAPICurrentThrottlingState();
        } catch (Exception exception) {
            logger.error(" error getting current API limits. ");
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), exception);
        }
        return rateLimitState;
    }


}


