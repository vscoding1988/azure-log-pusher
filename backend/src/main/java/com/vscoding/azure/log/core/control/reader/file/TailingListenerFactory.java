package com.vscoding.azure.log.core.control.reader.file;

import com.vscoding.azure.log.core.entity.LogRepository;
import com.vscoding.azure.log.core.entity.ProcessedLogRepository;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TailingListenerFactory {

  private final LogRepository logRepository;
  private final ProcessedLogRepository processedLogRepository;

  /**
   * Create tailing listener
   *
   * @param state reader config as {@link ReaderStateEntity}
   * @param path log path
   * @return created {@link TailingListener}
   */
  public TailingListener createTailingListener(ReaderStateEntity state, String path) {
    return new TailingListener(logRepository, processedLogRepository, state, path);
  }
}
