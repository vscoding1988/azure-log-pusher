package com.vscoding.azure.log.core.control.reader.file;

import com.google.gson.Gson;
import com.vscoding.azure.log.core.entity.ReaderStateEntity;
import com.vscoding.azure.log.core.entity.ReaderStateRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This reader will trail given file and push every minute the files to the sending que
 */
@Slf4j
@Service
public class TailingFileReader {

  private final long tailingDelay;
  private final ReaderStateRepository stateRepository;
  private final TailingListenerFactory factory;
  private final List<ReaderStateEntity> currentRunning = new ArrayList<>();

  public TailingFileReader(@Value("${app.reader.file.delay:1000}") long tailingDelay,
          TailingListenerFactory factory,
          ReaderStateRepository stateRepository) {
    this.tailingDelay = tailingDelay;
    this.factory = factory;
    this.stateRepository = stateRepository;
  }

  /**
   * Searches for new reader and starts them
   */
  @Scheduled(cron = "0 * * * * *")
  protected void startNewReader() {
    stateRepository.findAllByLastCheckIsNullAndErrorIsFalseAndConfigClass(TailingFileConfig.class)
            .forEach(this::run);

    // To reduce DB write access, write only once a minute
    stateRepository.saveAll(currentRunning);
  }

  /**
   * Read first the whole file and attach after that the tailing for coming changes.
   *
   * @param state start reader for given {@link ReaderStateEntity}
   */
  private void run(ReaderStateEntity state) {
    var config = new Gson().fromJson(state.getConfig(), TailingFileConfig.class);
    log.info("Start Reader with id = '{}' for path '{}'", state.getId(), config.getPath());

    if (!new File(config.getPath()).exists()) {
      log.error("Could not find file below '{}'", config.getPath());
      state.setError(true);
      return;
    }

    var listener = factory.createTailingListener(state, config.getPath());

    log.info("Start reading already present lines '{}'.", config.getPath());
    // first read the file as whole
    try (var br = new BufferedReader(new FileReader(config.getPath()))) {

      br.lines().forEach(listener::handle);

      listener.endOfFileReached();
    } catch (Exception e) {
      log.error("Error parsing file '{}'", config.getPath(), e);
    }

    log.info("Start tailing '{}'.", config.getPath());

    // attach tailing Tailer.create will create a separate thread und tail log inside
    Tailer.create(new File(config.getPath()), listener, tailingDelay, true);
    currentRunning.add(state);
  }
}
