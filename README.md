# rate-limiter

## Project Requirement

write a rate limiting  microservice that throttles a given API with the given 
configuration 


Eg: Given  inputs as apiKey (string), /api , 10, 300 secs, 86400secs - That is: if we see over 10 requests to /api in 5 minutes, we want that api key to be banned completely for 24 hours, for all requests.


## Final Deliverables (API's )

Following are the three API's exposed

* <B>ratelimit/enforce</b> :   This API helps in configuring throtlling with configuration parameters
  apiKey, apiPath, maxRequest, period, banTime

* <B>ratelimit/apiGateway</b> : This API acts as a gateway for the API calls  and executes the calls after checking throttling configuration.

* <B>ratelimit/state</b> : This API can be used to check the current throttling of all the API's

<a href="https://github.com/helloravisha/rate-limiter/blob/main/src/main/java/com/tripaction/controller/RateLimitController.java" target="_blank">API's</a>


##  Testing
An improper  tested product will definitely impact the product deliverable. Therefore   testing is an integral part of any agile development principles. Today we have different framework like junit, mockito
any many other frameworks, in the current system to best demonstrate API Testing end to end 
, i had leveraged rest assured to validate all the API's , the only thing we need to do to validate the entire code 
is to run the following test , which validates all the requires use cases. 

<a href="https://github.com/helloravisha/rate-limiter/blob/main/src/test/java/com/tripaction/api/automation/RateLimiterAPIAutomationTest.java" target="_blank">RateLimiterAPIAutomationTest</a>





##  Possible Enhancements
Application can be extended with more capabilities by adding some of the following features.

* In the best intrest of time all the components  required for the current system are sitting in one place , in a distrubuted environments all these componets 
will be placed different  as per the system design , we can pretty much
leverage load balancers, Kuberntes service mesh side car for having throttling 
component.
* in memory maps used for state management  can be replaced/ leveraged  with different technologies like 
redis cache , Hazle cast  , zoom keeper etc.
* Different rate limiting algorithms can be leveraged for the best rate limiting, taking 
different use cases in a distributed  environment. 


   


  

  



