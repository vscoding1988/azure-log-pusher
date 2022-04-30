package com.vscoding.azure.log.core.boundary;

import com.vscoding.azure.log.core.control.AzureService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest")
@AllArgsConstructor
public class MainController {
  private final AzureService azureService;

  @PostMapping(value = "add-log", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void addLog(@RequestBody LogSendRequest request) {
    azureService.sendLogs(request);
  }
}
