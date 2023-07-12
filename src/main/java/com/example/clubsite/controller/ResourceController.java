package com.example.clubsite.controller;


import com.example.clubsite.utility.PathUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "리소스 컨트롤러")
public class ResourceController {
    private final PathUtil pathUtil;

    @ApiOperation(value="raw 이미지-파일명 조회", notes="파일명으로 raw 이미지를 가져옵니다.")
    @GetMapping("images/raw/{fileName}")
    public ResponseEntity<Resource> getRawImage(@PathVariable String fileName) {
        return new ResponseEntity<>(pathUtil.getResource(pathUtil.getRawPath(fileName)), pathUtil.getHttpHeaders(pathUtil.getRawPath(fileName)), HttpStatus.OK);
    }

    @ApiOperation(value="썸네일 이미지-파일명 조회", notes="파일명으로 썸네일 이미지를 가져옵니다.")
    @GetMapping("images/thumbnail/{fileName}")
    public ResponseEntity<Resource> getThumbnailImage(@PathVariable String fileName) {
        return new ResponseEntity<>(pathUtil.getResource(pathUtil.getThumbnailPath(fileName)), pathUtil.getHttpHeaders(pathUtil.getThumbnailPath(fileName)), HttpStatus.OK);
    }

    @ApiOperation(value="프로필 이미지-프로필 조회", notes="파일명으로 프로필 이미지를 가져옵니다.")
    @GetMapping("images/profile/{fileName}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String fileName) {
        return new ResponseEntity<>(pathUtil.getResource(pathUtil.getProfileImagePath(fileName)), pathUtil.getHttpHeaders(pathUtil.getProfileImagePath(fileName)), HttpStatus.OK);
    }
}
