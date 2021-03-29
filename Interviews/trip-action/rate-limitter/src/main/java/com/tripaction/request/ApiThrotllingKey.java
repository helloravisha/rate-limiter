package com.tripaction.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiThrotllingKey {
    String apiKey;
    String apiPath;
}
