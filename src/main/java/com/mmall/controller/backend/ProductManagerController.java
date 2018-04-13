package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created by jianl on 2018/3/29.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManagerController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse addProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.addOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getDetail(productId);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }


//    @RequestMapping("list.do")
//    @ResponseBody
//    public ServerResponse<List<ProductListVo>> getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
//        if(user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            return iProductService.getProductList(pageNum,pageSize);
//        }
//        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
//    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(HttpSession session, Integer productId, String queryString, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productId, queryString, pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }


    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 @RequestParam(value = "uploadFile", required = false) MultipartFile file,
                                 HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //        {
    //            "success": true/false,
    //                "msg": "error message", # optional
    //            "file_path": "[real file path]"
    //        }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "uploadFile", required = false) MultipartFile file, HttpServletRequest request,HttpServletResponse response) {
        Map map = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            map.put("success",false);
            map.put("msg","请登录管理员");
            return map;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFileName)){
                map.put("success",false);
                map.put("msg","上传失败");
                return map;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            map.put("success",true);
            map.put("msg","上传成功");
            map.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return map;
        } else {
            map.put("success",false);
            map.put("msg","非管理员");
            return map;
        }
    }



    //富文本返回
    //        {
    //            "success": true/false,
    //                "msg": "error message", # optional
    //            "file_path": "[real file path]"
    //        }
    //    @RequestMapping("richtext_img_upload.do")
    //    @ResponseBody
    //    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "uploadFile", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
    //        Map resultMap = Maps.newHashMap();
    //        User user = (User) session.getAttribute(Const.CURRENT_USER);
    //        if (user == null) {
    //            resultMap.put("success", false);
    //            resultMap.put("msg", "请登录管理员");
    //            return resultMap;
    //        }
    //        if (iUserService.checkAdminRole(user).isSuccess()) {
    //            String path = request.getSession().getServletContext().getRealPath("upload");
    //            String targetFileName = iFileService.upload(file, path);
    //            if (StringUtils.isBlank(targetFileName)) {
    //                resultMap.put("success", false);
    //                resultMap.put("msg", "上传失败");
    //                return resultMap;
    //            }
    //            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
    //            resultMap.put("success", true);
    //            resultMap.put("msg", "上传成功");
    //            resultMap.put("file_path", url);
    //            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
    //            return resultMap;
    //        } else {
    //            resultMap.put("success", false);
    //            resultMap.put("msg", "无权限操作");
    //            return resultMap;
    //        }
    //    }

}
