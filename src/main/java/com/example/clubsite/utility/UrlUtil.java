package com.example.clubsite.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlUtil {
    private static final String IMAGES = "images/";
    private final Environment environment;
    @Value("${static.ip_port}")
    String ipPort;
    @Value("${static.url.front}")
    String frontUrl;

    public String getBaseUrl() {
        return frontUrl + "://" + ipPort + "/";
    }

    public String getProfileUrlWithUrl(String imageUrl) {
        if (imageUrl == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(getBaseUrl());
        sb.append(IMAGES);
        sb.append(PathUtil.PROFILE);
        sb.append(imageUrl);
        return sb.toString();
    }

    public String getRawUrl() {
        return getBaseUrl() + IMAGES + PathUtil.RAW;
    }

    public String getThumbnailUrl() {
        return getBaseUrl() + IMAGES + PathUtil.THUMBNAIL;
    }

    public String getProfileUrl() {
        return getBaseUrl() + IMAGES + PathUtil.PROFILE;
    }
}
