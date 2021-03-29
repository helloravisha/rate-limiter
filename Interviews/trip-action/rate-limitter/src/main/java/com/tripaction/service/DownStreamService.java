package com.tripaction.service;

import com.tripaction.request.DownstreamRequest;

public interface DownStreamService {
    String invokeAPI(DownstreamRequest DownstreamRequest);
}
