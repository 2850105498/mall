package com.taotao.order.controller;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:订单确定页面
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class OrderCartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @RequestMapping("order/order-cart")
    public String showOrderCart(HttpServletRequest request) {
/*        //1 从cookie中获取token
        String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
        //2 根据token获取用户洗信息
        TaotaoResult userByToken = userService.getUserByToken(token);
        //3 判断是否登入
        if (userByToken.getStatus()==200){
            //3.1 登入则根据用户id查询购物车列表信息
            TbUser tbUser = (TbUser) userByToken.getData();
            List<TbItem> cartList = cartService.getCartItemByUserId(tbUser.getId());
            request.setAttribute("cartList",cartList);
        }
        //4 将信息放入到request域中*/

        //拦截器，从拦截器中获取用户
       TbUser tbUser = (TbUser) request.getAttribute("USER_INFO");
        List<TbItem> cartList = cartService.getCartItemByUserId(tbUser.getId());
        request.setAttribute("cartList",cartList);
        return  "order-cart";
    }


    @RequestMapping(value = "/order/create",method = RequestMethod.POST)
    public String createOrder(OrderInfo orderInfo, Model model){
        //生成订单
        TaotaoResult result = orderService.createOrder(orderInfo);

        //返回逻辑视图
        model.addAttribute("orderId",result.getData().toString());
        model.addAttribute("payment",orderInfo.getPayment());
        //预计三天后送达
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(3);
        model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
        return "success";

    }

}
