// 파일 정보 저장 배열
let filesArr = [];

function handleFormErrors(errors) {
  // 기존 에러 메시지 초기화
  document.querySelectorAll('.error-message').forEach(el => el.textContent = '');

  // 서버에서 반환한 에러 매핑
  Object.entries(errors).forEach(([field, message]) => {
    const errorElement = document.getElementById(`${field}Error`);
    if (errorElement) {
      errorElement.textContent = message;
    }
  });
}

function handleNetworkError(error) {
  // 서버에서 응답을 받았으나 에러인 경우
  if (error.response) {
    alert('서버에서 오류 응답이 왔습니다.\n' +
      '상태코드: ' + error.response.status + '\n' +
      '메시지: ' + (error.response.data?.message || ''));
  } else if (error.request) {
    // 요청은 보냈으나 응답이 없음
    alert('서버로부터 응답이 없습니다. 네트워크 상태를 확인해 주세요.');
  } else {
    // 요청 자체가 실패
    alert('요청을 보내는 도중 오류가 발생했습니다: ' + error.message);
  }
}

// 서버로 전송하는 함수
function submitFormWithFilesAxios(formElem, filesArr) {

  // 1. FormData 객체 생성 (폼 요소 기반)
  const formData = new FormData(formElem);

  // 2. filesArr에 담긴 파일들 FormData에 추가
  filesArr.forEach(obj => {
    formData.append('uploadfiles', obj.file); // 여러 파일이면 'files'에 obj.file이 쌓임(append)
  });

  // 3. Axios로 서버에 전송
  axios.post($(formElem).attr('action'), formData, {
    headers: {
      'Content-Type': 'multipart/form-data' // 파일 업로드에 필요한 HTTP 헤더
    }
  })
    .then(response => {
      if (response.data.success) {
        console.log(response.data);
        location.href = response.data.data.redirectUrl;
      } else {
        handleFormErrors(response.data.data.errors);
      }
    })
    .catch(error => {
      handleNetworkError(error);
    });
}

// 파일 아이콘 생성 함수
function getFileIcon(obj) {
  // 이미지 파일이면 미리보기 썸네일 반환
  if (obj.file.type && obj.file.type.startsWith('image/')) { // file.type이 있고, 'image/'로 시작하면(이미지 파일이면)
    return `<img src="${obj.preview}" class="file-icon" style="object-fit:cover;border:1px solid #eee;">`;
    // file-icon : CSS에서 정의한 파일 아이콘 공통 스타일(너비/높이 18px, 수직중앙정렬, 오른쪽 여백)
    // object-fit : cover로 이미지가 잘리지 않고 꽉 차게 함
  }
  // PDF 파일이면 PDF 전용 아이콘 반환(file.type이 pdf이거나, 파일명이 .pdf로 끝나면)
  if (obj.file.type === 'application/pdf' || (obj.file.name && obj.file.name.match(/\.pdf$/i))) {
    return `<img src="https://cdn-icons-png.flaticon.com/512/337/337946.png" class="file-icon">`;
  }
  // 그 외의 파일이면 일반 파일 아이콘 반환
  return `<img src="https://cdn-icons-png.flaticon.com/512/2991/2991108.png" class="file-icon">`;
}

// 파일 목록 렌더링 함수 (파일 목록 테이블을 화면에 그려줌)
function renderFileTable() {
  const $fileList = $('#file-list'); // 파일 목록이 들어갈 DOM 요소를 jQuery 객체로 저장
  const $dragInfo = $('#drag-info'); // 안내 문구 DOM 요소를 jQuery 객체로 저장

  if (filesArr.length === 0) { // 첨부된 파일이 없으면
    $fileList.html(''); // 파일 목록 영역을 비움
    $dragInfo.show(); // 안내 문구를 다시 보여줌
    $('#file-delete-all, #file-upload-count').addClass('d-none'); // 모두삭제/업로드완료 span 숨김
    return; // 함수 종료
  }

  $dragInfo.hide(); // 파일이 있으면 안내 문구 숨김
  let html = `<table class="file-table"><tbody>`; // 표의 시작 태그 생성

  filesArr.forEach((obj, idx) => { // 첨부 파일 배열을 순회하면서
    html += `
    <tr>
      <td style="width:30px;">${getFileIcon(obj)}</td>
      <td style="word-break:break-all;">${obj.file.name}</td>
      <!-- word-break:break-all; : 긴 파일명도 줄바꿈되어 셀을 넘치지 않게 함 -->

      <td style="width:80px; text-align:right;">
        ${obj.file.size >= 1024*1024
          ? (obj.file.size/1024/1024).toFixed(2)+'MB'
          : (obj.file.size/1024).toFixed(1)+'KB'}
      </td>

      <td style="width:50px;">
        <button class="file-remove-btn" data-idx="${idx}">X</button>
      </td>
    </tr>
  `;
  });

  html += `</tbody></table>`; // 표의 끝 태그 추가
  $fileList.html(html); // 파일 목록 영역에 표 HTML 삽입
  $('#file-delete-all, #file-upload-count').removeClass('d-none'); // 모두삭제/업로드완료 span 표시
  $('#file-upload-count').text(`✔ ${filesArr.length}개 업로드 완료`); // 업로드 개수 갱신
}

// 파일 처리 함수 (이미지면 base64 형식의 preview 등록 후, 이미지가 아니면 preview에 빈문자 등록하여 렌더링함수 실행)
function handleFiles(files) { // 파일 목록을 받아 처리하는 함수
  Array.from(files).forEach(file => { // files를 배열로 변환 후 각 파일에 대해 반복
    // file.type이 truthy(즉, 값이 있고, undefined/null/빈문자열이 아님)인지 확인
    if (file.type && file.type.startsWith('image/')) { // truthy이고, 이미지이면

      // FileReader 객체 생성
      const reader = new FileReader(); // FileReader 객체 생성

      // 이벤트 핸들러 미리 등록
      reader.onload = e => { // 파일 읽기 완료 시 실행될 함수 등록(매개변수로 이벤트 객체 전달)
        // file의 모든 속성 + preview 속성이 포함된 새로운 객체를 만드는 문법
        filesArr.push({file:file, preview: e.target.result}); // 파일 정보와 미리보기(이미지 base64 데이터) 추가
        // e.target.result : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
        renderFileTable(); // 파일 목록 테이블 다시 렌더링
      };

      // 이미지를 base64 데이터로 읽기 시작, 읽기 완료후 등록한 onload 함수 자동 실행됨
      reader.readAsDataURL(file);

    } else { // 이미지가 아닌 파일이면
      filesArr.push({file:file, preview: ''}); // 미리보기 없이 파일 정보만 추가
      renderFileTable(); // 파일 목록 테이블 다시 렌더링
    }
  });
}

// ----------------- 이벤트 핸들러만 $(function(){}) 내부에 -----------------
$(document).ready(function() {

  //
  $('#cboard-write-form').on('submit', function(e) {
    e.preventDefault(); // 기존 폼 제출 방지
    submitFormWithFilesAxios(this, filesArr);
  });

  // 파일 첨부 버튼 클릭 이벤트 (많이 쓰이는 패턴)
  $('#file-upload-btn').on('click', function() {
    console.log("파일 첨부 버튼 클릭");
    // 요소를 동적으로 생성
    $('<input type="file" multiple>')
      .hide() // 숨김
      .on('change', function(e) { // 파일 선택시 이벤트 실행
        handleFiles(e.target.files); // 파일들에 handleFiles 함수로 전달
        $(this).remove(); // 사용이 끝난 input 요소는 제거
      })
      .appendTo('body') // 동적으로 생성한 input을 body에 추가 (브라우저가 인식할 수 있도록 함)
      .trigger('click'); // input을 강제로 클릭하여 파일 선택창을 띄움
  });

  // 모두 삭제 버튼 클릭 이벤트
  $('#file-delete-all').on('click', function() {
    filesArr = []; // 리스트 비움
    renderFileTable();
  });

  // 파일 삭제 버튼(동적) 클릭 이벤트
  $('#file-list').on('click', '.file-remove-btn', function() {
    filesArr.splice($(this).data('idx'), 1); // 클릭된 삭제 버튼의 data-idx을 가져와서 해당 파일을 삭제
    renderFileTable();
  });

  // 드래그 앤 드롭 관련 이벤트
  $('#drop-area')
    .on('dragover', function(e) { // 사용자가 파일을 드래그하여 drop-area 위에 올릴 때
      e.preventDefault(); // 기본 동작(브라우저의 파일 열기)을 막음
      e.stopPropagation(); // 이벤트가 부모로 전파되는 것을 막음
      $(this).addClass('drag-over'); // 4. CSS 효과를 주기 위해 drag-over 클래스를 추가
    })
    .on('dragleave drop', function(e) { // 드래그가 영역을 벗어나거나 파일을 놓았을 때
      e.preventDefault(); // 기본 동작을 막음
      e.stopPropagation(); // 이벤트 전파를 막음
      $(this).removeClass('drag-over'); // drag-over 클래스를 제거해 CSS 효과 해제
    })
    .on('drop', function(e) { // 파일을 drop-area에 놓았을 때
      handleFiles(e.originalEvent.dataTransfer.files); // 드롭된 파일 목록을 handleFiles 함수로 전달해 처리
    });

});