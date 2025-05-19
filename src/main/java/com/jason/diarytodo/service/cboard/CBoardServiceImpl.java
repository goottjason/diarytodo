package com.jason.diarytodo.service.cboard;



import com.jason.diarytodo.domain.cboard.CBoardReqDTO;
import com.jason.diarytodo.domain.cboard.CBoardRespDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardReqDTO;
import com.jason.diarytodo.domain.cboard.PageCBoardRespDTO;
import com.jason.diarytodo.mapper.cboard.CBoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CBoardServiceImpl implements CBoardService {
  private final CBoardMapper cBoardMapper;

  @Override
  public PageCBoardRespDTO<CBoardRespDTO> getPostsByPage(PageCBoardReqDTO pageCBoardReqDTO) {
    // 현재 총 게시글의 수를 카운트
    int totalPosts = cBoardMapper.selectTotalPostsCount(pageCBoardReqDTO);

    // pageNo를 토대로 만들어진 offset과 pageSize로 posts를 리스트에 담음
    List<CBoardRespDTO> cBoardRespDTOS = cBoardMapper.selectPostsByPage(pageCBoardReqDTO);

    /* 요청할 당시의 pageHBoardRequestDTO의 값이 필요한 이유?
    blockEndPage, blockStartPage, lastPage, showPrevBlockButton, showNextBlockButton의 값을 응답할 때
    pageNo와 pageSize를 토대로 식이 구성되므로 필요함
     */

    // 커스텀 빌더메서드
    return PageCBoardRespDTO.<CBoardRespDTO>withPageInfo()
      .pageHBoardReqDTO(pageCBoardReqDTO)
      .respDTOS(cBoardRespDTOS)
      .totalPosts(totalPosts)
      .build();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CBoardRespDTO getPostByBoardNoWithIp(int boardNo, String ipAddr) {

    // 1. 게시글 불러오기
    CBoardRespDTO cBoardRespDTO= cBoardMapper.selectPostByboardNo(boardNo);

    // 2. selfhboardlog 테이블에서 ipAddr 유저가 boardNo 글의 최근읽은시점의 현재시점과 차이값을 시간으로 구하기
    /*
    조회한 값이 없다면, -1
    조회한 값이 있다면, 그 시간차이를 반환
     */
    int dateDiff = cBoardMapper.selectDateDiff(boardNo, ipAddr);

    if(dateDiff == -1) {
      // 3. 최초 조회면 로그 추가
      cBoardMapper.insertLog(ipAddr, boardNo);
      // DB에서 조회수 1 증가
      cBoardMapper.incrementReadCount(boardNo);
      // 증가된 조회수를 cBoardRespDTO에 set하기
      cBoardRespDTO.setViewCount(cBoardRespDTO.getViewCount() + 1);
    } else if (dateDiff >= 24) {
      // 3. 최근읽은 시점으로부터 24시간이 넘은 경우, 로그의 readWhen을 현재시점으로 업데이트
      cBoardMapper.updateLog(ipAddr, boardNo);
      // DB에서 조회수 1 증가
      cBoardMapper.incrementReadCount(boardNo);
      // 증가된 조회수를 cBoardRespDTO에 set하기
      cBoardRespDTO.setViewCount(cBoardRespDTO.getViewCount() + 1);
    }

    // 과정을 거친 후, 게시글 반환
    return cBoardMapper.selectPostByboardNo(boardNo);
  }

  @Override
  public void registerPost(CBoardReqDTO cBoardReqDTO) {
    // 1. 게시글 저장
    cBoardMapper.insertNewPost(cBoardReqDTO);
    cBoardMapper.updateSetRefToBoardNo(cBoardReqDTO.getBoardNo());
  }

  @Override
  public void registerReply(CBoardReqDTO cBoardReqDTO) {
    // 1. insert할 답글보다 refOrder가 큰 답글들에 대해서 (과거글은 아래에 보여지므로) refOrder를 하나씩 더하여 뒤로 미룸
    cBoardMapper.updateSetRefOrderPlusOne(cBoardReqDTO.getRef(), cBoardReqDTO.getRefOrder());

    // 2. 등록할 답글의 step은 원글의 +1, refOrder도 원글의 +1 (바로 아래 계단(step)에, 바로 밑(refOrder)에 보여져야 하므로)
    cBoardReqDTO.setStep(cBoardReqDTO.getStep() + 1);
    cBoardReqDTO.setRefOrder(cBoardReqDTO.getRefOrder() + 1);

    log.info("◆insertNewReply : {}", cBoardMapper.insertNewReply(cBoardReqDTO));

    /*if (cBoardMapper.insertNewReply(cBoardReqDTO) == 1) {
    }*/
  }

  @Override
  public CBoardRespDTO getPostByBoardForModify(int boardNo) {
    return cBoardMapper.selectPostByboardNo(boardNo);
  }

  @Override
  public void modifyPost(CBoardReqDTO cBoardReqDTO) {
    cBoardMapper.updatePost(cBoardReqDTO);
  }

  @Override
  public void removePost(int boardNo) {
    cBoardMapper.deletePost(boardNo);
  }
}
