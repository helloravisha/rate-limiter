package com.tripaction.data;

import lombok.*;

/**
 * Holds the rate limiting state of each API.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateLimitState {

    Long startTime;
    Long endTime;
    Long apiCount;
    String apiKey;
    String api;
    Boolean throttled;
    Long banTime;
}
