package com.example.clubsite.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StepRequest {
    @ApiModelProperty(value = "stepCount", example = "1")
    private Long stepCount;
}
