package com.xy.ecommerce.controller.exposed;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @RequestMapping(value = "create.do", method = RequestMethod.POST)
    public Response create(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return orderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping(value = "cancel.do", method = RequestMethod.POST)
    public Response cancel(HttpSession session, Long orderNo){
        if (orderNo==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return orderService.cancelOrder(user.getId(),orderNo);
    }

    @RequestMapping(value = "select.do", method = RequestMethod.GET)
    public Response select(HttpSession session, Long orderNo){
        if (orderNo==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return Response.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderDetail(user.getId(),orderNo);
    }
}
