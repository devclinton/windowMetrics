package com.devclinton.WindowMetrics.tasks.WindowInfo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Created by Clinton Collins <clinton.collins@cgi.com> on 10/16/15.
 */
public class CachingInfoInterface {
    protected Cache<String, ProcessInfo> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

}
