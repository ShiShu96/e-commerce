package com.xy.ecommerce.controller.backend;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.Category;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/backend/category")
public class BackendCategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    public Response addCategory(String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        return categoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping(value = "update_category_name.do", method = RequestMethod.POST)
    public Response updateCategoryName(int categoryId,String categoryName, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        // modify category name
        return categoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "get_parallel_category.do", method = RequestMethod.GET)
    public Response<List<Category>> getParallelChildrenCategory(@RequestParam(value="categoryId", defaultValue = "0") int parentId, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        // query parallel children categories
        return categoryService.getParallelChildrenCategory(parentId);
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    public Response<Set<Category>> getDeepChildrenCategory(@RequestParam(value="categoryId", defaultValue = "0") int id, HttpSession session){
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null) return Response.createByError(ResponseCode.NEED_LOGIN);
        if (user.getRole()!=Const.ROLE_ADMIN) return Response.createByError(ResponseCode.NOT_AUTHORIZED);
        // query all children categories
        return categoryService.getCategoryWithChildren(id);
    }
}
