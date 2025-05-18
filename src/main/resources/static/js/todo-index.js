
$(document).ready(function() {
  let picker; // 전역 변수로 선언
  // 페이지 로드시 리스트/카운트 초기화
  initialList();
  // showTodoLeft();

  // 오늘 날짜 표시
  let today = new Date().toLocaleDateString();
  $("#header-date").html(today);

  // 캘린더 생성 및 날짜 클릭 이벤트
  let myCalendar = jsCalendar.new("#calendar");
  myCalendar.onDateClick(function(event, date){
    var value = date.toLocaleDateString('sv-SE');
    document.getElementById("selected-date").value = value;
    sessionStorage.setItem("status", "calPickDuedateList");
    sessionStorage.setItem("calPickDate", value);
    doList();
  });
  updateCalHighlights();
  // 캘린더 리프레시
  function updateCalHighlights() {
    let highlightedDates = listDuedate();
    highlightSpecialDatesOnCal(highlightedDates);
    myCalendar.refresh();
  }

  // 캘린더: 특수 날짜 강조 함수
  function highlightSpecialDatesOnCal(highlightedDates) {
    myCalendar.onDateRender(function(date, element, info) {
      let yyyy = date.getFullYear();
      let mm = String(date.getMonth() + 1).padStart(2, '0');
      let dd = String(date.getDate()).padStart(2, '0');
      let value = yyyy + '-' + mm + '-' + dd;
      if(highlightedDates.includes(value)){ // highlightedDates 해당하는 경우, 강조
        element.style.fontWeight = 'bold';
        element.style.color = '#0f1d41';
        element.style.fontSize = '16px';
      }
    });
  }

  // 데이트피커(마감일 입력)
  picker = new easepick.create({
    element: "#todo-duedate-input",
    css: ["https://cdn.jsdelivr.net/npm/@easepick/bundle@1.2.1/dist/index.css"],
    zIndex: 10,
    setup(picker) {
      picker.on('select', (e) => {
        let selectedDate = picker.getDate().format('YYYY-MM-DD');
        $('#date-hint').html(`${selectedDate}에 할일이 추가됩니다`).show();
      });
    },
  });

  // sortDirection 버튼 클릭 이벤트
  $("#sort-btn").on("click", function() {
    let icon = $("#sort-icon");
    let isAsc = icon.hasClass("fa-arrow-up-wide-short");
    icon.toggleClass("fa-arrow-up-wide-short fa-arrow-down-wide-short");
    sessionStorage.setItem("sortDirection", isAsc ? "DESC" : "ASC");
    doList();
  });

  // sortBy 셀렉트박스 변경 이벤트
  $("#sort-select").on("change", function() {
    sessionStorage.setItem("sortBy", $(this).val());
    doList();
  });

  // 할일 입력창: 엔터로 추가
  $('#todo-title-input').on('keydown', function(e) {
    if (e.key == "Enter") {
      console.log("엔터 침")
      addTodo();
    }
  });

  // 마감일 아이콘 클릭시 데이트피커 포커스
  $("#duedate-btn").on("click", function() {
    console.log("focus");
    $("#todo-duedate-input").show();
  });

  // 중요도 아이콘 클릭시 클래스 변경
  $("#important-btn").on("click", function() {
    $(this).toggleClass("fa-solid fa-regular");
  });

  // 추가입력 클릭시 토글 작동
  $("#extra-btn").on("click", function() {
    $("#extra-inputs").toggle();
  });

  // 테이블 체크박스 전체선택/개별선택
  $(document).on('change','#select-all-checkbox', function() {
    $('.row-checkbox').prop('checked', this.checked);
  });

  $(document).on('change', '.row-checkbox', function() {
    if (!this.checked) {
      $('#select-all-checkbox').prop('checked', false);
    } else {
      var allChecked = $('.row-checkbox').length === $('.row-checkbox:checked').length;
      $('#select-all-checkbox').prop('checked', allChecked);
    }
  });

  // 완료 체크박스 클릭시 상태 변경
  $(document).on("click", ".finished-icon", function() {
    let dno = $(this).data("dno");
    let solid = $(this).hasClass("fa-circle-check");
    let finished = !solid;
    // let finished = checked ? 1 : 0;
    let data = JSON.stringify({
      dno: dno,
      finished: finished
    });
    callAjax('/todo/modifyTodo', 'POST', data, 'text', function(text) {
      if (text == "success") {
        $("#finished-icon-" + dno).toggleClass("fa-circle fa-circle-check");
        $("#title-cell-" + dno).toggleClass("completed", checked);
        doList();
        countTodo();
      }
    });
  });

  // 제목 셀 클릭시 인라인 수정
  $(document).on('click', '.title-cell', function() {
    let span  = $(this).children('.title-span');
    let input = span.siblings('.edit-title-input');
    input.val(span.text()).show().focus();
    span.hide();
  });
  // 제목 수정 엔터/포커스아웃
  $(document).on('keydown', '.edit-title-input', function(e) {
    if (e.key === "Enter") {
      let dno = $(this).data("dno");
      let value = $(this).val();
      let span = $(this).siblings('.title-span');
      span.text(value).show();
      $(this).hide();
      titleModify(dno, value);
      countTodo();
    }
  });
  $(document).on('blur', '.edit-title-input', function() {
    let dno = $(this).data("dno");
    let value = $(this).val();
    let span = $(this).siblings('.title-span');
    span.text(value).show();
    $(this).hide();
    titleModify(dno, value);
    countTodo();
  });

  // 마감일 셀 클릭시 인라인 수정
  $(document).on('click', '.duedate-cell', function() {
    let span  = $(this).children('.duedate-span');
    let input = span.siblings('.edit-duedate-input');
    input.val(span.text()).show().focus();
    span.hide();
  });
  // 마감일 수정 엔터/포커스아웃
  $(document).on('keydown', '.edit-duedate-input', function(e) {
    if (e.key === "Enter") {
      let dno = $(this).data("dno");
      let value = $(this).val();
      let span = $(this).siblings('.duedate-span');
      span.text(value).show();
      $(this).hide();
      duedateModify(dno, value);
      countTodo();
      refreshCalStatus();
    }
  });
  $(document).on('blur', '.edit-duedate-input', function() {
    let dno = $(this).data("dno");
    let value = $(this).val();
    let span = $(this).siblings('.duedate-span');
    span.text(value).show();
    $(this).hide();
    duedateModify(dno, value);
    countTodo();
    refreshCalStatus();
  });

  // 중요도 아이콘 클릭시 상태 변경
  $(document).on("click", ".important-icon", function() {
    let dno = $(this).data("dno");
    let solid = $(this).hasClass("fa-solid");
    let important = solid ? 0 : 1;
    let data = { "dno": dno, "important": important };
    let result = ajaxFunc("/todolist/updateimportant", data, "text");
    if (result == "success") {
      $("#important-icon-" + dno).toggleClass("fa-solid");
      doList();
      countTodo();
    }
  });

  // 상세 보기 버튼 클릭시
  $(document).on("click", ".more-btn", function() {
    let dno = $(this).data("dno");
    let data = {"dno": dno};
    let result = ajaxFunc("/todolist/selectone", data, null);
    let html = jQuery('<div>').html(result);
    let contents = html.find("div#ajaxTodoDetail").html();
    $("#todo-detail").html(contents);
  });

  // 삭제 버튼 클릭시
  $(document).on("click", ".delete-btn", function() {
    let dno = $(this).data("dno");
    let data = { "dno": dno };
    let result = ajaxFunc("/todolist/deleteTodo", data, "text");
    $("#todo-detail").html("");
    doList();
    countTodo();
  });

  // 선택수정 모달 열기/닫기
  $(document).on('click','#multi-edit-btn', function() {
    $('#modal-edit-overlay').fadeIn(150);
  });
  $(document).on('click','#modal-edit-cancel, #modal-edit-overlay', function(e) {
    if (e.target.id === 'modal-edit-overlay' || e.target.id === 'modal-edit-cancel') {
      $('#modal-edit-overlay').fadeOut(150);
    }
  });
  $(document).on('click','#modal-edit', function(e) {
    e.stopPropagation();
  });
  // 선택수정 폼 제출
  $(document).on('submit','#form-edit-multi', function(e) {
    e.preventDefault();
    let finished = $('input[name="finished"]:checked').val() ?? '';
    let important = $('input[name="important"]:checked').val() ?? '';
    let duedate = $('input[name="duedate"]').val() ?? '';
    let selectedArr = $('.row-checkbox:checked').map(function(){ return $(this).data('dno'); }).get();
    updateSelectedAll(selectedArr, finished, important, duedate);
    $('#modal-edit-overlay').fadeOut(150);
  });

  // 선택삭제 모달 열기/닫기
  $(document).on('click','#multi-delete-btn', function() {
    $('#modal-delete-overlay').fadeIn(150);
  });
  $(document).on('click','#modal-delete-cancel, #modal-delete-overlay', function(e) {
    if (e.target.id === 'modal-delete-overlay' || e.target.id === 'modal-delete-cancel') {
      $('#modal-delete-overlay').fadeOut(150);
    }
  });
  $(document).on('click','#modal-delete', function(e) {
    e.stopPropagation();
  });
  // 선택삭제 폼 제출
  $(document).on('submit','#form-delete-multi', function(e) {
    e.preventDefault();
    let selectedArr = $('.row-checkbox:checked').map(function(){ return $(this).data('dno'); }).get();
    deleteSelectedAll(selectedArr);
    $('#modal-delete-overlay').fadeOut(150);
  });

  // 상세 패널 내 수정/삭제 버튼
  $(document).on("click", ".detail-edit-btn", function() {
    let dno      = $(this).data("dno");
    let title    = $("#detail-title-" + dno).val();
    let duedate  = $("#detail-duedate-" + dno).val();
    let content     = $("#detail-content-" + dno).val();
    let location = $("#detail-location-" + dno).val();
    let data = {
      dno: dno, title: title,
      duedate: duedate, content: content, location: location
    };
    let result = ajaxFunc("/todolist/updateDetail", data, "text");
    let nowUpdateTime = new Date().toLocaleString();
    $("#now-update-time").html(nowUpdateTime);
    $("#update-time-view").show();
    doList();
    countTodo();
    refreshCalStatus();
  });
  $(document).on("click", ".detail-delete-btn", function() {
    let dno      = $(this).data("dno");
    let data = { dno: dno };
    let result = ajaxFunc("/todolist/deleteDetail", data, "text");
    let nowUpdateTime = new Date().toLocaleString();
    $("#now-update-time").html(nowUpdateTime);
    $("#update-time-view").show();
    $("#todo-detail").html("");
    doList();
    countTodo();
    refreshCalStatus();
  });
});

// ---------------------- 주요 함수 정의 ----------------------
// 리스트/정렬 초기화
function initialList() {
  sessionStorage.setItem("sortBy", "duedate"); // 정렬기준 마감일순
  sessionStorage.setItem("sortDirection", "ASC"); // 정렬방식 오름차순
  sessionStorage.setItem("status", "allList"); // 모든 리스트 가져오기
  $("#header-title").html(sessionStorage.getItem("status"));
  doList();
}

function doList() {
  let loginId = $("#login-id").val();
  let sortBy = sessionStorage.getItem("sortBy");
  let sortDirection = sessionStorage.getItem("sortDirection");
  let status = sessionStorage.getItem("status");

  /* status가 calPickDuedateList일 때만 duedate값을 전달해야함 (그 외에는 null값) */
  let duedate = null;
  if (status == "calPickDuedateList") {
    duedate = sessionStorage.getItem("calPickDate");
  }

  let data = JSON.stringify({
    sortBy: sortBy,
    sortDirection: sortDirection,
    status: status,
    duedate: duedate,
    writer: loginId
  });

  callAjax('todo/getTodos', 'POST', data, 'html', function(html) {
    $('#todo-list-area').html(html);
  });
}


// 마감일 있는 날짜 리스트
function listDuedate() {
  let result = ajaxFunc("/todolist/listDuedate", null, "text");
  let info = jQuery('<div>').html(result);
  spans = info.find(".duedateByTodo");
  let tempList = new Array();
  spans.each(function() {
    tempList.push($(this).html());
  });
  return tempList;
}
// 메뉴 클릭시 상태 변경
function selectWhere(status) {
  sessionStorage.setItem("status", status);
  $("#header-title").html(sessionStorage.getItem("status"));
  doList();
}
// 검색
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
  $("#todo-list").html(contents);
}
// 할일 등록
function addTodo() {
  let title = $("#todo-title-input").val();
  let duedate = $("#todo-duedate-input").val();
  let important = $("#important-btn").hasClass("fa-solid") ? true : false;
  let location = $("#todo-location-input").val();
  let content = $("#todo-content-input").val();
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
      doList();
      // refreshCountTodo();
    }
  });


}
// 제목 수정
function titleModify(dno, modValue) {
  let title = modValue;
  let data = { "dno": dno, "title": title };
  let result = ajaxFunc("/todolist/updateTitle", data, "text");
  if (result == "success") {
    doList();
  }
}
// 마감일 수정
function duedateModify(dno, modValue) {
  let duedate = modValue;
  let data = { "dno": dno, "duedate": duedate };
  let result = ajaxFunc("/todolist/updateDuedate", data, "text");
  if (result == "success") {
    doList();
  }
}
// 선택 수정
function updateSelectedAll(selectedArr, finished, important, duedate) {
  let data = JSON.stringify({
    selectedArr: selectedArr,
    finished: finished,
    important: important,
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
// 선택 삭제
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



function callAjax(url, type, data, dataType, callback) {
  $.ajax({
    url: url,
    type: type,
    contentType: 'application/json', /*'application/x-www-form-urlencoded'*/
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