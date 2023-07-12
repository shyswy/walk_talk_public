package com.example.clubsite.utility;

import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class PathUtil {
    public static final String ARTICLE_IMAGE = "article_image/";
    public static final String PROFILE_IMAGE = "profile_image/";
    public static final String RAW = "raw/";
    public static final String THUMBNAIL = "thumbnail/";
    public static final String PROFILE = "profile/";
    public static final String CONTENT_TYPE = "Content-type";

    @Value("${default.image.name}")
    private String DEFAULT_IMAGE;

    @Value("${file.dir}")
    private String fileDir;

    public String getArticleImagePath() {
        return fileDir + ARTICLE_IMAGE;
    }

    public String getProfileImagePath(String fileName) {
        if (fileName == null || fileName.equals(DEFAULT_IMAGE)) {
            return DEFAULT_IMAGE;
        } else {
            return fileDir + PROFILE_IMAGE + fileName;
        }
    }

    public String getRawPath(String fileName) {
        if (fileName == null || fileName.equals(DEFAULT_IMAGE)) {
            return fileDir + ARTICLE_IMAGE + RAW;
        } else {
            return fileDir + ARTICLE_IMAGE + RAW + fileName;
        }
    }

    public String getThumbnailPath(String fileName) {
        if (fileName == null || fileName.equals(DEFAULT_IMAGE)) {
            return fileDir + ARTICLE_IMAGE + THUMBNAIL;
        } else {
            return fileDir + ARTICLE_IMAGE + THUMBNAIL + fileName;
        }
    }

    public HttpHeaders getHttpHeaders(String path) {
        HttpHeaders header = new HttpHeaders();
        try {
            Path filePath = Paths.get(path);
            log.info("shyswy filePath={}", filePath);
            header.add(CONTENT_TYPE, Files.probeContentType(filePath));
        } catch (IOException e) {
            log.info("ioerro!!!!!!!");
            log.error("message={}", e);
            throw new RestApiException(CommonErrorResponseCode.IO_ERROR, e.getMessage());
        }
        return header;
    }

    public Resource getResource(String path) {
        if (path == DEFAULT_IMAGE || path == null)
            return null;
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            log.info("resource.exist not!!!");
            throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        }
        return resource;
    }
}
