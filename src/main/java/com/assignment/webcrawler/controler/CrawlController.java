package com.assignment.webcrawler.controler;

import com.assignment.webcrawler.entity.Webpage;
import com.assignment.webcrawler.service.WebpageService;
import com.assignment.webcrawler.dto.WebpageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("crawl")
public class CrawlController {

    private WebpageService webpageService;

    @Autowired
    public CrawlController(WebpageService webpageService) {
        this.webpageService = webpageService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WebpageDto>> getPlayers(
            @RequestParam String url,
            @RequestParam(name = "number") Integer limit) {
        List<Webpage> webpagesInfo = webpageService.crawlUrls(url, limit);
        List<WebpageDto> ret = webpagesInfo.stream()
                .map(entity -> WebpageDto.builder()
                        .url(entity.getUrl())
                        .title(entity.getTitle())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ret);
    }

}
