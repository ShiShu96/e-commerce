package com.xy.ecommerce.controller.backend;

import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.FileService;
import com.xy.ecommerce.service.ProductService;
import com.xy.ecommerce.util.PropertiesUtil;
import com.xy.ecommerce.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/backend/product/")
public class BackendProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    public Response productSave(Product product, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        if (user.getRole()!=Const.ROLE_ADMIN) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        }

        return productService.saveOrUpdateProduct(product);
    }

    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    public Response setSaleStatus(Integer productId, Integer status, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        if (user.getRole()!=Const.ROLE_ADMIN) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        }
        return productService.setSaleStatus(productId, status);
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    public Response<ProductDetailVo> getProductDetail(Integer productId, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        if (user.getRole()!=Const.ROLE_ADMIN) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        }
        return productService.getProductDetail(productId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public Response<PageInfo> productLists(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        if (user.getRole()!=Const.ROLE_ADMIN) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        }
        return productService.getProductList(pageNum, pageSize);
    }

    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    public Response<List<ProductDetailVo>> productSearch(String productName, Integer productId, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        if (user.getRole()!=Const.ROLE_ADMIN) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        }
        return productService.searchProduct(productName, productId);
    }

    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    public Response<Map<String, String>> upload(MultipartFile file, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        if (user.getRole()!=Const.ROLE_ADMIN) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        }
        String path=session.getServletContext().getRealPath("upload");
        String targetFileName=fileService.upload(file, path);
        String url=PropertiesUtil.getProperty("ftp.server.prefix")+targetFileName;

        Map<String, String> map=new HashMap<>();
        map.put("fileName", targetFileName);
        map.put("url", url);
        return Response.createBySuccess(map);
    }

}
