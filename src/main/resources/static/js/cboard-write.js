// 파일 정보 저장 배열
let filesArr = [];

// 파일 아이콘 생성 함수
function getFileIcon(file) {
  // 이미지 파일이면 미리보기 썸네일
  if (file.type && file.type.startsWith('image/')) {
    return `<img src="${file.preview}" class="file-icon" style="object-fit:cover;border:1px solid #eee;">`;
  }
  // PDF 파일이면 PDF 아이콘
  if (file.type === 'application/pdf' || (file.name && file.name.match(/\.pdf$/i))) {
    return `<img src="https://cdn-icons-png.flaticon.com/512/337/337946.png" class="file-icon" alt="pdf">`;
  }
  // 기타 파일이면 일반 파일 아이콘
  return `<img src="https://cdn-icons-png.flaticon.com/512/2991/2991108.png" class="file-icon" alt="file">`;
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

  filesArr.forEach((file, idx) => { // 첨부 파일 배열을 순회하면서
    html += `
      <tr>
        <td style="width:30px;">${getFileIcon(file)}</td> // 파일 아이콘 또는 미리보기 표시
        <td style="word-break:break-all;">${file.name}</td> // 파일명 표시(길면 줄바꿈)
        <td style="width:80px; text-align:right;">
          ${file.size >= 1024*1024 // 파일 크기가 1MB 이상이면 MB 단위, 아니면 KB 단위로 표시
      ? (file.size/1024/1024).toFixed(2)+'MB'
      : (file.size/1024).toFixed(1)+'KB'}
        </td>
        <td style="width:50px;">
          <button class="file-remove-btn" data-idx="${idx}">X</button> // 삭제 버튼(파일 인덱스 저장)
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
        filesArr.push({...file, preview: e.target.result}); // 파일 정보와 미리보기(이미지 base64 데이터) 추가
        // e.target.result : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
        renderFileTable(); // 파일 목록 테이블 다시 렌더링
      };
      // 이미지를 base64 데이터로 읽기 시작, 읽기 완료후 등록한 onload 함수 자동 실행됨
      reader.readAsDataURL(file);

    } else { // 이미지가 아닌 파일이면
      filesArr.push({...file, preview: ''}); // 미리보기 없이 파일 정보만 추가
      renderFileTable(); // 파일 목록 테이블 다시 렌더링
    }
  });
}

// ----------------- 이벤트 핸들러만 $(function(){}) 내부에 -----------------
$(document).ready(function() {

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