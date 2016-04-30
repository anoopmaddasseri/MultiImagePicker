package com.picker.utils;

/**
 * This class keeps all the globally used constant values only for static access
 */
public final class Constants {

    public static final String PREFERENCE_FILE_NAME = "com.picker";
    // Intent data
    public static final String DATA = "data";
    public static final int RESULT_RETRY = 2;
    public static final int REQUEST_CODE = 100;

    public Constants() {
        throw new RuntimeException("Final Class, cannot be instantiated !");
    }

}
