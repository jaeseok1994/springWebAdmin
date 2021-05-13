/**
 * 페이지 네비게이션 생성 및 이동 처리
 * @param grid : Vue Object
 * @param totalData : 총 데이터 건수
 * @param searchFunc : 조회 함수
 * @returns
 */
function commonPaging(grid, totalData, searchFunc){
	
	//console.log("grid.keys.pagerows : " + grid.keys.pagerows);
	//console.log("grid.keys.pagecnt : " + grid.keys.pagecnt);
	//console.log("grid.keys.pagenum : " + grid.keys.pagenum);

	//console.log("totalData : " + totalData);
  var pageDiv = grid.pageDiv||"#commonPaging";
  var pagerows = grid.pagerows||100;
  var pagecnt = grid.pagecnt||10;
  
  var totalPage = Math.ceil(totalData/pagerows);    // 총 페이지 수
  var pageGroup = Math.ceil(grid.pagenum/pagecnt);    // 페이지 그룹
  
  //console.log("totalPage : " + totalPage);
  //console.log("pageGroup : " + pageGroup);
  
  var last = pageGroup * pagecnt;    // 화면에 보여질 마지막 페이지 번호
  var first = last - (pagecnt-1);    // 화면에 보여질 첫번째 페이지 번호
  
  if(last > totalPage)
      last = totalPage;
  
  var next = last+1;
  var prev = first-1;
  
  //console.log("last : " + last);
  //console.log("first : " + first);
  //console.log("next : " + next);
  //console.log("prev : " + prev);

  
  var html = "";

  if(prev == 0) {
      html += "<a href=# id='first' class='disabled'>|<</a> ";
      html += "<a href=# id='prev' class='disabled'><</a> ";
  } else {
      html += "<a href=# id='first' style='letter-spacing: -6px'>◀◀</a> ";
      html += "<a href=# id='prev' >◀</a> ";
  }
  
  
  for(var i=first; i <= last; i++){
      html += "<a href='#' style='width: 50px' id=" + i + ">" + i + "</a> ";
  }
  
  if(last < totalPage) {
      html += "<a href=# id='next'>▶</a>";
      html += "<a href=# id='last' style='letter-spacing: -6px'>▶▶</a>";
  } else {
      html += "<a href=# id='next' class='disabled'>></a>";
      html += "<a href=# id='last' class='disabled'>>|</a>";
  }

  if(totalData > 0 ){
      html += "<a style='padding-left:20px;font-size:0.9em;'>전체 : "+ totalData + " 건</a>";
  }
  $(pageDiv).html(html);    // 페이지 목록 생성
  
  $(pageDiv + " a").css({"color": "black",
                      "padding-left": "10px"});
                      
  $(pageDiv + " a#" + grid.pagenum).css({"text-decoration":"underline",
                                     "color":"#4b79bd",
                                     "font-weight":"bold"});    // 현재 페이지 표시
                                     
  $(pageDiv + " a").click(function(){
      var $item = $(this);
      var $id = $item.attr("id");
      var selectedPage = $item.text();
      
      if($id == "first")   selectedPage = 1;
      if($id == "next")    selectedPage = next;
      if($id == "prev")    selectedPage = prev;
      if($id == "last")    selectedPage = totalPage;
      
      grid.pagenum = Number(selectedPage);
      app.keys.pagenum = grid.pagenum;
      app.keys.pagerows = grid.pagerows;

      if(typeof searchFunc === 'function') searchFunc(); // 조회 함수 실행
  });
                                     
}