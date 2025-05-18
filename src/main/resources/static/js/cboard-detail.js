

$(function() {
  let boardNo = $("#boardNo").val(); /*[[${param.boardNo[0]}]];*/
  let pageNo = 1;
  console.log(boardNo, pageNo);
  /* let status = [[${param.status}]]; */
  // boardNo의 pageNo의 댓글을 모두 호출
  // -> 응답받은 데이터로 displayAllComments, displayPaginaation
  getAllComments(pageNo);

  $("#remove-btn").on("click", function(e) {
    e.preventDefault();
    $("#delete-modal").show();

  });
  $("#closeModalBtn").on("click", function(e) {
    e.preventDefault();
    $("#delete-modal").hide();
  })

  /* 삭제예정
  $("#modifyBtn").on("click", function(e) {
    e.preventDefault();
    const boardNo = $(this).data("boardno");
    let link = $(this).data("link");
    let url = "/board/modify?boardNo=" + boardNo;
    if(link) {
      url += `&${link}`;
    }
    location.href = url;
  });*/

  // 현재 페이지의 URL 쿼리스트링(예: ?status=success)을 파싱할 수 있는 객체 생성
  const urlSearchParams = new URLSearchParams(window.location.search);
  // 쿼리스트링에서 'status' 파라미터의 값을 가져옴 (예: "success", "failure", "authFail" 등)
  const status = urlSearchParams.get("status");
  console.log(status);
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
});



function ajaxtest() {
  $.ajax({
    url: `/ajaxtest`,
    type: "post",
    /*contentType: "application/json;charset=UTF-8",
    data: JSON.stringify(commentData),*/
    dataType: "json",
    success: function (res) {
      console.log(res);
    },
    error: function (res) {
      console.log(res);
      if (res.status == 401) {
        location.href = `/member/login?boardNo=${boardNo}`;
      }
    },
    complete: function (res) {
    }
  });
}

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

function displayAllComments(commentList) {
  // 댓글 리스트를 감쌀 ul 생성
  let output = `<ul class="list-group">`;
  if(!commentList || commentList.length == 0 ) {
    // 댓글이 없으면 "텅" 표시
    output+=`<li class="list-group-item">텅</li>`;
  } else {
    // 댓글이 있으면 각각 li로 출력
    commentList.forEach(function (comment) {
      output += `
        <li class="list-group-item">
          <!-- 아바타 이미지 -->
          <img src="/images/avatar.png" style="width:50px; height:50px; border-radius: 50px;margin-right:15px;">
          <div class="flex-grow-1">
            <!-- 댓글 내용 -->
            <div class="fw-bold mb-1">${comment.content}</div>
            <!-- 댓글 작성일 (n분전 등) -->
            <div class="small">${proccessPostDate(comment.regDate)}</div>
          </div>
          <div class="text-end">
            <div>
              <!-- 수정/삭제 버튼(아직 동작은 안함) -->
              <span style="cursor: pointer">수정</span>
              <span style="cursor: pointer">삭제</span>
            </div>
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

function displayPaginaation(data) {

}

function getAllComments(pageNo) {
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
        // 댓글 목록 표시
        displayAllComments(data.data.respDTOS);
        // 페이지네이션 표시
        displayPaginaation(data.data);
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

// 댓글 저장, 수정, 삭제 시 로그인 인증
function preAuth() {
  // Thymeleaf에서 세션 로그인 멤버를 JS 변수로 받음
  /*let commenter = [[${session.loginMember}]];*/


  /*if (commenter == null) {
    // 로그인 안 되어 있으면 로그인 페이지로 이동 (리다이렉트)
    location.href = `/member/login?redirectUrl=commboard/viewBoard&boardNo=${boardNo}`;

    // 입력한 댓글 내용이 있으면 로컬스토리지에 임시 저장
    let commentContent = $("#commentContent").val();
    if (commentContent != '') {
      localStorage.setItem("commentContent", commentContent);
    }
    return null; // 인증 실패
  } else {
    // 로그인된 사용자면 ID 반환
    console.log("로그인된 사용자 ID", commenter.memberId);
    return commenter.memberId;
  }*/
}

function saveComment() {
  // 댓글 작성자 인증
  let commenter = preAuth();
  // 댓글 내용 가져오기
  let content=$("#commentContent").val();
  console.log(boardNo + "저장하자");

  if (commenter == null) {
    // 인증 실패 시 함수 종료
    return;
  }

  // 서버로 보낼 데이터 객체 생성
  let commentData = {
    content : content,
    commenter: commenter
  };

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
        getAllComments(pageNo);
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



