package com.vscoding.azure.log.core.entity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<LogEntity, Long> {

  Iterable<LogEntity> findAll(PageRequest request);
}
