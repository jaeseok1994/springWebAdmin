package com.webAdmin.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webAdmin.dao.CommonMybatisDao;
import com.webAdmin.dao.model.fileVO;
import com.webAdmin.security.domain.model.SecurityUser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * author:james
 * day:2020-09-12
 */
@Controller
@RequestMapping("/service/commonFile/{pgm}")
public class FileServiceController {
    @Autowired
    private CommonMybatisDao dao;
    String group = "CommonFile";

    @RequestMapping(method = RequestMethod.POST, value = "/maintfileUpload.do/{sqlId}")
    public ModelAndView maintfileUpload(MultipartHttpServletRequest req, @PathVariable("pgm") String pgm, @PathVariable("sqlId") String sqlId ,ModelMap model ) throws IOException {

        String htmlCodedMapData = null;
        HashMap<String,Object> para = new  HashMap<String,Object>();

        initParam(para);
        try{
            htmlCodedMapData = StringEscapeUtils.unescapeHtml4(req.getParameter("mapData"));
        }catch (Exception e){
            // Logger.errop("JSON string HTML code convert to Character 예외 발생");
            throw new RuntimeException("JSON string HTML code convert to Character 예외 발생");
        }

        Map<String, Object> map = new HashMap();
        List<Map<?, ?>> result = null;
        try {
            //Logger.debug("ExcelController : htmlCodeMapData -" + htmlCodedMapData);
            map = new ObjectMapper().readValue(htmlCodedMapData,HashMap.class);
        }catch (Exception e){
            // Logger.errop("mapData JSON Parse 예외 발생");
            throw new RuntimeException("mapData JSON Parse 예외 발생");
        }
        MultipartFile insertFile = null;

        int count = 0;
        ModelAndView mv = new ModelAndView("jsonView");

        String ext = "";
        Iterator fileIter = req.getFileNames();
        if(fileIter != null && fileIter.hasNext()){
            String paramName = (String) fileIter.next();
            List mFiles = req.getFiles(paramName);
            if(mFiles != null && mFiles.size() >0){
                for(int cnt = 0; cnt < mFiles.size(); cnt++) {
                    insertFile = (MultipartFile) mFiles.get(cnt);
                    String filename = insertFile.getOriginalFilename();
                    //int filelength = filename.length();
                    //int fileval = filename.lastIndexOf('.');
                    //String exfile = filename.substring(fileval + 1, filelength);
                    byte[] byt = insertFile.getBytes();
                    if (filename.length() > 0 && filename != "") {
                        map.put("filedata", byt);
                        map.put("filename", filename);
                    }

                    para.put("mapperGroup",group);
                    para.put("mapperPgm",pgm);
                    para.put("mapperSqlId","selectList");

                    result = dao.selectList(para);
                    para.put("FIL_SN",result.get(0).get("fil_sn"));

                    para.put("mapperSqlId",sqlId);
                    para.put("map",map);


                    count += dao.fileupload(para);
                    mv.addObject("FIL_SN"+cnt,result.get(0).get("fil_sn"));
                }
            }
        }

        setMessage(mv,sqlId);
        return mv;
    }

    @RequestMapping(value = "/maintfileDownload.do/{sqlId}", produces="application/text; charset=utf8")
    public void fileDownload(HttpServletResponse response, HttpServletRequest req, @RequestParam Map map, @PathVariable("pgm") String pgm,@PathVariable("sqlId") String sqlId , ModelMap model ) throws Exception{
       // Map<String, Object> resultFile = new HashMap();
        HashMap<String,Object> para = new  HashMap<String,Object>();
        String param1 = String.valueOf(map.get("param"));
        param1 = param1.replaceAll("&quot;","\"");

        try {
            para = new ObjectMapper().readValue(param1,HashMap.class);
        }catch (IOException e){
            throw new IOException("mapData JSON Parse 예외 발생");
        }
        initParam(para);

        para.put("mapperGroup",group);
        para.put("mapperPgm",pgm);
        para.put("mapperSqlId",sqlId);

        List<Map<?, ?>> resultList = dao.selectList(para);
        if(resultList.isEmpty()){

        }else{
            fileVO vo = (fileVO) resultList.get(0);
            byte[] file = vo.getBLOB1();
            String FileName = vo.getATTC_FIL_NM();

            ServletOutputStream os = response.getOutputStream();


 //           LinkedHashMap test = (LinkedHashMap) para.get("map");
 //           String FileName = (String) test.get("flid");

            //String header = req.getHeader("User-Agent");

            FileName = new String(FileName.getBytes(StandardCharsets.UTF_8),"ISO-8859-1");
            response.setHeader("Content-Disposition",String.format("attachment; filename=\""+ FileName + "\""));

            os.write(file);
            os.flush();

        }

    }


    private void setMessage(ModelAndView mv,String sqlId) {
        if (sqlId.startsWith("select")) {
            mv.addObject("message", "조회되었습니다.");
        } else if (sqlId.startsWith("insert")) {
            mv.addObject("message", "입력이 되었습니다.");
        } else if (sqlId.startsWith("maint")) {
            mv.addObject("message", "저장이 되었습니다.");
        } else if (sqlId.startsWith("update")) {
            mv.addObject("message", "수정이 되었습니다.");
        } else if (sqlId.startsWith("delete")) {
            mv.addObject("message", "삭제이 되었습니다.");
        }
    }
    private void initParam(Map map){
        String userId = "";
        SecurityUser user = getSecurityUser();
        if(user != null){
            userId = user.getUid();
        }
        map.put("_loginId_",userId);
    }
    private void initParam(HashMap<String,Object> param){
        String userId = "";
        SecurityUser user = getSecurityUser();
        if(user != null){
            userId = user.getUid();
        }
        param.put("_loginId_",userId);
    }
    private SecurityUser getSecurityUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";
        if(auth != null){
            Object o = auth.getPrincipal();
            if(o instanceof SecurityUser){
                SecurityUser user = (SecurityUser)auth.getPrincipal();
                return user;
            }
        }
        return null;
    }
}
