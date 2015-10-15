package com.devclinton.WindowMetrics;

import java.io.File;
import java.io.IOException;

/**
 * Created by Clinton Collins <clinton.collins@gmail.com> on 10/15/15.
 */
public class TestUtility {
    public static String safeTempFile(String prefix) {
        try {
            return File.createTempFile(prefix, null).getAbsolutePath();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
