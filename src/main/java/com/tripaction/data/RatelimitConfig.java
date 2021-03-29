package com.tripaction.data;


import lombok.*;

/**
 * Holds all the configuration  required for the rate limitter.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatelimitConfig {
    Long apiKey;
    String apiPath;
    Long maxRequest;
    Long period;
    Long banTime;
}
