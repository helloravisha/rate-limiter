package com.tripaction.request;


import lombok.*;

/**
 * A generic payload created for downstream request , this can be
 * extended as per the requirement.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownstreamRequest {
    Object payload;
}
