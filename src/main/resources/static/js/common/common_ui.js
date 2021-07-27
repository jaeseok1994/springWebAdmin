//화면 리사이즈시 변경되어야 할 object ids
var resizeHeightObjs = [];
var heightBottom = 0;

$(window).resize(function () {
    resizeHeight();
});

function setResizeHeightObjs () {
    for(var i = 0;i<arguments.length;i++){
        if(!resizeHeightObjs[arguments[i]])
            resizeHeightObjs.push(arguments[i]);
    }
    resizeHeight();
}
function resizeHeight () {
    for(var i=0;i<resizeHeightObjs.length;i++){
        try{
            var tabHeight = window.innerHeight - $("#"+resizeHeightObjs[i]).offset().top; //
            var btm = heightBottom;
            if(resizeHeightObjs.length > 1 && resizeHeightObjs[i] == "tabList") btm = 0;   //전체화면은 밑부분은 제외
            if($("#"+resizeHeightObjs[i]).offset().top == 0) continue;
            $("#"+resizeHeightObjs[i]).height(tabHeight -5 - 30 - btm); //message bar 제외
        }catch(e){
            console.log("Div 이름 확인 : "+resizeHeightObjs[i]);
        }
    }
}


//modal, vue alert, confirm ,select2 //vue 라이버러리 필요
var app_alert ;
var app_confirm;
var app_innerpopup;
var app_message;

$(document.body).ready(function () {
    try{
        if(!Vue) return;
    }catch(exception){
        return;
    }
    Vue.component('select2', {
        props: ['options', 'value' ,'url','para','min'],
        template: '<select>\
                     <slot></slot>\
                   </select>',
        mounted: function () {
            var _url = this.url;
            //debugger;
            if(_url.indexOf('/') == -1){
                _url = "/commonCode/selectList.do/" + _url;
            }
            var vm = this;
            var _para = this.para;
            var _min = this.min;
            if(_min == undefined) _min = '1';
            console.log("min:",this.min);
            console.log("_min:",_min);
            if(this.options){
                $(this.$el).select2({ data: this.options, closeOnSelect: true, allowClear: true  })
                           .val(this.value);
            }else{
                $(this.$el)
                    // init select2
                    //.select2({ data: this.options, closeOnSelect: true, allowClear: true  })
                    .select2({
                            placeholder: '선택',
                            //templateResult: formatState,
                            closeOnSelect: true,
                            allowClear: true,
                            minimumInputLength: _min,
                            ajax: {
                                url:_url,
                                type:"POST",
                                dataType: 'json',
                                contentType : "application/json; charset=UTF-8",
                                delay: 250,
                                data: function (params) {
                                          //var para = {};
                                          _para.q = params.term;
                                          var param = {
                                              map: _para
                                          };
                                          return JSON.stringify(param);
                                      },
                                processResults: function (data, params) {
                                    params.page = params.page || 1;
                                    var results = data.resultList.map(function(obj){
                                        obj.id = obj.id||obj.value;
                                        obj.text = obj.value + ' ' + obj.text;
                                        return obj;
                                    });
                                    return {
                                        results: results,
                                    };
                                },
                                cache: false
                            },
                    })
                    .val('1234')
                    .trigger('change')
                    // emit event on change.
                    .on('change', function () {
                        vm.$emit('input', this.value)
                        })
                    ;
           }
        },
        watch: {
            value: function (value) {
                // update value
                $(this.$el)
                    .val(value)
                    .trigger('change')
            },
            options: function (options) {
                // update options
                $(this.$el).empty().select2({ data: options })
            }
        },
        destroyed: function () {
            $(this.$el).off().select2('destroy')
        }
    });

    var vue_temp = '    \
        <div class="container" id="vuemodalalert"> \
            <!--<h5>vue test 동적 (Alert) 테스트 </h5>\
            <button id="show-modal" @click="showModal = true">Show Modal</button>-->\
            <modal-alert v-if="showModal" @close="showModal = false">\
                <h3 slot="header">{{headText}}</h3>\
                <h4 slot="body">{{message}}</h4>\
                <h5 slot="footer">\
                    <button v-if=\'showModalConfirm\' class="modal-default-button btn btn-primary" @click="rtnConfirm(\'Y\')">\
                        Yes\
                    </button>\
                    &nbsp;\
                    <button v-if=\'showModalConfirm\' class="modal-default-button btn btn-primary" @click="rtnConfirm(\'N\')">\
                        No\
                    </button>\
                    &nbsp;\
                    <button class="btn_typeA btn_search" @click="showModal = false">\
                        Close\
                    </button>\
                </h4>\
            </modal-alert>\
        </div>';

    Vue.component('modal-alert', {
        template: ' \
        <transition name="modal">\
            <div class="modal-mask">\
                <div class="modal-wrapper">\
                    <div class="modal-container"> \
                        <div class="modal-header">\
                            <slot name="header">\
                                default header\
                            </slot>\
                        </div> \
                        <div class="modal-body">\
                            <slot name="body">\
                                default body\
                            </slot>\
                        </div> \
                        <div class="modal-footer">\
                            <slot name="footer"> \
                                <button class="modal-default-button" @click="$emit(\'close\')">\
                                    Close\
                                </button>\
                            </slot>\
                        </div>\
                    </div>\
                </div>\
            </div>\
        </transition> '
    })


    $(document.body).append(vue_temp);
    app_alert = new Vue({
        el: '#vuemodalalert',
        data: {
            showModal: false,
            headText: "Alert",
            message: "안녕하세요",
            showModalConfirm:''
        }
    })

    var vue_confirm_temp = '    \
        <div class="container" id="vuemodalconfirm"> \
            <modal-user-confirm v-if="showModal" @close="showModal = false">\
                <h3 slot="header">{{headText}}</h3>\
                <h4 slot="body">{{message}}</h4>\
                <h5 slot="footer">\
                    <button class="btn_typeA btn_search" @click="callBack(\'Y\')">\
                        Yes\
                    </button>\
                    <button class="btn_typeA btn_search" @click="callBack(\'N\')">\
                        No\
                    </button>\
                </h4>\
            </modal-user-confirm>\
        </div>';

    Vue.component('modal-user-confirm', {
        template: ' \
        <transition name="modal">\
            <div class="modal-mask">\
                <div class="modal-wrapper">\
                    <div class="modal-container"> \
                        <div class="modal-header">\
                            <slot name="header">\
                                default header\
                            </slot>\
                        </div> \
                        <div class="modal-body">\
                            <slot name="body">\
                                default body\
                            </slot>\
                        </div> \
                        <div class="modal-footer">\
                            <slot name="footer"> \
                                <button class="modal-default-button" @click="$emit(\'close\')">\
                                    Close\
                                </button>\
                            </slot>\
                        </div>\
                    </div>\
                </div>\
            </div>\
        </transition> '
    });

    $(document.body).append(vue_confirm_temp);
    app_confirm = new Vue({
        el: '#vuemodalconfirm',
        data: {
            showModal: false,
            headText: "확인",
            message: "확인하시겠습니까?",
            showModalConfirm:'',
            rtnValue:'',
            callBack:fn_confirm_close
        }
    })


    var vue_message_temp = '    \
        <div id="vuemessage" class="msgbox">\
            <transition name="vuemessageslide" appear>\
                <div class="msgtext" v-if="showMessage"><div>Msg : </div>{{text}}\
                    <button class="message-close-button" @click="showMessage=false">X</button>\
                </div>\
            </transition>\
        </div>\
         ';

    $(document.body).append(vue_message_temp);
    app_message = new Vue({
        el: '#vuemessage',
        data: {
            showMessage: false,
            text: ""
        },
        methods: {
            setMessage:function(msg){
                this.text = msg;
                this.showMessage = open;
                setTimeout(function(){app_message.showMessage = false;},2000);
            }
        }
    });

    var vue_innerpopup_temp = '    \
        <div class="container" id="vueinnerpopup"> \
            <div class="modal-mask" v-if="showModal" @close="showModal = false">\
                <div class="modal-wrapper"  id="innerPopupWrapper">\
                    <div class="innerPopupContainerGrid" id="innerPopupContainerGrid" :style="{width:width,height:height}"> \
                        <div class="innerPopupTitle" id="innerPopupTitle">{{headText}}</div>\
                        <div class="innerPopupClose">\
                            <button class="modal-default-button" @click="defaultCallBack()">Close</button>\
                        </div>\
                        <div class="innerPopupMain" >\
                            <iframe :src="url" width="100%" frameborder="NO" framespacing="0" height="99%"></iframe>\
                        </div> \
                        <div class="innerPopupFooter" >\
                            Msg:\
                        </div> \
                    </div>\
                </div>\
            </div>\
        </div>';

    $(document.body).append(vue_innerpopup_temp);
    app_innerpopup = new Vue({
        el: '#vueinnerpopup',
        data: {
            showModal: false,
            headText: "Inner Popup",
            width:'800px',
            height:'700px',
            url:'',
            rtnValue:'',
            callBack:null,
            defaultCallBack:fn_popup_close
        }
    });



    /*달력*/
    Vue.component('datepicker', {
    props: ['value'],
    template: '<input  :value="value"  style="text-align: center;"/>',
    mounted: function() {
      var self = this;
      $(this.$el).datepicker({
        dateFormat: "yy-mm-dd",
        prevText: '이전 달',
        nextText: '다음 달',
        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
        dayNames: ['일', '월', '화', '수', '목', '금', '토'],
        dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
        dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
        showMonthAfterYear: true,
        yearSuffix: '년',
        changeMonth: true,
        changeYear: true,
        //buttonImage: "images/calendar.gif",
        onSelect: function(d){
          //self.$emit('update-date',d);
          self.$emit('input',d);
          //self.value = d;
          //$(self.$el).value = d;
         // $(self.$el).val(d);
        }
      });
    },
    beforeDestroy: function(){$(this.$el).datepicker('hide').datepicker('destroy')}
    });
    /*달력(월)  <monthpicker v-model="date1" ></monthpicker>*/
    Vue.component('monthpicker', {
    props: ['value' ],
    template: '<input   :value="value" style="text-align: center;"/>',
    mounted: function () {
      var self = this;
      $(this.$el).monthpicker({
        pattern: 'yyyy-mm',// input태그에 표시될 형식
        startYear: 2000, // 시작연도
        finalYear: 2032, // 마지막연도 //
        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'], // 화면에 보여줄 월이름
        openOnFocus: true, // focus시에 달력이 보일지 유무
        disableMonths: [], // 월 비활성화
      });
      $(this.$el).monthpicker().bind('monthpicker-click-month',function(e,month,year){
        var settings = $(self.$el).data('monthpicker').settings;
        var val = settings.selectedYear + '-' + (settings.selectedMonth<10?'0':'') + settings.selectedMonth;
        self.$emit('input', val);
      });
    },
    beforeDestroy: function () { $(this.$el).monthpicker('hide').monthpicker('destroy') }
    });

    //enter auto select
    setTimeout(function(){$(":input").keydown(fn_auto_search)},1500);

});
function fn_auto_search(e){
    if(this.className.indexOf("noSelect")>-1) return;
    if(e.keyCode==13){
        try{
            search();
        }catch(e){
        }
    }
}

function fn_alert(msg,time){
    app_alert.message=msg;
    app_alert.showModal=true;
    var time
    var reset = function(){
        app_alert.showModal=false;
    }
    setTimeout(reset,2000);
}
function fn_message(msg){
    app_message.text = msg;
    app_message.showMessage=!app_message.showMessage;
    setTimeout(function () { app_message.showMessage = false; }, 5000);
}


/* 사용예 async, await 필수
async function fn_update(){
    var rtn = await fn_confirm("진행하시겠습니까?");
    console.log(rtn);
}
*/
function fn_confirm_close(arg){
    app_confirm.rtnValue = arg;
    app_confirm.showModal = false;
}
function fn_confirm(msg){
    app_confirm.message = msg;
    return new Promise(
        resole => {
            app_confirm.showModal = true;
            var id = setInterval(function(){
                        if(!app_confirm.showModal){
                            resole(app_confirm.rtnValue);
                            clearInterval(id);
                            return;
                        }
            },400);
        }
    );
}

function fn_popup_close(arg){
    app_innerpopup.rtnValue = arg;
    app_innerpopup.showModal = false;
    if(app_innerpopup.callBack) app_innerpopup.callBack(arg);
}
function fn_inner_popup_open(arg){

    var jstring = encodeURIComponent(JSON.stringify(arg.map));
    app_innerpopup.url = arg.url + "?param=" + jstring;
    app_innerpopup.callBack = arg.callBack;
    app_innerpopup.showModal = true;
    if(arg.width) app_innerpopup.width = arg.width;
    if(arg.height) app_innerpopup.height = arg.height;
    if(arg.title) app_innerpopup.headText = arg.title;

    //debugger;
    //var container = document.getElementById("innerPopupContainerGrid");
    setTimeout(function(){dragElement();},200);
}

// 새창팝업
function fn_popup_open(arg){
    var jsonData = {
        param:arg.map,
    }
    var para = encodeURIComponent(JSON.stringify(jsonData));
    var url = arg.url+ "?param="+para;
    var option = String('width='+Number(arg.width)+','+'height='+ Number(arg.height)+','+'top='+Number(arg.top)+','+'left='+Number(arg.left));
    //var option = "top=10, left=100, width=1500, height=800 location=no";
    //console.log(url);
    window.open(url,arg.name,option);
 }

 //파일 다운로드 공통팝업
function fn_downLoad(url, jsonData, option){
    var param = encodeURIComponent(JSON.stringify(jsonData));
    var popTitle = option ||"popOpen";
        window.open(url +  "?param=" + param,popTitle,"toolbar=no, location=no, directories=no, status=no, menubar=no,scrollbar=no,resizable=no,width=1400,height=800,top=100,left=100");
}

//화면 연결
// var arg = {
//     map: {arg1:1,domn_nm:"링크드 파라레트다.",},
//     url : "/service/sample/innerPopup2/selectForm.do",
//     name : "화면 연결 테스트"
// }
// fn_openMenuTab(arg);
function fn_openMenuTab(arg){
    if(parent && parent.addLinkedTab){
        parent.addLinkedTab(arg)
    }else{
        console.error("부모창을 찾을 수가 없습니다.");
    }

}


//vue 사용없이 ex) fn_setSelect2("#dept",{regn:'2'},"selectDeptList"); param.dept = $('#dept').val();
function fn_setSelect2(id,para,url) {
    if(url.indexOf('/') == -1){
        url = "/common/selectList.do/" + url;
    }
    $(id).select2({
        placeholder: '선택',
        //templateResult: formatState,
        closeOnSelect: true,
        allowClear: true,
        minimumInputLength: 1,
        ajax: {
            url:url,
            type:"POST",
            dataType: 'json',
            contentType : "application/json; charset=UTF-8",
            delay: 250,
            data: function (params) {
                      para.q = params.term;
                      var param = {
                          map: para
                      };
                      return JSON.stringify(param);
                  },
            processResults: function (data, params) {
                params.page = params.page || 1;
                var results = data.resultList.map(function(obj){
                    obj.id = obj.id||obj.value;
                    obj.text = obj.value + ' ' + obj.text;
                    return obj;
                });
                return {
                    results: results,
                };
            },
            cache: false
        },
    });
}

var app_code_popup;

$(document.body).ready(function () {
    try{
        if(!Vue) return;
    }catch(exception){
        return;
    }
    var vue_temp2 = '    \
        <div   id="vuemodalpopup"> \
            <modal-alert v-if="showModal" @close="showModal = false">\
                <h3 slot="header">{{headText}}</h3>\
                <div slot="body">\
                    <select2 name="popupSelectCode" id="popupSelectCode" class="form-control" v-model="keys.code"    :url="url" :para="para"  style="width:300px" >\
                    </select2>\
                </div>\
                <h5 slot="footer">\
                    <button class="modal-default-button btn btn-primary" @click="selectCode()">\
                        선택\
                    </button>\
                    &nbsp;\
                    <button class="modal-default-button btn btn-primary" @click="showModal = false">\
                        닫기\
                    </button>\
                </h4>\
            </modal-alert>\
        </div>';
    $(document.body).append(vue_temp2);
    app_code_popup = new Vue({
        el: '#vuemodalpopup',
        data: {
            keys:{
                code:''
            },
            para:{
            },
            url:'selectDeptList',
            showModal: false,
            headText: "Code Search",
            message: "안녕하세요",
            callBack:function(){}
        },
        methods:{
            selectCode:function(){
                var data = $('#popupSelectCode').select2('data')[0];
                if(!data) return;
                //console.log(data);
                this.callBack(data);
                this.showModal = false;
            }
        }
    })
});



/* 동적 콤보 inner팝업 호출) 예)
            fn_getCodePopup({comp:'1'},'selectDeptList',
                function(data){
                    console.log("call data",data);
                },"조직 검색"
            );
*/
function fn_getCodePopup(param,url,callBack,title){
    if(title) app_code_popup.headText = title;
    app_code_popup.para=param;
    app_code_popup.url=url;
    app_code_popup.showModal=true;
    app_code_popup.callBack = callBack;
}



function dragElement() {
    var elmnt = document.getElementById("innerPopupContainerGrid");
    var parent = elmnt.parentElement;
    elmnt.style.top = (parent.offsetHeight - elmnt.offsetHeight)/2 + "px";
    elmnt.style.left = (parent.offsetWidth - elmnt.offsetWidth)/2 + "px";

    var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
    if (document.getElementById("innerPopupTitle")) {
        // if present, the header is where you move the DIV from:
        document.getElementById("innerPopupTitle").onmousedown = dragMouseDown;
    } else {
        // otherwise, move the DIV from anywhere inside the DIV:
        elmnt.onmousedown = dragMouseDown;
    }

    function dragMouseDown(e) {
        e = e || window.event;
        e.preventDefault();
        // get the mouse cursor position at startup:
        pos3 = e.clientX;
        pos4 = e.clientY;
        document.onmouseup = closeDragElement;
        // call a function whenever the cursor moves:
        document.onmousemove = elementDrag;
    }

    function elementDrag(e) {
        e = e || window.event;
        e.preventDefault();
        // calculate the new cursor position:
        // console.log(e.clientX);
        // console.log(elmnt.offsetTop);
        pos1 = pos3 - e.clientX;
        pos2 = pos4 - e.clientY;
        pos3 = e.clientX;
        pos4 = e.clientY;
        // set the element's new position:
        elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
        elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
    }

    function closeDragElement() {
        // stop moving when mouse button is released:
        document.onmouseup = null;
        document.onmousemove = null;
    }
}