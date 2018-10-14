package com.xy.ecommerce.controller.exposed;

import com.github.pagehelper.PageInfo;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    public Response getProductDetail(Integer productId){
        Response response=productService.getProductDetail(productId, false);
        if(!response.isSuccess()){
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public Response<PageInfo> productLists(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        Response<PageInfo> response=productService.getProductList(pageNum, pageSize);
        if(!response.isSuccess()){
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "rank_list.do", method = RequestMethod.GET)
    public Response<List<Map<String, Integer>>> productRankList(){
        Response<List<Map<String, Integer>>> response=productService.getRankList();
        if(!response.isSuccess()){
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    @RequestMapping(value = "rank.do", method = RequestMethod.GET)
    public Response<Integer> productRankList(Integer productId){
        Response<Integer> response=productService.getRank(productId);
        if(!response.isSuccess()){
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return response;
    }
}
