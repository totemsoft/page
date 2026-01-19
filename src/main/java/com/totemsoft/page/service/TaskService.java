package com.totemsoft.page.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.totemsoft.page.model.entity.SeriesData;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.SeriesDataRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskService {

    private final KeyRepository keyRepository;

    private final SeriesDataRepository seriesDataRepository;

    @Scheduled(cron = "@daily") // @midnight
    @Scheduled(initialDelay = 10_000) // one-time
    @Transactional
    public void dailyTask() {
        log.info(">>> Daily task started at: {}", LocalTime.now());
        try {
            final var pageable = PageRequest.of(0, 100);
            final var keys = keyRepository.findAll(pageable).getContent();
            final var date = LocalDate.now();
            keys.forEach(key -> loadData(key.getId(), date));
            log.info("<<< Daily task executed at: {}", LocalTime.now());
        } catch (Throwable ignore) {
            log.warn("<<< Daily task failed:", ignore);
        }
    }

    private void loadData(long keyId, LocalDate date) {
        seriesDataRepository.save(SeriesData.builder()
            .keyId(keyId)
            .date(date)
            .value(randomValue(1_000, 1_000_000))
            .title(randomTitle(16, 32))
            .build());
    }

    /**
     * Generate the random number: min + (max - min) * randomDouble[0,1]
     */
    private static BigDecimal randomValue(long min, long max) {
        return BigDecimal.valueOf(min)
            .add(BigDecimal.valueOf(max - min)
            .multiply(BigDecimal.valueOf(Math.random())))
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generate the random string whose length is between the inclusive minimum and the exclusive maximum
     */
    private static String randomTitle(int min, int max) {
        return RandomStringUtils.insecure().nextAlphabetic(min, max);
    }

}
