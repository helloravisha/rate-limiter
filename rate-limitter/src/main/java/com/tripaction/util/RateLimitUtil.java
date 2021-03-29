package com.tripaction.util;

import com.tripaction.controller.RateLimitController;
import com.tripaction.data.RateLimitState;
import com.tripaction.data.RatelimitConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * Utility which calculates throtlling.
 * @author ravi katta
 */
public class RateLimitUtil {
    private static final Logger logger = LogManager.getLogger(RateLimitController.class);

    public static Boolean isThrottled(RateLimitState rateLimitState, RatelimitConfig ratelimitConfig) {
        try {
            Long currentTime = System.currentTimeMillis();
            Long period = ratelimitConfig.getPeriod()*1000;
            Long banTime = ratelimitConfig.getBanTime()*1000;
            Long maxRequestCount = ratelimitConfig.getMaxRequest();
            Long currentApiCount = rateLimitState.getApiCount();

            // is throttled
            if (rateLimitState.getThrottled()) {
                if (currentTime < rateLimitState.getBanTime()) {
                    logger.error(" api blocked , api={} callsReceived={} maxAllowed={} bannedForSeconds={}  ",
                            ratelimitConfig.getApiPath(), rateLimitState.getApiCount(), ratelimitConfig.getMaxRequest(),
                            ratelimitConfig.getBanTime() / 1000);
                    return true;
                } else {
                    resetRateLimitState(rateLimitState);
                    currentApiCount = 0l;
                }
            }
            if (currentApiCount == 0) {
                rateLimitState.setStartTime(currentTime);
                rateLimitState.setEndTime(currentTime + period);
            }
            rateLimitState.setApiCount(rateLimitState.getApiCount() + 1);

            // if we see over x requests to API in y minutes , ban it
            if (rateLimitState.getApiCount() > maxRequestCount && currentTime <= rateLimitState.getEndTime()) {
                rateLimitState.setThrottled(true);
                rateLimitState.setBanTime(currentTime + banTime);
                rateLimitState.setApiCount(0l);
                logger.error(" api throttled , api={} callsReceived={} maxAllowed={} bannedForSeconds={}  ",
                        ratelimitConfig.getApiPath(), rateLimitState.getApiCount(), ratelimitConfig.getMaxRequest(),
                        ratelimitConfig.getBanTime() / 1000);
                return true;
            }
            // window crossed reset the state
            if (currentTime > rateLimitState.getEndTime()) {
                resetRateLimitState(rateLimitState);
            }

        } catch (Exception exception) {
            logger.error(" error computing api rate limit , api={} callsReceived={} maxAllowed={} ex={} ",
                    ratelimitConfig.getApiPath(), rateLimitState.getApiCount(), ratelimitConfig.getMaxRequest(), exception
            );
            return false;
        }
        logger.error(" api call allowed  , api={} callsReceived={} maxAllowed={}  ",
                ratelimitConfig.getApiPath(), rateLimitState.getApiCount(), ratelimitConfig.getMaxRequest()
        );
        return false;
    }


    private static void resetRateLimitState(RateLimitState rateLimitState) {
        rateLimitState.setThrottled(false);
        rateLimitState.setApiCount(0l);
        rateLimitState.setStartTime(null);
        rateLimitState.setEndTime(null);
        rateLimitState.setBanTime(0l);
    }
}
