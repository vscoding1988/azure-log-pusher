package com.vscoding.azure.log.core.entity;

import com.vscoding.azure.log.core.control.reader.ReaderConfig;
import org.springframework.data.repository.CrudRepository;

public interface ReaderStateRepository extends CrudRepository<ReaderStateEntity, String> {

  /**
   * Get not started reader
   *
   * @param configClass is used to distinguish between the different reader
   * @return list of {@link ReaderStateEntity}
   */
  Iterable<ReaderStateEntity> findAllByLastCheckIsNullAndErrorIsFalseAndConfigClass(
          Class<? extends ReaderConfig> configClass);
}
