$(function() {

  // ====================================
  // 페이지로드, 캘린더, 데이트피커
  // ====================================

  // -------- 페이지 초기화
  initialList();
  /*countTodo();*/

  let today = new Date().toLocaleDateString().substring(0, 10);
  $("#duedate, #from, #to").val(today);
  let todayText = new Date().toLocaleDateString();
  $("#menuDateArea").html(todayText);

  // -------- 캘린더 생성 관련
  let specialDates = [];
  let myCalendar = jsCalendar.new("#my-calendar"); // 캘린더 생성

  myCalendar.onDateClick(function(event, date){
    var value = date.toLocaleDateString('sv-SE');
    document.getElementById("selected-date").value = value;

    sessionStorage.setItem("status", "calendarList");
    sessionStorage.setItem("calPickDate", value);
    doList();
  });

  // -------- |calBoldFunc()|
  // 볼드처리하는 함수
  function calBoldFunc() {
    myCalendar.onDateRender(function(date, element, info) {
      let yyyy = date.getFullYear();
      let mm = String(date.getMonth() + 1).padStart(2, '0');
      let dd = String(date.getDate()).padStart(2, '0');
      let value = yyyy + '-' + mm + '-' + dd;

      if(specialDates.includes(value)){
        element.style.fontWeight = 'bold';
        element.style.color = '#0f1d41'; //'#2453E3';
        element.style.fontSize = '16px';
      }
    });
  }

  //-------- |refreshCalStatus()|
  // 리스트를 받아와서 볼드처리하여 새로고침하는 함수
  async function refreshCalStatus() {
    specialDates = await listDuedate(); // 데이터 수신 완료까지 대기
    calBoldFunc();
    myCalendar.refresh();
  }

  refreshCalStatus();

  // -------- 데이트피커 생성 관련
  let picker = new easepick.create({
    element: "#regDateInput",
    css: ["https://cdn.jsdelivr.net/npm/@easepick/bundle@1.2.1/dist/index.css"],
    zIndex: 10,
    setup(picker) {
      picker.on('select', (e) => {
        let selectedDate = picker.getDate().format('YYYY-MM-DD');
        $('#dateViewSpan').html(`\${selectedDate}에 할일이 추가됩니다`);
        $('#dateViewSpan').show();
      });
    },
  });


  // ===============================
  // 검색창
  // ===============================

  // -------- 검색어 입력하는 동안 이벤트 --------
  $("#searchWord").on("keyup", function() {
    let searchWord = $(this).val();
    if (searchWord.length != 0) {
      doSearch("title", searchWord);
    } else {
//         let result = ajaxFunc("/todolist/selectMulti", null, null);
      doList();
    }
  });


  // ===============================
  // 정렬
  // ===============================

  // 정렬아이콘 클릭시 (오름차순, 내림차순)
  $("body").on("click", "#orderMethodSelect", function() {
    // 오름차순이면 true
    let isAsc = $(this).hasClass("fa-arrow-up-wide-short");
    $(this).toggleClass("fa-arrow-up-wide-short fa-arrow-down-wide-short");

    if(isAsc) {
      // 클릭해서 내림차순 상태로 변경
      sessionStorage.setItem("ordermethod", "desc");
    } else {
      sessionStorage.setItem("ordermethod", "asc");
    }
    doList();
  });

  // 정렬방식 선택 변경시 (마감일순, 등록일순, 제목순)
  $("body").on("change", "#orderbySelect", function() {
    sessionStorage.setItem("orderby", $("#orderbySelect").val());
    doList();
  });




  // ===============================
  // 할일추가
  // ===============================

  // -------- 할일추가 제목창에서 Enter
  $(document).on('keydown', '.regTitleInput', function(e) {
    if (e.key == "Enter") {
      addTodo();
    }
  });

  // -------- 할일추가에서 캘린더아이콘 클릭시
  $(document).on("click", "#regDuedate", function() {
//     	alert("!");
    $("#regDateInput").focus();
  });

  // -------- 중요도 아이콘이 눌렸을 때 --------
  $("body").on("click", ".regStarInput", function() {
    $(".regStarInput").toggleClass("fa-solid");
  });

  // -------- 할일추가에서 하단화살표아이콘 클릭시
  $("body").on("click", "#etcBtn", function() {
    $("#lm-toggle").toggle();
  });



  // ===============================
  // 테이블
  // ===============================

  // 전체 선택 체크박스 클릭 시
  $(document).on('change','#selectAllCheckbox', function() {

    $('.rowCheckbox').prop('checked', this.checked);
  })

  // 각 행 체크박스 클릭 시
  $(document).on('change', '.rowCheckbox', function() {
    // 하나라도 체크 해제되면 전체선택 체크박스 해제
    if (!this.checked) {
      $('#selectAllCheckbox').prop('checked', false);
    } else {
      // 모두 체크되면 전체선택 체크박스 체크
      var allChecked = $('.rowCheckbox').length === $('.rowCheckbox:checked').length;
      $('#selectAllCheckbox').prop('checked', allChecked);
    }
  });

  // -------- 완료 체크박스 클릭
  $("body").on("click", ".finishedIcon", function() {
    let dno = $(this).data("dno");
    let finished = null;
    let solid = $(this).hasClass("fa-circle-check");
    let checked = null;

    // 채워져있으면 해제
    if(solid) {
      finished = 0;
      checked = false;
    } else {
      finished = 1;
      checked = true;
    }
    let data = { "dno": dno, "finished": finished };
    let result = ajaxFunc("/todolist/updateFinished", data, "text");
    if (result == "success") {
      $("#dfinishedIcon-" + dno).toggleClass("fa-circle");
      $("#dfinishedIcon-" + dno).toggleClass("fa-circle-check");
      $("#detailfinishedIcon-"+dno).toggleClass("fa-circle");
      $("#detailfinishedIcon-"+dno).toggleClass("fa-circle-check");
      $("#dtitleTd-" + dno).toggleClass("completed", checked);
      doList();
      countTodo();
    }
  });

  // -------- 테이블에서 제목영역 클릭시 (수정모드 전환)
  $(document).on('click', '.titleTd', function() {
    let span  = $(this).children('.titleSpan');
    let input = span.siblings('.edit-input-title');

    input.val(span.text()).show().focus();
    span.hide();
  });

  // -------- 테이블에서 제목영역 클릭하여 수정 후 Enter
  $(document).on('keydown', '.edit-input-title', function(e) {
    if (e.key === "Enter") {
      let dno = $(this).data("dno");
      let value = $(this).val();
      let span = $(this).siblings('.titleSpan');
      span.text(value).show();
      $(this).hide();
      titleModify(dno, value);
      countTodo();
    }
  });

  // -------- 테이블에서 제목영역 클릭하여 수정 후 다른 영역 클릭시
  $(document).on('blur', '.edit-input-title', function() {
    let dno = $(this).data("dno");
    let value = $(this).val();
    let span = $(this).siblings('.titleSpan');
    span.text(value).show();
    $(this).hide();
    titleModify(dno, value);
    countTodo();
  });

  // -------- 테이블에서 날짜영역 클릭시 (수정모드 전환)
  $(document).on('click', '.duedateTd', function() {
    let span  = $(this).children('.duedateSpan');
    let input = span.siblings('.edit-input-duedate');
    input.val(span.text().trim()).show().focus();
    span.hide();
  });

  // -------- 테이블에서 날짜영역 클릭하여 수정 후 Enter
  $(document).on('keydown', '.edit-input-duedate', function(e) {
    if (e.key === "Enter") {
      let dno = $(this).data("dno");
      let value = $(this).val();
      let span = $(this).siblings('.duedateSpan');
      span.text(value).show();
      $(this).hide();
      duedateModify(dno, value);
      countTodo();
      refreshCalStatus();
    }
  });

  // -------- 테이블에서 날짜영역 클릭하여 수정 후 다른 영역 클릭시
  $(document).on('blur', '.edit-input-duedate', function() {
    let dno = $(this).data("dno");
    let value = $(this).val();
    let span = $(this).siblings('.duedateSpan');
    span.text(value).show();
    $(this).hide();
    duedateModify(dno, value);
    countTodo();
    refreshCalStatus();
  });

  $(document).on('click', '.page-link', function(e) {
    e.preventDefault();
    let pageNo = $(this).data('page');
    sessionStorage.setItem("pageNo", pageNo);
    doList();
  });

  // -------- 중요도 아이콘이 눌렸을 때
  $("body").on("click", ".starIcon", function() {
    let dno = $(this).data("dno");
    let star = null;
    //
    let solid = $(this).hasClass("fa-solid");
    // 채워져있으면 해제해야 함
    if(solid) {
      star = 0;
    } else {
      star = 1;
    }

    let data = { "dno": dno, "star": star };
    let result = ajaxFunc("/todolist/updateStar", data, "text");
    if (result == "success") {
      // checked가 true이면, "completed" 추가하고, false면 삭제
      $("#dstar-" + dno).toggleClass("fa-solid");
      $("#detailstar-"+dno).toggleClass("fa-solid");
      doList();
      countTodo();
    }
  });

  // -------- 더보기 버튼 클릭 시 상세 보기
  $("body").on("click", ".moreBtn", function() {
    let dno = $(this).data("dno");
    let data = {"dno": dno};
    let result = ajaxFunc("/todolist/selectone", data, null);
    let html = jQuery('<div>').html(result);
    let contents = html.find("div#ajaxTodoDetail").html();
    $("#todoDetail").html(contents);
  });

  // -------- 삭제 버튼 클릭 시
  $("body").on("click", ".delBtn", function() {
    let dno = $(this).data("dno");
    let data = { "dno": dno };
    let result = ajaxFunc("/todolist/deleteTodo", data, "text");
    $("#todoDetail").html(""); // 삭제되면 상세보기 공간 비우기
    doList();
    countTodo();
  });

  // -------- '선택수정' 버튼 클릭 시 모달 열기
  $("body").on('click','#multiEditBtn', function() {
    $('#editModalOverlay').fadeIn(150);
  });

  // -------- '선택수정'에서 모달 '취소' 버튼 또는 오버레이 클릭 시 닫기
  $("body").on('click','#modalCancelBtn, #editModalOverlay', function(e) {
    if (e.target.id === 'editModalOverlay' || e.target.id === 'modalCancelBtn') {
      $('#editModalOverlay').fadeOut(150);
    }
  });

  // -------- '선택수정'에서 모달창 클릭 시 오버레이 닫힘 방지
  $("body").on('click','#editModal', function(e) {
    e.stopPropagation();
  });

  // -------- '선택수정'에서 폼 제출 시(수정 버튼)
  $("body").on('submit','#multiEditForm', function(e) {
    e.preventDefault();
    let finished = $('input[name="finished"]:checked').val() ?? '';
    let star = $('input[name="star"]:checked').val() ?? '';
    let duedate = $('input[name="duedate"]').val() ?? '';

    let selectedArr = $('.rowCheckbox:checked').map(function(){ return $(this).data('dno'); }).get();
    updateSelectedAll(selectedArr, finished, star, duedate);

    $('#editModalOverlay').fadeOut(150);
  });

  // -------- '선택삭제' 버튼 클릭 시 모달 열기
  $("body").on('click','#multiDeleteBtn', function() {
    $('#deleteModalOverlay').fadeIn(150);
  });

  // -------- '선택삭제'에서 모달 '취소' 버튼 또는 오버레이 클릭 시 닫기
  $("body").on('click','#modalCancelBtn, #deleteModalOverlay', function(e) {
    if (e.target.id === 'deleteModalOverlay' || e.target.id === 'modalCancelBtn') {
      $('#deleteModalOverlay').fadeOut(150);
    }
  });

  // -------- '선택삭제'에서 모달창 클릭 시 오버레이 닫힘 방지
  $("body").on('click','#deleteModal', function(e) {
    e.stopPropagation();
  });

  // -------- '선택삭제'에서 폼 제출 시(삭제 버튼)
  $("body").on('submit','#multiDeleteForm', function(e) {
    e.preventDefault();
    let selectedArr = $('.rowCheckbox:checked').map(function(){ return $(this).data('dno'); }).get();
    deleteSelectedAll(selectedArr);

    $('#deleteModalOverlay').fadeOut(150);
  });



  // ================================
  // 오른쪽 사이드바
  // ================================

  // -------- 디테일 수정 버튼 --------
  $("body").on("click", ".detailModBtn", function() {
    let dno      = $(this).data("dno");
    let title    = $("#detailtitle-" + dno).val();
    let duedate  = $("#detailduedate-" + dno).val();
    let memo     = $("#detailmemo-" + dno).val();
    let location = $("#detaillocation-" + dno).val();
    let data = {
      dno: dno, title: title,
      duedate: duedate, memo: memo, location: location
    };
    let result = ajaxFunc("/todolist/updateDetail", data, "text");
    let nowUpdateTime = new Date().toLocaleString();
    $("#nowUpdateTime").html(nowUpdateTime);
    $("#updateTimeView").show();

    doList();
    countTodo();
    refreshCalStatus();
  });

  // -------- 디테일 삭제 버튼 --------
  $("body").on("click", ".detailDelBtn", function() {
    let dno      = $(this).data("dno");
    let data = {
      dno: dno
    };
    let result = ajaxFunc("/todolist/deleteDetail", data, "text");
    let nowUpdateTime = new Date().toLocaleString();
    $("#nowUpdateTime").html(nowUpdateTime);
    $("#updateTimeView").show();

    // 삭제되면 비우기
    $("#todoDetail").html("");
    doList();
    countTodo();
    refreshCalStatus();

  });
});



// ====================================
// 함수: 페이지로드, 캘린더, 데이트피커
// ====================================

// -------- |initialList()|
// session 저장 후 페이지 로딩 함수
function initialList() {
  sessionStorage.setItem("sortDirection", "ASC");
  sessionStorage.setItem("sortBy", "duedate");
  sessionStorage.setItem("status", "allList");
  sessionStorage.setItem("pageNo", 1);
  doList();
}

// -------- |countTodo()|
// 왼쪽 사이드바에서 할일 개수 함수
function countTodo() {
  let today = new Date().toISOString().substring(0, 10);
  let data = {
    today : today
  }

  // 별도의 데이터가 필요 없다면 두 번째 인자에 빈 객체 전달
  axios.post('/todo/todoCnt', {})
    .then(function(response) {
      // response.data에 서버에서 반환한 JSON 객체가 담겨있음
      const todoCount = response.data;
      console.log(todoCount); // { todayCnt: 5, allCnt: 20, unfinishedCnt: 7, ... }

      // 각 값 사용 예시
      $("#todayCnt").text(todoCount.todayListCnt);
      $("#allCnt").text(todoCount.allListCnt);
      $("#unfinishedCnt").text(todoCount.unfinishedListCnt);
      $("#starCnt").text(todoCount.importantListCnt);
      $("#isDuedateCnt").text(todoCount.hasDuedateListCnt);
      $("#isNotDuedateCnt").text(todoCount.noDuedateListCnt);

    })
    .catch(function(error) {
      // 오류 처리
      console.error('에러 발생:', error);
    });

  // let result = ajaxFunc("/todo/todoCnt", null, "text");

  /*let info = jQuery('<div>').html(result);

  $("#todayCnt").html(info.find("#spanTodayCnt").html());
  $("#allCnt").html(info.find("#spanAllCnt").html());
  $("#unfinishedCnt").html(info.find("#spanUnfinishedCnt").html());
  $("#starCnt").html(info.find("#spanStarCnt").html());
  $("#isDuedateCnt").html(info.find("#spanIsDuedateCnt").html());
  $("#isNotDuedateCnt").html(info.find("#spanIsNotDuedateCnt").html());*/
}

// -------- |listDuedate()|
// 마감일이 설정된 할일의 마감일을 불러와 배열로 반환 함수 (캘린더에 사용)
async function listDuedate() {
  const json = await callAjaxPromise('/todo/getListForCal', 'POST', null, 'json');
  return json;
}

function callAjaxPromise(url, method, data, dataType) {
  return new Promise((resolve, reject) => {
    $.ajax({
      url: url,
      type: method,
      data: data,
      dataType: dataType,
      success: function(response) {
        resolve(response);
      },
      error: function(xhr, status, error) {
        reject(error);
      }
    });
  });
}



// ===============================
// 함수: 왼쪽 사이드바
// ===============================

// -------- |selectWhere()|
// 왼쪽에서 추출할 메뉴 클릭시 세션에 상태를 저장하는 함수
function selectWhere(status) {
  if (status == "onlyTodayList") {
    sessionStorage.setItem("status", "onlyTodayList");
  } else if (status == "allList") {
    sessionStorage.setItem("status", "allList");
  } else if (status == "unfinishedList") {
    sessionStorage.setItem("status", "unfinishedList");
  } else if (status == "starList") {
    sessionStorage.setItem("status", "starList");
  } else if (status == "isNotNullDuedateList") {
    sessionStorage.setItem("status", "isNotNullDuedateList");
  } else if (status == "isNullDuedateList") {
    sessionStorage.setItem("status", "isNullDuedateList");
  } else if (status == "searchList") {
    sessionStorage.setItem("status", "searchList");
  } else if (status == "calendarList") {
    sessionStorage.setItem("status", "calendarList");
  }
  doList();
}



// ===============================
// 함수: 검색창
// ===============================

// -------- |doSearch()|
// 검색 동작시 작동하는 함수
function doSearch(searchTypes, searchWord) {
  let orderby = sessionStorage.getItem("orderby");
  let ordermethod = sessionStorage.getItem("ordermethod");

  let data = {
    searchTypes: searchTypes,
    searchWord: searchWord,
    orderby: orderby,
    ordermethod: ordermethod
  };
  let result = ajaxFunc("/todolist/selectMulti", data, null);
  let html = jQuery('<div>').html(result);
  let contents = html.find("div#ajaxList").html();
  $("#todolist").html(contents);
}



// ===============================
// 함수: 정렬, 리마인더
// ===============================




// ===============================
// 함수: 할일추가
// ===============================

// 할일추가 함수
/*function register() {
  let title = $(".regTitleInput").val();
  let duedate = $("#regDateInput").val();
  let star = null;
  let isStar = $(".regStarInput").hasClass("fa-solid");
  if ($(".regStarInput").hasClass("fa-solid")) {
    star = 1;
  } else {
    star = 0;
  }
  let location = $("#addLocationInput").val();
  let memo = $("#addMemoInput").val();

  let data = JSON.stringify({
    title: title,
    duedate: duedate,
    star:star,
    location:location,
    memo:memo
  });
  let result = ajaxFunc2('/todolist/insertTodo', 'application/json', 'text', data);
  if (result == "success") {
    $(".regTitleInput").val("");
    doList();
    countTodo();
  }
}*/

function addTodo() {
  let title = $("#todo-title-input").val();
  let duedate = $("#regDateInput").val();
  let important = $("#regStarInput").hasClass("fa-solid") ? true : false;
  let location = $("#addLocationInput").val();
  let content = $("#addMemoInput").val();
  let loginId = $("#login-id").val();
  let data = JSON.stringify({
    title: title,
    duedate: duedate,
    important: important,
    location: location,
    content: content,
    writer: loginId
  });
  callAjax('/todo/addTodo', 'POST', data, 'text', function(text) {
    if (text == "success") {
      $("#todo-title-input").val(""); // 추가되었으니까, input 비워둠
      $("#addLocationInput").val("");
      $("#addMemoInput").val("");
      doList();
      countTodo();
    }
  });
}



// ===============================
// 함수: 테이블
// ===============================

// -------- |titleModify()|
// 테이블에서 제목영역 클릭할 때 수정 함수
function titleModify(dno, modValue) {
  let title = modValue;

  let data = { "dno": dno, "title": title };
  let result = ajaxFunc("/todolist/updateTitle", data, "text");
  if (result == "success") {
    doList();
  }
}

// -------- |duedateModify()|
// 테이블에서 날짜영역 클릭할 때 수정 함수
function duedateModify(dno, modValue) {
  let duedate = modValue;

  let data = { "dno": dno, "duedate": duedate };
  let result = ajaxFunc("/todolist/updateDuedate", data, "text");
  if (result == "success") {
    doList();
  }
}

// -------- |updateSelectedAll()|
// 선택수정 버튼 관련 함수
function updateSelectedAll(selectedArr, finished, star, duedate) {
  let data = JSON.stringify({
    selectedArr: selectedArr,
    finished: finished,
    star: star,
    duedate: duedate
  });

  let result = ajaxFunc2('/todolist/updateSelectedAll', 'application/json', 'text', data);
  if (result == "success") {
    doList();
    countTodo();
  } else {
    alert("적어도 하나는 선택해야 수정이 된단다.");
  }
}

// -------- |deleteSelectedAll()|
// 선택삭제 버튼 관련 함수
function deleteSelectedAll(selectedArr) {
  let data = JSON.stringify({
    selectedArr: selectedArr,
  });
  let result = ajaxFunc2('/todolist/deleteSelectedAll', 'application/json', 'text', data);
  if (result == "success") {
    doList();
    countTodo();
  } else {
    alert("삭제할 항목이 없습니다. 선택해주세요.");
  }
}

// -------- |doList()|
// 테이블을 보여주는 함수
function doList() {
  let loginId = $("#login-id").val();
  let sortBy = sessionStorage.getItem("sortBy");
  let sortDirection = sessionStorage.getItem("sortDirection");
  let status = sessionStorage.getItem("status");
  let pageNo = sessionStorage.getItem("pageNo");

  /!* status가 calPickDuedateList일 때만 duedate값을 전달해야함 (그 외에는 null값) *!/
  let duedate = null;
  if (status == "calPickDuedateList") {
    duedate = sessionStorage.getItem("calPickDate");
  }

  let data = JSON.stringify({
    sortBy: sortBy,
    sortDirection: sortDirection,
    status: status,
    duedate: duedate,
    writer: loginId,
    pageNo: pageNo
  });

  callAjax('todo/getTodos', 'POST', data, 'html', function(html) {
    $('#todolist').html(html);
  });

}

function callAjax(url, type, data, dataType, callback) {
  $.ajax({
    url: url,
    type: type,
    contentType: 'application/json',
    data: data,
    dataType: dataType, // 서버에서 HTML fragment를 응답받을 때
    success: function(response) {
    callback(response); // 결과값을 콜백에 전달
  },
  error: function(xhr, status, error) {
    // 필요시 에러 콜백도 추가 가능
    alert('목록을 불러오지 못했습니다.');
  }
});
}

function ajaxFunc(url, data, dataType=null) {
  let result = "";
  $.ajax({
    url : url,
    type : "POST",
    data : data,
    dataType : dataType,
    async : false,
    success : function(data) {
      result = data;
    },
    error : function() {
    },
    complete : function() {
    },
  });
  return result;
}


function ajaxFunc2(url, contentType, dataType, data) {
  let result = "";
  $.ajax({
    url : url,
    type : "POST",
    contentType: contentType, // 클라이언트가 서버로 보내는 데이터타입
    dataType : dataType, // 서버로부터 받을 응답 데이터의 타입
    data : data,
    async : false,
    success : function(data) {
      result = data;
    },
    error : function() {
    },
    complete : function() {
    },
  });
  return result;
}



function outputMsg(errorMsg, tagObj, color) {
  // tagObj 요소의, 그 다음 요소에 출력
  let errTag = $(tagObj);
  $(errTag).html(errorMsg);
  $(errTag).css("color", color);
  $(tagObj).css("border-color", color);
}

function validCheckPwd1 (pwd1, pwd2) {
  // 비밀번호 8~20자
  if (pwd1.length < 8 || pwd1.length > 20) {
    outputMsg("비밀번호는 8~20자로 입력하세요!", $("#newPwd1MsgArea"), "red");
    return "fail";
  } else {
    if(pwd2 == "") {
      outputMsg("아래에 다시 한번 입력해주세요.", $("#newPwd1MsgArea"), "orange");
      return "success";
    } else {
      if(pwd1 != pwd2) {
        outputMsg("비밀번호가 일치하지 않습니다.", $("#newPwd1MsgArea"), "red");
        outputMsg("", $("#newPwd2MsgArea"), "red");
        return "fail";
      } else {
        outputMsg("비밀번호 검증완료", $("#newPwd1MsgArea"), "green");
        outputMsg("비밀번호가 일치합니다.", $("#newPwd2MsgArea"), "green");
        return "success";
      }
    }
  }
}

function validCheckPwd2 (pwd1, pwd2) {
  if (pwd1.length < 8 || pwd1.length > 20) {
    outputMsg("비밀번호는 8~20자로 입력하세요!", $("#newPwd1MsgArea"), "red");
    return "fail";
  }
  if(pwd1 != pwd2) { //
    outputMsg("비밀번호가 일치하지 않습니다.", $("#newPwd1MsgArea"), "red");
    outputMsg("", $("#newPwd2MsgArea"), "red");
    return "fail";
  } else {
    outputMsg("비밀번호 검증완료", $("#newPwd1MsgArea"), "green");
    outputMsg("비밀번호가 일치합니다.", $("#newPwd2MsgArea"), "green");
    return "success";
  }
}

function validCheckUsername (username, id) {
  if(username.length > 0) { //
    outputMsg("사용가능", $("#"+id), "green");
    return "success";
  } else {
    outputMsg("이름은 필수 입력입니다.", $("#"+id), "red");
    return "fail";

  }
}

function validCheckUseraddr(useraddr, id) {
  if(useraddr.length > 0) { //
    outputMsg("사용가능", $("#"+id), "green");
    return "success";
  } else {
    outputMsg("주소는 필수 입력입니다.", $("#"+id), "red");
    return "fail";
  }
}