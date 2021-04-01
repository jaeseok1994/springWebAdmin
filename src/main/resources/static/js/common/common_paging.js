/**
 * 페이지 네비게이션 생성 및 이동 처리
 * @param paramApp : Vue Object
 * @param totalData : 총 데이터 건수
 * @param searchFunc : 조회 함수
 * @returns
 */
function commonPaging(paramApp, totalData, searchFunc){
	
	console.log("paramApp.keys.pagerows : " + paramApp.keys.pagerows);
	console.log("paramApp.keys.pagecnt : " + paramApp.keys.pagecnt);
	console.log("paramApp.keys.pagenum : " + paramApp.keys.pagenum);
	console.log("totalData : " + totalData);
  
  var totalPage = Math.ceil(totalData/paramApp.keys.pagerows);    // 총 페이지 수
  var pageGroup = Math.ceil(paramApp.keys.pagenum/paramApp.keys.pagecnt);    // 페이지 그룹
  
  console.log("totalPage : " + totalPage);
  console.log("pageGroup : " + pageGroup);
  
  var last = pageGroup * paramApp.keys.pagecnt;    // 화면에 보여질 마지막 페이지 번호
  var first = last - (paramApp.keys.pagecnt-1);    // 화면에 보여질 첫번째 페이지 번호
  
  if(last > totalPage)
      last = totalPage;
  
  var next = last+1;
  var prev = first-1;
  
  console.log("last : " + last);
  console.log("first : " + first);
  console.log("next : " + next);
  console.log("prev : " + prev);

  var $pingingView = $("#commonPaging");
  
  var html = "";

  if(prev == 0) {
      html += "<a href=# id='first' class='disabled'>|<</a> ";
      html += "<a href=# id='prev' class='disabled'><</a> ";
  } else {
      html += "<a href=# id='first'>|<</a> ";
      html += "<a href=# id='prev'><</a> ";         
  }
  
  
  for(var i=first; i <= last; i++){
      html += "<a href='#' style='width: 50px' id=" + i + ">" + i + "</a> ";
  }
  
  if(last < totalPage) {
      html += "<a href=# id='next'>></a>";
      html += "<a href=# id='last'>>|</a>";
  } else {
      html += "<a href=# id='next' class='disabled'>></a>";
      html += "<a href=# id='last' class='disabled'>>|</a>";
  }
  
  $("#commonPaging").html(html);    // 페이지 목록 생성
  
  $("#commonPaging a").css({"color": "black",
                      "padding-left": "10px"});
                      
  $("#commonPaging a#" + paramApp.keys.pagenum).css({"text-decoration":"none", 
                                     "color":"red", 
                                     "font-weight":"bold"});    // 현재 페이지 표시
                                     
  $("#commonPaging a").click(function(){
      var $item = $(this);
      var $id = $item.attr("id");
      var selectedPage = $item.text();
      
      if($id == "first")   selectedPage = 1;
      if($id == "next")    selectedPage = next;
      if($id == "prev")    selectedPage = prev;
      if($id == "last")    selectedPage = totalPage;
      
      paramApp.keys.pagenum = Number(selectedPage);
      
      if(typeof searchFunc === 'function') searchFunc(); // 조회 함수 실행
  });
                                     
}