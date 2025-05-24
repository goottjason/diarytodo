package com.jason.diarytodo.domain.common;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class AttachmentRespDTO {
  private Integer fileId;             // file_id: PK, INT(11), Auto Increment
  private String originalName;        // original_name: VARCHAR(100), NOT NULL
  private String storedName;          // stored_name: VARCHAR(150), NOT NULL
  private String storedThumbName;     // stored_thumb_name: VARCHAR(150), NULL
  private Boolean isImage;            // is_image: TINYINT(1), NOT NULL, default '0'
  private String ext;                 // ext: VARCHAR(20), NULL
  private Long size;                  // size: BIGINT(20), NULL
  private String refType;             // ref_type: ENUM('post', 'comment'), NOT NULL
  private Integer refId;              // ref_id: INT(11), NULL
  private String base64;              // base64: LONGTEXT, NULL
  private String storedPath;          // stored_path: VARCHAR(200), NULL
  private String storedThumbPath;     // stored_thumb_path: VARCHAR(200), NULL

}
