package com.xy.ecommerce.controller.backend;

import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.Product;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.ProductService;
import com.xy.ecommerce.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/backend/product/")
public class BackendProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "save.do")
    public Response productSave(Product product, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        return productService.saveOrUpdateProduct(product);
    }

    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    public Response setSaleStatus(Integer productId, Integer status, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        return productService.setSaleStatus(productId, status);
    }

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    public Response getProductDetail(Integer productId, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        return productService.getProductDetail(productId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public Response<PageInfo> productLists(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        return productService.getProductList(pageNum, pageSize);
    }

    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    public Response<List<ProductDetailVo>> productSearch(String productName, Integer productId, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        return productService.searchProduct(productName, productId);
    }

}
