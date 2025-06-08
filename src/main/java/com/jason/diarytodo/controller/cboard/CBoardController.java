package com.jason.diarytodo.controller.cboard;

import com.jason.diarytodo.domain.cboard.*;
import com.jason.diarytodo.domain.common.AttachmentReqDTO;
import com.jason.diarytodo.domain.common.AttachmentRespDTO;
import com.jason.diarytodo.domain.common.MyResponse;
import com.jason.diarytodo.service.cboard.CBoardService;
import com.jason.diarytodo.util.FileUploader;
import com.jason.diarytodo.util.GetClientIpAddr;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controller 어노테이션: 스프링에서 컨트롤러로 인식
@Controller
// Slf4j 로그 사용
@Slf4j
// RequiredArgsConstructor: final 필드에 대해 생성자 자동 생성
@RequiredArgsConstructor
public class CBoardController {

  // 게시글 관련 서비스 주입
  private final CBoardService cBoardService;
  // 파일 업로드 유틸리티 주입
  private final FileUploader fileUploader;

  /**
   * GET: 요청(pageNo=1, pageSize=15, 나머지 null) -> 서비스계층에 전달 -> 응답받음
   * @param pageCBoardReqDTO
   * @param model
   * @return
   */
  @GetMapping({"/cboard", "/cboard/list"})
  public String list(PageCBoardReqDTO pageCBoardReqDTO, Model model) {

    log.info("pageCBoardReqDTO: {}", pageCBoardReqDTO);

    // 페이징 정보를 이용해 게시글 목록 조회
    PageCBoardRespDTO<CBoardRespDTO> pageCBoardRespDTO = cBoardService.getPostsByPage(pageCBoardReqDTO);

    log.info("pageCBoardRespDTO: {}", pageCBoardRespDTO);

    model.addAttribute("pageCBoardRespDTO", pageCBoardRespDTO);

    // 뷰 템플릿 반환
    return "/cboard/list";
  }

  // GET 요청: /cboard/detail로 접근 시 게시글 상세 페이지 반환
  @GetMapping("/cboard/detail")
  public String detail(@RequestParam(value = "boardNo", required = false, defaultValue = "-1") int boardNo,
                       PageCBoardReqDTO pageCBoardReqDTO,
                       Model model,
                       HttpServletRequest req) {

    // 클라이언트의 IP주소를 얻어와서 서비스단에 전달
    String ipAddr = GetClientIpAddr.getClientIp(req);

    // 조회수 로직을 거친 후 게시글 불러오기
    CBoardRespDTO cBoardRespDTO = cBoardService.getPostByBoardNoWithIp(boardNo, ipAddr);

    // 해당 게시글의 첨부파일 정보 조회
    List<AttachmentRespDTO> attachmentRespDTOS = cBoardService.getAttachmentsInfo("post", boardNo);
    // 로그에 게시글 및 첨부파일 정보 출력
    log.info(cBoardRespDTO.toString());
    log.info(attachmentRespDTOS.toString());

    // 모델에 게시글, 첨부파일 정보 추가
    model.addAttribute("cBoardRespDTO", cBoardRespDTO);
    model.addAttribute("attachmentRespDTOS", attachmentRespDTOS);

    // model.addAttribute("pageCBoardReqDTO", pageCBoardReqDTO);

    /** pageCBoardReqDTO를 model에 addAttribute 안 해도 되는 이유
     * 컨트롤러의 파라미터로커맨드(또는 폼) 객체를 선언하면, 스프링은 이 객체를 자동으로 생성
     * 요청 파라미터를 바인딩해주고, 자동으로 모델에 등록해줌
     */

    // 뷰 템플릿 반환
    return "/cboard/detail";
  }

  // GET 요청: /cboard/write로 접근 시 게시글 작성 폼 페이지 반환
  @GetMapping("/cboard/write")
  public String write(Model model) {
    // 빈 객체 생성 후 뷰로 객체를 전달
    model.addAttribute("cBoardReqDTO", new CBoardReqDTO());
    // 뷰 템플릿 반환
    return "/cboard/write";
  }

  // POST 요청: 게시글 등록 처리
  @PostMapping("/cboard/write")
  public ResponseEntity<MyResponse<Map<String, Object>>> write(
    @Valid @ModelAttribute("cBoardReqDTO") CBoardReqDTO cBoardReqDTO,
    BindingResult bindingResult,
    @RequestPart(value="uploadfiles", required = false) List<MultipartFile> uploadfiles,
    HttpSession session) throws IOException {

    log.info("cBoardReqDTO: {}", cBoardReqDTO);
    log.info("uploadfiles: {}", uploadfiles);


    // 에러가 있으면, 에러 정보를 맵에 담아 반환
    if(bindingResult.hasErrors()) {
      log.info("bindingResult: {}", bindingResult);
      Map<String, String> errorMap = new HashMap<>();

      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
      }

      Map<String, Object> data = new HashMap<>();
      data.put("errors", errorMap);

      return ResponseEntity.ok(MyResponse.success(data));
    }

    // 파일 → DTO 변환
    List<AttachmentReqDTO> attachmentReqDTOS = new ArrayList<>();
    if (uploadfiles != null && !uploadfiles.isEmpty()) {
      attachmentReqDTOS = fileUploader.saveFiles(uploadfiles);
    }
    cBoardReqDTO.setAttachmentList(attachmentReqDTOS);
    log.info("2. 변환된 파일 DTO: {}", attachmentReqDTOS); // 변환 결과 확인

    // 글 등록
    cBoardService.registerPost(cBoardReqDTO);

    Map<String, Object> data = new HashMap<>();
    data.put("redirectUrl", "/cboard/detail?boardNo=" + cBoardReqDTO.getBoardNo());

    // cBoardRequestDTO에 boardNo를 set되었으므로, get하여 GET요청하도록 redirect함
    // return "redirect:/cboard/detail?boardNo=" + cBoardReqDTO.getBoardNo();
    return ResponseEntity.ok(MyResponse.success(data));
  }

  // GET 요청: /cboard/reply로 접근 시 답글 작성 폼 페이지 반환
  @GetMapping("/cboard/reply")
  public String reply(@RequestParam(value = "ref") int ref,
                              @RequestParam(value = "step") int step,
                              @RequestParam(value = "refOrder") int refOrder,
                              PageCBoardReqDTO pageCBoardReqDTO, Model model) {
    // 빈 객체 생성 후
    CBoardReqDTO cBoardReqDTO = new CBoardReqDTO();

    // 뷰단에서 파라미터로 받아온 원래의 게시글의 ref, step, refOrder를 set한 후
    cBoardReqDTO.setRef(ref);
    cBoardReqDTO.setStep(step);
    cBoardReqDTO.setRefOrder(refOrder);

    // 뷰로 객체를 전달
    model.addAttribute("cBoardReqDTO", cBoardReqDTO);

    // 뷰 템플릿 반환
    return "/cboard/reply";
  }

  @PostMapping("/cboard/reply")
  public String reply(@Valid @ModelAttribute("cBoardReqDTO") CBoardReqDTO cBoardReqDTO,
                      BindingResult bindingResult,
                      PageCBoardReqDTO pageCBoardReqDTO,
                      Model model) {
    log.info("cBoardReqDTO: " + cBoardReqDTO);
    if(bindingResult.hasErrors()) {
      return "/cboard/reply";
    }

    // 글 등록
    cBoardService.registerReply(cBoardReqDTO);

    return "redirect:/cboard/detail?boardNo=" + cBoardReqDTO.getBoardNo() + "&" + pageCBoardReqDTO.getPageParams() + "&" + pageCBoardReqDTO.getSearchParams();
  }


  @GetMapping("/cboard/edit")
  public String edit(@RequestParam(value = "boardNo") int boardNo,
                       PageCBoardReqDTO pageCBoardReqDTO, Model model) {
    log.info("pageCBoardReqDTO=" + pageCBoardReqDTO);
    CBoardRespDTO cBoardRespDTO = cBoardService.getPostByBoardForModify(boardNo);

    model.addAttribute("cBoardRespDTO", cBoardRespDTO);
    // 빈 객체 생성 후 뷰로 객체를 전달
    model.addAttribute("cBoardReqDTO", new CBoardReqDTO());
    return "/cboard/edit";
  }

  @PostMapping("/cboard/edit")
  public String edit(@Valid @ModelAttribute("cBoardReqDTO") CBoardReqDTO cBoardReqDTO, BindingResult bindingResult,
                       PageCBoardReqDTO pageCBoardReqDTO, Model model) {

    log.info("pageCBoardReqDTO=" + pageCBoardReqDTO);
    // cBoardReqDTO.setBoardNo(cBoardReqDTO.getBoardNo());
    log.info("::::asfdjlkajdflkj:::::{}", cBoardReqDTO.getBoardNo());

    if(bindingResult.hasErrors()) {
      return "/cboard/edit";
    }

    cBoardService.modifyPost(cBoardReqDTO);
    log.info(":::::::::::::::::::::::::::::{}", cBoardReqDTO.getBoardNo());
    return "redirect:/cboard/detail?boardNo=" + cBoardReqDTO.getBoardNo() + "&" + pageCBoardReqDTO.getPageParams() + "&" + pageCBoardReqDTO.getSearchParams();
  }

  @PostMapping("/cboard/remove")
  public String remove(@RequestParam("boardNo") int boardNo,
                       PageCBoardReqDTO pageCBoardReqDTO, Model model) {

    log.info("pageCBoardReqDTO=" + pageCBoardReqDTO);

    cBoardService.removePost(boardNo);

    return "redirect:/cboard/list?" + "&" + pageCBoardReqDTO.getPageParams() + "&" + pageCBoardReqDTO.getSearchParams();
  }

  /*@GetMapping("/cboard/like/status/{boardNo}")
  public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable("boardNo") int boardNo, HttpSession session) {

  }*/

}
