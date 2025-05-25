package com.jason.diarytodo.service.cboard;


import com.jason.diarytodo.domain.cboard.CBoardReqDTO;
import com.jason.diarytodo.domain.cboard.CBoardRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;
import com.jason.diarytodo.domain.common.AttachmentRespDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface CBoardService {

  PageCBoardRespDTO<CBoardRespDTO> getPostsByPage(PageCBoardReqDTO pageCBoardReqDTO);

  CBoardRespDTO getPostByBoardNoWithIp(int boardNo, String ipAddr);

  void registerPost(@Valid CBoardReqDTO cBoardReqDTO);

  void registerReply(@Valid CBoardReqDTO cBoardReqDTO);

  CBoardRespDTO getPostByBoardForModify(int boardNo);

  void modifyPost(@Valid CBoardReqDTO cBoardReqDTO);

  void removePost(int boardNo);

  List<AttachmentRespDTO> getAttachmentsInfo(String refType, int refId);
}
