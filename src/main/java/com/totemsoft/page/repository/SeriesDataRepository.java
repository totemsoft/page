package com.totemsoft.page.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totemsoft.page.model.entity.Key;
import com.totemsoft.page.model.entity.SeriesData;

@Repository
public interface SeriesDataRepository extends JpaRepository<SeriesData, Long> {

    List<SeriesData> findByKeyIn(Set<Key> keys);

}
