package com.example.clubsite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
    private int pageSize;
    private int page;
    private String type;
    private String keyword;

    public PageRequestDTO() {
        this.page = 1;
        this.pageSize = 10;
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, pageSize, sort);
    }
}
