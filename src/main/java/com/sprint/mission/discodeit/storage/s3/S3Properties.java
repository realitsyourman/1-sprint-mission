package com.sprint.mission.discodeit.storage.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class S3Properties {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;
  private Integer presignedUrlExpiration;
}
