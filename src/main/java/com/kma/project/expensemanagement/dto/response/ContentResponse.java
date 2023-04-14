package com.kma.project.expensemanagement.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
public class ContentResponse<T> {

    private T content;

    public ContentResponse(T content) {
        this.content = content;
    }

    public ContentResponse() {

    }
}
