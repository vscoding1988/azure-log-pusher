package com.vscoding.azure.log.core.entity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<LogEntity, Long> {

  Iterable<LogEntity> findAll(Pageable request);
}
