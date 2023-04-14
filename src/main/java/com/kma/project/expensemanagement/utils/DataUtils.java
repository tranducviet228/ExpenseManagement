package com.kma.project.expensemanagement.utils;

import com.kma.project.expensemanagement.dto.response.ContentResponse;
import com.kma.project.expensemanagement.dto.response.DataResponse;

public class DataUtils {

    public static <T> DataResponse<T> formatData(T data) {
        DataResponse dataResponse = new DataResponse<>();
        dataResponse.setData(data);
        return dataResponse;
    }

    public static <T> ContentResponse<T> formatContent(T data) {
        ContentResponse contentResponse = new ContentResponse();
        contentResponse.setContent(data);
        return contentResponse;
    }
}
