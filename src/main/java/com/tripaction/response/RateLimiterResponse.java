package com.tripaction.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateLimiterResponse {
    private Boolean success;
}
