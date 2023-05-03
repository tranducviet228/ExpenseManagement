package com.kma.project.expensemanagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Builder
@Getter
@Setter
public class ResourceDto {

    private Resource resource;

    private MediaType mediaType;
}
