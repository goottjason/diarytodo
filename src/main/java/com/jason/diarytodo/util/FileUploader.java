package com.jason.diarytodo.util;


import com.jason.diarytodo.domain.common.AttachmentReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploader {
  @Value("${file.upload-base-dir}")
  private String uploadBaseDir;

  @Value("${file.upload-url-path}")
  private String uploadUrlPath;

  public List<AttachmentReqDTO> saveFiles(List<MultipartFile> uploadFiles) throws IOException {
    log.info("Uploading files: {}", uploadFiles.size());

    List<AttachmentReqDTO> attachmentReqDTOS = new ArrayList<>();

    if(uploadFiles == null || uploadFiles.size() == 0) return attachmentReqDTOS;


    // 현재 날짜로 디렉토리 생성
    String dateForCreateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    String dirPath = uploadBaseDir + dateForCreateDir;
    Files.createDirectories(Paths.get(dirPath));

    log.info("폴더 생성 완료");

    for (MultipartFile uploadFile : uploadFiles) {
      if(uploadFile.isEmpty()) continue; // 이중검토?
      String originalName = uploadFile.getOriginalFilename();
      String uuid = UUID.randomUUID().toString();
      String ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
      String storedName = uuid + "_" + originalName;
      String storedPath = dirPath + "/" + storedName;
      uploadFile.transferTo(new File(storedPath));

      String base64 = null;
      String storedThumbName = null;
      String storedThumbPath = null;
      Boolean isImage = ImageMimeType.isImage(ext);
      if (isImage) {
        storedThumbName = "thumb_" + storedName;
        storedThumbPath = dirPath + "/" + storedThumbName;

        BufferedImage thumbImg = Thumbnails
          .of(new File(storedPath))
          .size(200, 200)
          .asBufferedImage();
        ImageIO.write(thumbImg, ext, new File(storedThumbPath));
        byte[] imageToBytes = Files.readAllBytes(Paths.get(storedThumbPath));

        base64 = Base64.getEncoder().encodeToString(imageToBytes);
      }

      String relativePath = storedPath.replaceFirst(Pattern.quote(uploadBaseDir), "/upload/");
      String relativeThumbPath = null;
      if (storedThumbPath != null) {
        relativeThumbPath = storedThumbPath.replaceFirst(Pattern.quote(uploadBaseDir), "/upload/");
      }

      AttachmentReqDTO attachmentReqDTO = AttachmentReqDTO.builder()
        // .fileId()
        .originalName(originalName)
        .storedName(storedName)
        .storedThumbName(storedThumbName)
        .isImage(isImage)
        .ext(ext)
        .size(uploadFile.getSize())
        // .refType()
        // .refId()
        .base64(base64)
        .storedPath(relativePath)
        .storedThumbPath(relativeThumbPath)
        .build();

      attachmentReqDTOS.add(attachmentReqDTO);
    }
    return attachmentReqDTOS;
  }

  public void deleteFile(String relativePath) {
    String storedPath = (uploadBaseDir + relativePath.replace(uploadUrlPath, "")).replace("/", File.separator);
    File file = new File(storedPath);
    if(file.exists()) file.delete();
  }
}
