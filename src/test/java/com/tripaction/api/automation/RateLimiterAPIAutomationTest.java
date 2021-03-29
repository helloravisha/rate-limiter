package com.tripaction.api.automation;

import com.tripaction.RateLimiterApplication;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * This API automation test used for validating all the use cases
 * <p>
 * what am i validating here ?
 * <p>
 * if we see over 1 requests in 5 seconds , we ban the api for 10 seconds and trigger
 * the api call again to see if it goes.
 *
 * All steps updated with the comments.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {RateLimiterApplication.class})
public class RateLimiterAPIAutomationTest {

    private static final String ENFORCE_RATE_LIMITER_API = "ratelimit/enforce";
    private static final String API_GATEWAY = "ratelimit/apiGateway";
    private static final String ALL_API_LIMITS = "ratelimit/state";
    private static final String HOST = "http://localhost:";
    private static final String ROOT_CONTEXT = "/tripaction/";

    @LocalServerPort
    protected int port;
    protected RequestSpecification httpRequest;

    @Before
    public void setup() {
        RestAssured.baseURI = HOST + port + ROOT_CONTEXT;
        httpRequest = RestAssured.given();
    }

    /**
     * This is the core Test , which validates the entire functionality of the API.
     * updated with comments with each case being validated.
     *
     * @throws Exception
     */
    @Test
    public void validateRateLimiter() throws Exception {
        RequestSpecification requestSpec = getRequestSpecForRateLimiter();
        ResponseSpecification responseSpec = getResponseSpecForRateLimiter();

        // configure rate limit for the API - /api/employee  with apiKey - 1
        httpRequest.spec(requestSpec).request(Method.POST, ENFORCE_RATE_LIMITER_API).then().spec(responseSpec);

        // invoke the gateway which checks throttling and invokes the downstream API
        RequestSpecification apiSpec = getRequestSpecForApiGateway();
        httpRequest.spec(apiSpec).request(Method.GET, API_GATEWAY).then().spec(responseSpec);

        // Second call triggered in less than 5 seconds , this is throttled and not allowed and throws - 429
        ResponseSpecification tooManyRequestsResponseSpec = getTooManyRequestsResponseSpec();
        httpRequest.spec(apiSpec).request(Method.GET, API_GATEWAY).then().spec(tooManyRequestsResponseSpec);

        // wait until ban time is completed
        Thread.sleep(10000);

        // third call triggered after ban time and it is sucessful.
        RequestSpecification afterBanSpec = getRequestSpecForApiGateway();
        httpRequest.spec(afterBanSpec).request(Method.GET, API_GATEWAY).then().spec(responseSpec);

        // check  throttling state  of all  API's
        httpRequest.request(Method.GET, ALL_API_LIMITS).body().prettyPrint();

    }

    /**
     * Configure rate limiter for the API - /api/employee and APIKey - 1
     * with the following configuration.
     * <p>
     * This Spec acts as payload for  throttling configuration.
     *
     * @return
     */
    private RequestSpecification getRequestSpecForRateLimiter() {
        Map<String, Object> rateLimiterPayload = new HashMap<>();
        rateLimiterPayload.put("apiKey", 1);
        rateLimiterPayload.put("apiPath", "/api/employee");
        rateLimiterPayload.put("maxRequest", 1);
        rateLimiterPayload.put("period", 5);
        rateLimiterPayload.put("banTime", 10);
        return new RequestSpecBuilder().setContentType(ContentType.JSON).setAccept(ContentType.JSON).setBody(rateLimiterPayload).build();
    }

    /**
     * This Spec acts as payload for API call .
     *
     * @return
     */
    private RequestSpecification getRequestSpecForApiGateway() {
        Map<String, Object> requestSpecForApiGateway = new HashMap<>();
        requestSpecForApiGateway.put("apiKey", 1);
        requestSpecForApiGateway.put("apiPath", "/api/employee");
        return new RequestSpecBuilder().setContentType(ContentType.JSON).setAccept(ContentType.JSON).setBody(requestSpecForApiGateway).build();
    }

    /**
     * Success Response SPEC with HTTP Code - 200
     *
     * @return
     */
    private ResponseSpecification getResponseSpecForRateLimiter() {
        ResponseSpecification responseSpecification = new ResponseSpecBuilder().build().statusCode(200);
        return responseSpecification;
    }

    /**
     * Error Response SPEC with HTTP Code - 429 ( Too Many Requests )
     *
     * @return
     */
    private ResponseSpecification getTooManyRequestsResponseSpec() {
        ResponseSpecification responseSpecification = new ResponseSpecBuilder().build().statusCode(429);
        return responseSpecification;
    }


}
