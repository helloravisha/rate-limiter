package com.tripaction.service;

import com.tripaction.request.DownstreamRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This is a downstream service which is can be extended
 * as per the requirement. we will land in this down stream
 * service API's only once throttling check is done at
 * the API Gateway level or as per the design of the distrubuted
 * system.
 *
 */
@Component("rest")
public class DownStreamRestService  implements  DownStreamService{

    private static final Logger logger = LogManager.getLogger(DownStreamRestService.class);

    @Override
    public String invokeAPI(DownstreamRequest DownstreamRequest)
    {
        logger.info(" downstream rest service invoked.");
        return "success";
    }
}
