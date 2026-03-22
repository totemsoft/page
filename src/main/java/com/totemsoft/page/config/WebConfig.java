package com.totemsoft.page.config;

import java.util.function.Predicate;

import javax.naming.directory.SearchResult;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.LoadDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.PageResponse;
import com.totemsoft.page.model.Pagination;
import com.totemsoft.page.model.Row;
import com.totemsoft.page.model.SearchData;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.service.ApiException;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableScheduling
@RegisterReflectionForBinding({
    Cell.class,
    ColumnDef.class,
    ErrorResponse.class,
    KeyDto.class,
    LoadDto.class,
    PageDto.class,
    PageResponse.class,
    Pagination.class,
    Row.class,
    SearchData.class,
    SearchResult.class,
    SectionDto.class,
    SeriesDataDto.class,
    SubSectionDto.class,
    TabDto.class,
    TagDto.class,
    TagTypeDto.class
})
@ImportRuntimeHints(CustomRuntimeHintsRegistrar.class)
@Log4j2
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/page").setViewName("page");
        registry.addViewController("/login").setViewName("login");
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//            .addResourceHandler("/static/**")
//            .addResourceLocations("/")
//            .setCacheControl(CacheControl.noCache().cachePrivate().mustRevalidate())
//            .setCachePeriod(86400);
//    }

    @Bean
    RestClient exchangeRatesApiRestClient(
            @Value("${page.exchangeratesapi.io.base-url}") String baseUrl,
            RestClientInterceptor restClientInterceptor) {
        return RestClient.builder()
            .baseUrl(baseUrl)
            //.apiVersionInserter(ApiVersionInserter.usePathSegment(0))
            //.defaultApiVersion("v1")
            .defaultStatusHandler(
                Predicate.not(HttpStatusCode::is2xxSuccessful),
                (request, response) -> {
                    // @see com.totemsoft.page.exchangerates.v1.model.ApiError class
                    final var error = new String(response.getBody().readAllBytes());
                    log.error("API request failed. Response status: {}, body: {}", 
                        response.getStatusCode(), error);
                    throw new ApiException(error);
                }
            )
            .bufferContent((uri, method) -> true)
            .requestInterceptor(restClientInterceptor)
            .build();
    }

    @Bean
    RestClient marketStackApiRestClient(
            @Value("${page.marketstack.com.base-url}") String baseUrl,
            RestClientInterceptor restClientInterceptor) {
        return RestClient.builder()
            .baseUrl(baseUrl)
            //.apiVersionInserter(ApiVersionInserter.usePathSegment(0))
            //.defaultApiVersion("v2")
            .defaultStatusHandler(
                Predicate.not(HttpStatusCode::is2xxSuccessful),
                (request, response) -> {
                    // @see com.totemsoft.page.marketstack.v2.model.ApiError class
                    final var error = new String(response.getBody().readAllBytes());
                    log.error("API request failed. Response status: {}, body: {}", 
                        response.getStatusCode(), error);
                    throw new ApiException(error);
                }
            )
            .bufferContent((uri, method) -> true)
            .requestInterceptor(restClientInterceptor)
            .build();
    }

}
