package com.assignment.webcrawler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebpageDto {

    private String url;
    private String title;
}
