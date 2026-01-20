package com.totemsoft.page.config;

import javax.naming.directory.SearchResult;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.totemsoft.page.model.Cell;
import com.totemsoft.page.model.ColumnDef;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.PageDto;
import com.totemsoft.page.model.PageResponse;
import com.totemsoft.page.model.Row;
import com.totemsoft.page.model.SectionDto;
import com.totemsoft.page.model.SeriesDataDto;
import com.totemsoft.page.model.SubSectionDto;
import com.totemsoft.page.model.SubSectionResult;
import com.totemsoft.page.model.TabDto;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;

@Configuration
@EnableScheduling
@RegisterReflectionForBinding({
    Cell.class,
    ColumnDef.class,
    KeyDto.class,
    PageDto.class,
    PageResponse.class,
    Row.class,
    SearchResult.class,
    SectionDto.class,
    SeriesDataDto.class,
    SubSectionDto.class,
    SubSectionResult.class,
    TabDto.class,
    TagDto.class,
    TagTypeDto.class
})
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

}
