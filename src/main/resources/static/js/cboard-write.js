$(function() {
  var $fileInput = $('#fileElem');
  var $fileList = $('#file-list');

  // 드래그 오버 (클래스 추가)
  $(document).on('dragover', '#drop-area', function(e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).addClass('dragover');
  });

  // 드래그 아웃 (클래스 제거)
  $(document).on('dragleave drop', '#drop-area', function(e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).removeClass('dragover');
  });

  // 파일 드롭 (파일 표시)
  $(document).on('drop', '#drop-area', function(e) {
    e.preventDefault();
    var files = e.originalEvent.dataTransfer.files;
    showFiles(files);
    $fileInput[0].files = files;
  });

  // 파일 선택 시 (input이 직접 클릭됨)
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