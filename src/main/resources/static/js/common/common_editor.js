function initEditor(config,config2){
    var initializedId = "";
    if(config && config.initializedId) config.initializedId;

    var _config2 = {
        txHost: '', /* 런타임 시 리소스들을 로딩할 때 필요한 부분으로, 경로가 변경되면 이 부분 수정이 필요. ex) http://xxx.xxx.com */
        txPath: '', /* 런타임 시 리소스들을 로딩할 때 필요한 부분으로, 경로가 변경되면 이 부분 수정이 필요. ex) /xxx/xxx/ */
        txService: 'sample', /* 수정필요없음. */
        txProject: 'sample', /* 수정필요없음. 프로젝트가 여러개일 경우만 수정한다. */
        initializedId: "2", /* 대부분의 경우에 빈문자열 */
        wrapper: "tx_trex_container2", /* 에디터를 둘러싸고 있는 레이어 이름(에디터 컨테이너) */
        form: 'tx_editor_form2'+"", /* 등록하기 위한 Form 이름 */
        txIconPath: "/daumeditor/images/icon/editor/", /*에디터에 사용되는 이미지 디렉터리, 필요에 따라 수정한다. */
        txDecoPath: "/daumeditor/images/deco/contents/", /*본문에 사용되는 이미지 디렉터리, 서비스에서 사용할 때는 완성된 컨텐츠로 배포되기 위해 절대경로로 수정한다. */
        canvas: {
            exitEditor:{
            },
            styles: {
                color: "#123456", /* 기본 글자색 */
                fontFamily: "굴림", /* 기본 글자체 */
                fontSize: "10pt", /* 기본 글자크기 */
                backgroundColor: "#fff", /*기본 배경색 */
                lineHeight: "1.5", /*기본 줄간격 */
                padding: "8px" /* 위지윅 영역의 여백 */
            },
            showGuideArea: false,
            initHeight:config.initHeight||400
        },
        events: {
            preventUnload: false
        },
        sidebar: {
            attachbox: {
                show: true,
                confirmForDeleteAll: true
            }
        },
        size: {
            //contentWidth: 700 /* 지정된 본문영역의 넓이가 있을 경우에 설정 */
        }
    };
    var _initHeight = 400;
    if(config2 && config2.initHeight ) _initHeight= config2.initHeight;
    var _config3 = {
        txHost: '', /* 런타임 시 리소스들을 로딩할 때 필요한 부분으로, 경로가 변경되면 이 부분 수정이 필요. ex) http://xxx.xxx.com */
        txPath: '', /* 런타임 시 리소스들을 로딩할 때 필요한 부분으로, 경로가 변경되면 이 부분 수정이 필요. ex) /xxx/xxx/ */
        txService: 'sample', /* 수정필요없음. */
        txProject: 'sample', /* 수정필요없음. 프로젝트가 여러개일 경우만 수정한다. */
        initializedId: "3", /* 대부분의 경우에 빈문자열 */
        wrapper: "tx_trex_container3", /* 에디터를 둘러싸고 있는 레이어 이름(에디터 컨테이너) */
        form: 'tx_editor_form3'+"", /* 등록하기 위한 Form 이름 */
        txIconPath: "/daumeditor/images/icon/editor/", /*에디터에 사용되는 이미지 디렉터리, 필요에 따라 수정한다. */
        txDecoPath: "/daumeditor/images/deco/contents/", /*본문에 사용되는 이미지 디렉터리, 서비스에서 사용할 때는 완성된 컨텐츠로 배포되기 위해 절대경로로 수정한다. */
        canvas: {
            exitEditor:{
            },
            styles: {
                color: "#123456", /* 기본 글자색 */
                fontFamily: "굴림", /* 기본 글자체 */
                fontSize: "10pt", /* 기본 글자크기 */
                backgroundColor: "#fff", /*기본 배경색 */
                lineHeight: "1.5", /*기본 줄간격 */
                padding: "8px" /* 위지윅 영역의 여백 */
            },
            showGuideArea: false,
            initHeight:_initHeight
        },
        events: {
            preventUnload: false
        },
        sidebar: {
            attachbox: {
                show: true,
                confirmForDeleteAll: true
            }
        },
        size: {
            //contentWidth: 700 /* 지정된 본문영역의 넓이가 있을 경우에 설정 */
        }
    };

    new Editor(_config2);
    if(config2){
        Editor.getCanvas().observeJob(Trex.Ev.__IFRAME_LOAD_COMPLETE, function() {
//            Editor.modify({
//                content: 'Editor1'
//            });
//            _config.initializedId = config2.initializedId;
//            _config.form =  'tx_editor_form2';
//            _config.wrapper = 'tx_trex_container2';
            new Editor(_config3);
            Editor.getCanvas().observeJob(Trex.Ev.__IFRAME_LOAD_COMPLETE, function(ev) {
//                Editor.modify({
//                    content: 'Editor2'
//                });
            });
        });
    }
}

/* 2개 이상일때 async 로 사용  ex) await fn_setEditorContent("2",data.resultList[0].affr_sbc); */
function fn_setEditorContent(id,text){
    return new Promise(
        resole => {
            setTimeout(function (){
                Editor.switchEditor(id);
                Editor.modify({ content: text});
                resole();
            },150);
        }
    );
    Editor.switchEditor("3");
    Editor.modify({ content: text });
}