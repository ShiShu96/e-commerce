package com.xy.ecommerce.controller.exposed;

import com.xy.ecommerce.common.Const;
import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.common.ResponseCode;
import com.xy.ecommerce.entity.Shipping;
import com.xy.ecommerce.entity.User;
import com.xy.ecommerce.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    public Response add(Shipping shipping, HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return shippingService.add(user.getId(), shipping);
    }

    @RequestMapping(value = "delete.do", method = RequestMethod.POST)
    public Response delete(Integer shippingId, HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return shippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping(value = "update.do", method = RequestMethod.POST)
    public Response update(Shipping shipping, HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return shippingService.update(user.getId(), shipping);
    }

    @RequestMapping(value = "select.do", method = RequestMethod.GET)
    public Response select(Integer shippingId, HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return shippingService.select(user.getId(), shippingId);
    }

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    public Response list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return Response.createByError(ResponseCode.NEED_LOGIN);
        }
        return shippingService.list(user.getId(), pageNum, pageSize);
    }
}
