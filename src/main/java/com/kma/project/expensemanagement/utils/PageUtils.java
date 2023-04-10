package com.kma.project.expensemanagement.utils;

import com.kma.project.expensemanagement.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {

    public static Pageable customPageable(Integer page, Integer size, String sort) {
        page = page == null ? 1 : page;
        size = size == null ? 20 : size;
        Sort sortObject;
        if (sort == null) {
            sortObject = Sort.by(Sort.Direction.DESC, "createdAt");
        } else {
            String[] sortStrings = sort.split("\\s");
            if (sortStrings[1].equals("desc")) {
                sortObject = Sort.by(Sort.Direction.DESC, sortStrings[0]);
            } else {
                sortObject = Sort.by(Sort.Direction.ASC, sortStrings[0]);
            }
        }
        return PageRequest.of(page - 1, size, sortObject);
    }

    public static String buildSearch(String source) {
        if (source == null) {
            source = "";
            return "%" + source + "%";
        } else {
            while (source.contains("  ")) {
                source = source.replaceAll(" {2}", " ");
            }
            return "%" + source.trim().replaceAll(" ", "%") + "%";
        }
    }

    public static <T> PageResponse<T> formatPageResponse(Page<T> page) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setContent(page.getContent());
        pageResponse.setPageNumber(page.getNumber() + 1);
        pageResponse.setPageSize(page.getSize());
        pageResponse.setTotalRecord(page.getTotalElements());
        pageResponse.setTotalPage(page.getTotalPages());
        return pageResponse;
    }
}
