package com.jason.diarytodo.controller.cboard;


import com.jason.diarytodo.domain.cboard.*;
import com.jason.diarytodo.domain.common.AttachmentReqDTO;
import com.jason.diarytodo.service.cboard.CBoardService;
import com.jason.diarytodo.util.FileUploader;
import com.jason.diarytodo.util.GetClientIpAddr;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CBoardController {

  private final CBoardService cBoardService;
  private final FileUploader fileUploader;

  // ================== 목록(list) 불러오기
  @GetMapping({"/cboard", "/cboard/list"})
  public String index(PageCBoardReqDTO pageCBoardReqDTO, Model model) {
    PageCBoardRespDTO<CBoardRespDTO> pageCBoardRespDTO = cBoardService.getPostsByPage(pageCBoardReqDTO);
    model.addAttribute("pageCBoardRespDTO", pageCBoardRespDTO);
    return "/cboard/list";
  }

  // ================== 게시글(detail) 불러오기
  @GetMapping("/cboard/detail")
  public String detail(@RequestParam(value = "boardNo", required = false, defaultValue = "-1") int boardNo,
                       PageCBoardReqDTO pageCBoardReqDTO, Model model, HttpServletRequest req) {

    // 클라이언트의 IP주소를 얻어와서 서비스단에 전달
    String ipAddr = GetClientIpAddr.getClientIp(req);

    // 조회수 로직을 거친 후 게시글 불러오기
    CBoardRespDTO cBoardRespDTO = cBoardService.getPostByBoardNoWithIp(boardNo, ipAddr);

    model.addAttribute("cBoardRespDTO", cBoardRespDTO);

    // model.addAttribute("pageCBoardReqDTO", pageCBoardReqDTO);

    /* [[ 생략해도 되는 이유? ]]
    컨트롤러의 파라미터로 PageCBoardReqDTO pageCBoardReqDTO와 같이
    커맨드(또는 폼) 객체를 선언하면, 스프링은 이 객체를 자동으로 생성하고,
    요청 파라미터를 바인딩해주고, 자동으로 모델에 등록
     */

    return "/cboard/detail";
  }



  /* write를 통해, "GET -> 뷰(Form) -> POST" 흐름을 잘 파악하자!
  1. GET요청 : 컨트롤러에서 cBoardRequestDTO 객체(빈 객체)를 모델에 담아 뷰로 전달
  2. 폼 렌더링시: 전달받은 객체의 필드에 폼의 입력필드 값이 연결됨
    - 자바 객체의 필드와 폼의 입력필드가 연결되기 위해서는  입력필드 태그에 th:field="*{title}" 를 입력함으로써 연결됨
    - id="title", name="title", value="cBoardRequestDTO 객체의 title의 값(빈 객체이므로 null)"
  3. 폼 제출(POST) : 입력필드에 입력한 값이 객체에 자동 바인딩(자바객체에 채워짐)되어 컨트롤러에 전달됨
   */

  // ================== 게시글 등록하기
  @GetMapping("/cboard/write")
  public String write(Model model) {
    // 빈 객체 생성 후 뷰로 객체를 전달
    model.addAttribute("cBoardReqDTO", new CBoardReqDTO());
    return "/cboard/write";
  }

  @PostMapping("/cboard/write")
  public String write(@Valid @ModelAttribute("cBoardReqDTO") CBoardReqDTO cBoardReqDTO,
                      BindingResult bindingResult,
                      @RequestPart(value="uploadfiles", required = false)List<MultipartFile> uploadfiles) throws IOException {
    /*
    @Valid : 각 필드를 유효성 검사하여 실패한 필드와 메시지가 BindingResult 객체에 저장됨
    @ModelAttribute : 뷰에서 컨트롤러로 입력한 데이터가 자바 객체의 각 필드에 바인딩되어 전달됨
    BindingResult : @ModelAttribute 바로 뒤에 붙여야하고, 에러가 있으면 정보를 수집
     */

    // 에러가 있으면, 뷰를 생성하여 전송
    if(bindingResult.hasErrors()) {
      /*
      Spring MVC가 /templates/cboard/write.html를 찾고,
      템플릿엔진(타임리프)이 데이터(입력값, 에러메시지 등)을 채워넣고, 최종 HTML을 생성하여 브라우저에 전송함
       */
      return "/cboard/write";
    }

    if (uploadfiles != null && !uploadfiles.isEmpty()) {
      List<AttachmentReqDTO> attachmentReqDTOS = fileUploader.saveFiles(uploadfiles);
      cBoardReqDTO.setUploadfiles(attachmentReqDTOS);
    }

    // 글 등록
    cBoardService.registerPost(cBoardReqDTO);

    // cBoardRequestDTO에 boardNo를 set되었으므로, get하여 GET요청하도록 redirect함
    return "redirect:/cboard/detail?boardNo=" + cBoardReqDTO.getBoardNo();
  }

  // ================== 답글 등록하기
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

    /*if (!model.containsAttribute("pageCBoardReqDTO")) {
      model.addAttribute("pageCBoardReqDTO", pageCBoardReqDTO);
    }*/

    /*
     Spring MVC가 /templates/cboard/reply.html를 찾고,
     템플릿엔진(타임리프)이 최종 HTML을 생성하여 브라우저에 전송함
     */
    return "/cboard/reply";
  }

  @PostMapping("/cboard/reply")
  public String reply(@Valid @ModelAttribute("cBoardReqDTO") CBoardReqDTO cBoardReqDTO, BindingResult bindingResult,
                              PageCBoardReqDTO pageCBoardReqDTO, Model model) {
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

}
