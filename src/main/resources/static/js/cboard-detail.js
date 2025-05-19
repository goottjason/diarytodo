
// 전역변수 설정
let pageNo = 1;
let currentPageNo = 1;
/* let status = [[${param.status}]]; */


$(function() {

  getAllComments(currentPageNo); // 댓글 호출 (with Page)

  // 페이지번호 클릭시
  $(document).on("click", ".page-btn", function(e) {
    e.preventDefault();
    let pageNo = $(this).data("page");
    getAllComments(pageNo);
  })

  // 좋아요 클릭시,
  $("#heart-icon").click(function() {
    let isLiked = $(this).hasClass("fa-solid");
    let doesLike = isLiked ? 'dislike' : 'like';

    sendBoardLike(doesLike);
  });

  // ========================= 게시글 삭제 관련 =========================
  // 게시글 삭제 버튼 클릭시 (아래와 병합 가능한지 체크)
  $("#post-remove-btn").on("click", function(e) {
    e.preventDefault();
    $("#post-delete-modal").show();

  });
  // 삭제확인모달의 취소 버튼 클릭시 (cf. 삭제확인모달의 삭제 버튼은 submit 폼전송)
  $(".modal-reset-btn").on("click", function(e) {
    e.preventDefault();
    $("#post-delete-modal").hide();
  })

  // ========================= 게시글 수정 관련 =========================
  // ========================= 알림 토스트 처리 =========================
  // 현재 페이지의 URL 쿼리스트링(예: ?status=success)을 파싱할 수 있는 객체 생성
  const urlSearchParams = new URLSearchParams(window.location.search);
  // 쿼리스트링에서 'status' 파라미터의 값을 가져옴 (예: "success", "failure", "authFail" 등)
  const status = urlSearchParams.get("status");
  if(status == "success") {
    $(".toast-body").html("수정완료");
    $("#toastMessage").show();
    // 2초 후에 toast 메시지 닫기
    setTimeout(function() {
      $("#toastMessage").fadeOut();
    }, 3000);
  } else if (status == "failure") {
    $(".toast-body").html("수정실패");
    $("#toastMessage").show();
    // 2초 후에 toast 메시지 닫기
    setTimeout(function() {
      $("#toastMessage").fadeOut();
    }, 3000);
  } else if (status == "authFail") {
    $(".toast-body").html("권한이 없습니다.");
    $("#toastMessage").show();
    setTimeout(function() {
      $("#toastMessage").fadeOut();
    }, 3000);
  }

  // 댓글 인풋 엔터
  $('#commentContent').on('keydown', function(e) {
    if (e.key == "Enter") {
      saveComment();
    }
  });
});



function sendBoardLike(doesLike) {
  let who = "user01";
  let boardNo = $("#boardNo").val();
  // boardNo = $("#boardNo").data("boardNo");
  console.log(who, boardNo, doesLike);
  axios.post("/commboard/boardlike", null, { /*★*/
    params: {
      "who": who,
      "boardNo": boardNo,
      "like": doesLike}
  })
    .then(function (response) {
      console.log(response);
      if(response == "success") {
        loadLikeStatus();
      }
    })
    .catch(function (error) {
      console.log(error);
    });
}

function loadLikeStatus() {
  let boardNo = $("#boardNo").val();
  axios.get(`/commboard/boardlike/status/${boardNo}`) /*★*/
    .then(function (response) {
      console.log(response);
      if(response == "success") {
      }
    })
    .catch(function (error) {
      console.log(error);
    });
}

// 댓글의 '방금전, 몇분전, 몇시간전 등'
function proccessPostDate(regDate) {
  // 현재 시간을 구함
  const now = new Date();
  // 댓글 작성일(문자열)을 Date 객체로 변환
  const postDate = new Date(regDate);

  // 두 시간의 차이를 초(second) 단위로 계산
  let diff = (now - postDate) / 1000;

  // 시간 단위별로 비교할 배열 (일, 시간, 분)
  const times = [
    {name : "일", time: 24 * 60 * 60},
    {name : "시간", time: 60 * 60},
    {name : "분", time: 60},
  ]

  // 큰 단위부터 차례로 비교 (일, 시간, 분)
  for (let val of times) {
    let betweenTime = Math.floor(diff / val.time); // 해당 단위로 몇 차이 나는지
    if (betweenTime > 0 && val.name != "일") {
      // 1시간, 1분 이상일 때: "n시간전", "n분전"으로 반환
      return betweenTime + val.name + "전";
    } else if (betweenTime > 0 && val.name == "일") {
      // 1일 이상이면 날짜 포맷으로 반환
      return postDate.toLocaleString();
    }
  }
  // 1분 미만일 때 "방금전" 반환
  return "방금전";
}

// 데이터로부터 댓글 출력하는 output
function displayAllComments(commentData) {
  let commentList = commentData.pageCBoardRespDTO.respDTOS;
  let loginIdByLoginMember = commentData.loginIdByLoginMember;
  console.log(commentData.respDTOS);

  // 댓글 리스트를 감쌀 ul 생성
  let output = `<ul class="list-group">`;
  if(!commentList || commentList.length == 0 ) {
    // 댓글이 없으면 "텅" 표시
    output+=`<li class="list-group-item">텅</li>`;
  } else {
    // 댓글이 있으면 각각 li로 출력
    commentList.forEach(function (comment) {
      output += `
        <li class="list-group-item" id="comment-${comment.commentId}">
          <!-- 아바타 이미지 -->
          <img src="/assets/images/avatar.png" style="width:50px; height:50px; border-radius: 50px;margin-right:15px;">
          <div class="flex-grow-1">
            <!-- 댓글 내용 -->
            <div class="fw-bold mb-1">${comment.content}</div>
            <!-- 댓글 작성일 (n분전 등) -->
            <div class="small">${proccessPostDate(comment.createdAt)}</div>
          </div>
          <div class="text-end">
            <!-- 로그인 한 유저이자 작성자인 경우, 버튼 보이도록 -->
            ${(loginIdByLoginMember == comment.commenter) ? 
            `<div>
              <!-- 수정/삭제 버튼(아직 동작은 안함) -->
              <span style="cursor: pointer" 
              onclick="editComment(${comment.commentId}, '${comment.content}');">수정</span>
              <span style="cursor: pointer"
              onclick="removeComment(${comment.commentId});">삭제</span>
            </div>`:''}
            <div class="small text-secondary">
              <!-- 댓글 작성자 -->
              ${comment.commenter}
            </div>
          </div>
        </li>
      `;
    });
  }
  output += `</ul>`;
  // 완성된 HTML을 #commentList에 삽입
  $("#commentList").html(output);
}

function editComment(commentId, content) {
  alert("!");
  const commentItem = $("#comment-" + commentId);
  let output = `
    <div class="flex-grow-1">
      <input type="text" class="form-control" value="${content}" id="edit-content-${commentId}"></input>
    </div>
    <div class="text-end">
      <button type="button" class="btn btn-success" onclick="submitEditComment(${commentId})">저장</button>
      <button type="button" class="btn btn-secondary" onclick="cancelEditComment()">취소</button>
    </div>
  `;
  commentItem.html(output);
}

function submitEditComment(commentId) {
  const modifiedContent = $("edit-content-" + commentId).val();
  if(modifiedContent == "") {
    alert("내용을 입력하세요...");
    return;
  }
  axios.patch(`/comment/${commentId}`, {
    content: newContent,
  })
    .then(function (response) {
      console.log(response);
      if(response.data.resultMessage == "SUCCESS") {
        alert("<UNK>", pageNo);
        getAllComments(currentPageNo);

      }
    })
    .catch(function (error) {
      if(error.response.data.resultMessage == "FAIL") {
        alert("수정실패");
      }
      console.log(error);
    });
}

function cancelEditComment() {
  getAllComments(currentPageNo);
}

function removeComment(commentId) {
  if (!confirm("정말 이 댓글을 삭제하시겠습니까?")) {
    return;
  } else {
    axios.delete(`/comment/${commentId}`)
      .then(function (response) {
        console.log(response);
        if(response.data.resultMessage == "SUCCESS") {
          alert("댓글 삭제 완료");
          getAllComments(currentPageNo);
        }
      })
      .catch(function (error) {
        if(error.response.data.resultMessage == "FAIL") {
          alert("댓글 삭제 실패");
        }
        console.log(error);
      });
  }
}


// 데이터로부터 페이지네이션 출력하는 output
function displayPagination(pageData) {
  console.log(pageData);
  // 댓글이 없는 경우
  if(pageData.totalPosts == 0) {
    $("#comment-pagination").html("");
    return;
  }
  let output = `<ul class="pagination justify-content-center" style="margin:20px 0">`;
  let prevPage = pageData.pageNo > 1 ? pageData.pageNo - 1 : 1;
  // prev 버튼
  output += `
    <li class="page-item ${pageData.pageNo == 1? 'disabled': ''}">
      <a class="page-link page-btn" href="#" data-page="${prevPage}">Prev</a>
    </li>`;
  // 페이지 번호 버튼
  for (let i = pageData.blockStartPage; i <= pageData.blockEndPage; i++) {
    let active = pageData.pageNo == i ? "active" : "";
    output += `
      <li class="page-item ${active}">
        <a class="page-link page-btn" href="#" data-page="${i}">${i}</a>
      </li>`;
  }
  // next 버튼
  let nextPage = pageData.pageNo < pageData.blockEndPage ? pageData.pageNo + 1 : pageData.lastPage;
  output += `
    <li class="page-item ${pageData.pageNo == pageData.lastPage ? 'disabled': ''}">
      <a class="page-link page-btn" href="#" data-page="${nextPage}">Next</a>
    </li></ul>`;
  $("#comment-pagination").html(output);
}

// 해당 페이지의 댓글 데이터 호출
function getAllComments(pageNo) {
  currentPageNo = pageNo;
  let boardNo = $("#boardNo").val();
  $.ajax({
    url: `/comment/all/${boardNo}/${pageNo}`, // 댓글 목록 요청 URL
    type: "GET", // GET 방식
    dataType: 'json', // 응답 데이터 타입
    success: function (data) {
      // 요청 성공 시
      console.log(data);
      console.log(data.resultMessage);
      if (data.resultMessage == "SUCCESS") {
        console.log(data.data);
        // 댓글 목록 표시
        displayAllComments(data.data);
        // 페이지네이션 표시
        displayPagination(data.data.pageCBoardRespDTO);
      }
    },
    error: function (data) {
      // 요청 실패 시
      console.log(data);
    },
    complete: function (data) {
      // 요청 완료 시 (성공/실패와 무관)
      console.log(data);
    }
  });
}

// 댓글 작성 요청 ajax
function saveComment() {

  // 댓글 내용 가져오기
  let boardNo = $("#boardNo").val();
  let content = $("#commentContent").val();
  console.log(boardNo + "저장하자");

  // 서버로 보낼 데이터 객체 생성
  let commentData = {
    content : content
  };
  console.log(commentData);

  // AJAX로 댓글 저장 요청
  $.ajax({
    url: `/comment/${boardNo}`, // 저장 요청 URL
    type: "POST", // POST 방식
    contentType: "application/json;charset=UTF-8", // JSON 형식
    data: JSON.stringify(commentData), // 데이터 직렬화
    dataType: 'json', // 응답 데이터 타입
    success: function (res) {
      // 저장 성공 시
      console.log(res);
      if (res.resultMessage == "SUCCESS") {
        // 입력창 비우고, 댓글 목록 새로고침
        $("#commentContent").val("");
        getAllComments(currentPageNo);
      }
    },
    error: function (res) {
      // 저장 실패 시
      console.log(res);
    },
    complete: function (res) {
      // 요청 완료 시
      console.log(res);
    }
  });
}



