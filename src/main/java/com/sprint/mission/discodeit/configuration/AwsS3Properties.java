package com.sprint.mission.discodeit.configuration;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class AwsS3Properties {

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(new FileReader(".env.properties"));
        } catch (IOException e) {
            throw new RuntimeException(".env 파일을 불러오지 못했습니다.", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

}
