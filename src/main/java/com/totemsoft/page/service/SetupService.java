package com.totemsoft.page.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.totemsoft.page.config.SecurityConfig;
import com.totemsoft.page.exchangerates.v1.model.CurrencyDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerDto;
import com.totemsoft.page.model.ColumnDef.DropdownOption;
import com.totemsoft.page.model.KeyDto;
import com.totemsoft.page.model.Pagination;
import com.totemsoft.page.model.SearchData;
import com.totemsoft.page.model.TagDto;
import com.totemsoft.page.model.TagTypeDto;
import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.KeyTag;
import com.totemsoft.page.model.entity.Tag;
import com.totemsoft.page.model.entity.TagType;
import com.totemsoft.page.model.entity.exchangerates.Currency;
import com.totemsoft.page.model.entity.marketstack.Exchange;
import com.totemsoft.page.model.entity.marketstack.ExchangeTicker;
import com.totemsoft.page.model.entity.marketstack.ExchangeTickerId;
import com.totemsoft.page.model.mapper.DropdownOptionMapper;
import com.totemsoft.page.model.mapper.MarketStackMapper;
import com.totemsoft.page.model.mapper.PageMapper;
import com.totemsoft.page.model.mapper.SetupMapper;
import com.totemsoft.page.repository.CurrencyRepository;
import com.totemsoft.page.repository.ExchangeRepository;
import com.totemsoft.page.repository.ExchangeTickerRepository;
import com.totemsoft.page.repository.KeyRepository;
import com.totemsoft.page.repository.KeyTagRepository;
import com.totemsoft.page.repository.TagRepository;
import com.totemsoft.page.repository.TagTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@PreAuthorize(SecurityConfig.HAS_ROLE_SETUP)
@RequiredArgsConstructor
@Log4j2
public class SetupService {

    private final KeyTaggingService keyTaggingService;

    private final DropdownOptionMapper dropdownOptionMapper;
    private final MarketStackMapper marketStackMapper;
    private final PageMapper pageMapper;
    private final SetupMapper setupMapper;

    private final CurrencyRepository currencyRepository;
    private final ExchangeRepository exchangeRepository;
    private final ExchangeTickerRepository exchangeTickerRepository;
    private final KeyRepository keyRepository;
    private final KeyTagRepository keyTagRepository;
    private final TagRepository tagRepository;
    private final TagTypeRepository tagTypeRepository;

    @Transactional
    public Long saveTag(TagDto dto) {
        log.trace("saving: {}", dto);
        final var id = dto.getId();
        Tag entity;
        if (id == null) {
            entity = pageMapper.mapTag(dto);
        } else {
            entity = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Tag.class));
            entity.setName(dto.getName());
            entity.setTitle(dto.getTitle());
        }
        entity = tagRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer saveTagType(TagTypeDto dto) {
        log.trace("saving: {}", dto);
        final var id = dto.getId();
        TagType entity;
        if (id == null) {
            entity = pageMapper.mapTagType(dto);
        } else {
            entity = tagTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, TagType.class));
            entity.setName(dto.getName());
            entity.setTitle(dto.getTitle());
        }
        entity = tagTypeRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public SearchData<TagTypeDto> findTagTypes() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return SearchData.<TagTypeDto>builder()
            .records(pageMapper.mapTagType(tagTypes))
            .build();
    }

    @Transactional
    public List<DropdownOption> tagTypeDropdownOptions() {
        final var tagTypes = tagTypeRepository.findAll(Sort.by("title"));
        return dropdownOptionMapper.mapTagType(tagTypes);
    }

    @Transactional
    public SearchData<TagDto> findTagsByKey(long keyId) {
        final var key = keyRepository.findById(keyId)
            .orElseThrow(() -> new EntityNotFoundException(keyId, Key.class));
        return SearchData.<TagDto>builder()
            .records(setupMapper.mapKey(key).getTags())
            .build();
    }

    @Transactional
    public SearchData<KeyDto> findKeys() {
        final var keys = keyRepository.findAll(Sort.by("title"));
        return SearchData.<KeyDto>builder()
            .records(setupMapper.mapKey(keys))
            .build();
    }

    @Transactional
    public Long saveKey(KeyDto dto) {
        log.trace("saving: {}", dto);
        final var id = dto.getId();
        Key entity;
        if (id == null) {
            entity = setupMapper.mapKey(dto);
        } else {
            entity = keyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Key.class));
            entity.setName(dto.getName());
            entity.setTitle(dto.getTitle());
        }
        entity = keyRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void saveKeyTags(long keyId, List<Long> tagIds) {
        keyTagRepository.deleteAllByKeyId(keyId);
        tagIds.forEach(tagId -> keyTagRepository.save(
            KeyTag.builder()
                .keyId(keyId)
                .tagId(tagId)
                .build()
            )
        );
    }

    @Transactional
    public SearchData<CurrencyDto> findCurrencies() {
        final var currencies = currencyRepository.findAll(
            Sort.by("base").descending().and(Sort.by("code")));
        return SearchData.<CurrencyDto>builder()
            .records(pageMapper.mapCurrency(currencies))
            .build();
    }

    @Transactional
    public String saveCurrency(CurrencyDto dto) {
        log.trace("saving: {}", dto);
        final var code = dto.getCode();
        var entity = currencyRepository.findById(code)
            .orElseThrow(() -> new EntityNotFoundException(code, Currency.class));
        entity.setBase(dto.getBase());
        entity = currencyRepository.save(entity);
        return entity.getCode();
    }

    @Transactional
    public SearchData<ExchangeDto> findExchanges(
            Pagination pagination) {
        final Integer total;
        if (pagination.getTotal() == null) {
            total = (int) exchangeRepository.count();
        } else {
            total = pagination.getTotal();
        }
        final var sort = Sort.by("base").descending().and(Sort.by("city")).and(Sort.by("mic"));
        return SearchData.<ExchangeDto>builder()
            .records(marketStackMapper.mapExchange(
                exchangeRepository.findAll(PageRequest.of(
                    pagination.getPage(),
                    pagination.getLimit(),
                    sort))
                .getContent()
            ))
            .offset(pagination.getOffset())
            .limit(pagination.getLimit())
            .total(total)
            .build();
    }

    @Transactional
    public String saveExchange(ExchangeDto dto) {
        log.trace("saving: {}", dto);
        final var mic = dto.getMic();
        var entity = exchangeRepository.findById(mic)
            .orElseThrow(() -> new EntityNotFoundException(mic, Exchange.class));
        entity.setBase(dto.getBase());
        entity = exchangeRepository.save(entity);
        keyTaggingService.saveTag(entity);
        return entity.getMic();
    }

    @Transactional
    public SearchData<ExchangeTickerDto> findExchangeTickers(
            Optional<String> mic,
            Pagination pagination) {
        final Integer total;
        if (mic.isPresent()) {
            total = exchangeTickerRepository.countByMic(mic.get());
        } else {
            total = pagination.getTotal();
        }
        final var sort = Sort.by("base").descending().and(Sort.by("symbol"));
        return SearchData.<ExchangeTickerDto>builder()
            .records(mic.isEmpty() ? List.of() : marketStackMapper.mapExchangeTicker(
                exchangeTickerRepository.findByMic(mic.get(),
                    PageRequest.of(
                        pagination.getPage(),
                        pagination.getLimit(),
                        sort))
            ))
            .offset(pagination.getOffset())
            .limit(pagination.getLimit())
            .total(total)
            .build();
    }

    @Transactional
    public void saveExchangeTicker(ExchangeTickerDto dto) {
        log.trace("saving: {}", dto);
        final var mic = dto.getMic();
        final var symbol = dto.getSymbol();
        var entity = exchangeTickerRepository.findById(new ExchangeTickerId(mic, symbol))
            .orElseThrow(() -> new EntityNotFoundException(mic, ExchangeTicker.class));
        entity.setBase(dto.getBase());
        entity = exchangeTickerRepository.save(entity);
        keyTaggingService.saveTag(entity);
    }

}
