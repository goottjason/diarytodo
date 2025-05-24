$(function() {
  var $dropArea = $('#drop-area');
  var $fileInput = $('#fileElem');
  var $fileList = $('#file-list');

  // 드래그 오버
  $dropArea.on('dragover', function(e) {
    e.preventDefault();
    e.stopPropagation();
    $dropArea.addClass('dragover');
  });

  // 드래그 아웃
  $dropArea.on('dragleave', function(e) {
    e.preventDefault();
    e.stopPropagation();
    $dropArea.removeClass('dragover');
  });

  // 파일 드롭
  $dropArea.on('drop', function(e) {
    e.preventDefault();
    e.stopPropagation();
    $dropArea.removeClass('dragover');
    var files = e.originalEvent.dataTransfer.files;
    showFiles(files);
    // input에 동기화 (실제 업로드 시 필요)
    $fileInput[0].files = files;
  });

  // 드롭영역 클릭 시 파일 선택창 열기 (버블링 차단)
  $dropArea.on('click', function(e) {
    if (e.target === this) { // drop-area 자체가 클릭된 경우만
      e.stopPropagation();
      $fileInput.click();
    }
  });

  // 파일 선택 시
  $fileInput.on('change', function() {
    showFiles(this.files);
  });

  // 파일명 표시 함수
  function showFiles(files) {
    var html = '';
    for (var i = 0; i < files.length; i++) {
      html += '<div>' + files[i].name + ' <span class="text-muted">(' + Math.round(files[i].size/1024) + ' KB)</span></div>';
    }
    $fileList.html(html);
  }
});