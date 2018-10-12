package com.xy.ecommerce.controller.exposed;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.CartService;
import com.xy.ecommerce.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    public Response<CartVo> add(Integer count, Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.add(user.getId(),productId,count);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    public Response<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.list(user.getId());
    }

    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    public Response<CartVo> update(Integer count, Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.update(user.getId(),productId,count);
    }

    @RequestMapping(value = "delete.do", method = RequestMethod.POST)
    public Response<CartVo> delete(String productIds, HttpSession session ){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.delete(user.getId(), productIds);
    }

    @RequestMapping(value = "select.do", method = RequestMethod.GET)
    public Response<CartVo> select(Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectUnSelect(user.getId(), Const.CART_CHECKED, productId);
    }

    @RequestMapping(value = "un_select.do", method = RequestMethod.GET)
    public Response<CartVo> unSelect(Integer productId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectUnSelect(user.getId(), Const.CART_UNCHECKED, productId);
    }

    @RequestMapping(value = "select_all.do", method = RequestMethod.GET)
    public Response<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectUnSelect(user.getId(), Const.CART_CHECKED, null);
    }

    @RequestMapping(value = "un_select_all.do", method = RequestMethod.GET)
    public Response<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return cartService.selectUnSelect(user.getId(), Const.CART_UNCHECKED, null);
    }


}
