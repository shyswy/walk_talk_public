package com.example.clubsite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int totalPages;
    private int currentPage;
    private long totalRows;
    private int pageSize;
}
