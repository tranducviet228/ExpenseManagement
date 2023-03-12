package com.kma.project.expensemanagement.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
public class DataResponse<T> {

    private T data;

    public DataResponse(T data) {
        this.data = data;
    }

    public DataResponse() {

    }
}
