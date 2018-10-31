package com.taotao.cart.controller;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.service.ItemService;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:购物车的Controller
 * @author:cxg
 * @Date:${time}
 */
@Controller
public class CartController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;


    /**
     * 添加商品到购物车里
     * @param itemId
     * @param num
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/cart/add/{itemId}")
    public String addItemCart(@PathVariable Long itemId,
                              @RequestParam(defaultValue = "1") Integer num,
                              HttpServletResponse response, HttpServletRequest request) {

        //1  从cookie中获取token
        String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
        //2  获取用户信息
        TaotaoResult userByToken = userService.getUserByToken(token);
        //3  根据itemId获取商品数据
       TbItem tbItem= itemService.getItemById(itemId);
        //4 判断userByToken是否成功获取
        //4.1 getStatus()为200表示获取成功，用户登入成功
        if (userByToken.getStatus()==200){
            //4.1.1 通过getData()获取用户数据
            TbUser tbUser = (TbUser) userByToken.getData();
            cartService.addItemCartToRedis(tbItem,num,tbUser.getId());
        }
        //4.2 未登入状态添加商品到购物车的cookie中
        else {
            //4.2.1 根据itemId获取商品信息
            TbItem itemById = itemService.getItemById(itemId);
            //4.2.2  获取cookie中的购物车商品信息
            List<TbItem> cartListByCookie = getCartListByCookie(request);
            //4.2.3 设置布尔值
            boolean flag=false;
            //4.2.4 购物车商品存在，数量相加
            for (TbItem tbItem1:cartListByCookie) {
                if (tbItem1.getId()==itemId.longValue()){
                    tbItem1.setNum(tbItem1.getNum() + num);
                    flag=true;
                    break;
                }
            }
            //4.2.5 购物车商品不存在
            if (!flag) {
                itemById.setNum(num);
                String image = itemById.getImage();
                if (StringUtils.isNotBlank(image)){
                    String[] images = image.split(",");
                    itemById.setImage(images[0]);
                }
                cartListByCookie.add(itemById);
            }
            CookieUtils.setCookie(request,response,"COOKIE_CART_KEY",
                    JsonUtils.objectToJson(cartListByCookie),7*24*3600,true);

        }
        return "cartSuccess";
    }
    /**
     * 根据cookie查询购物车商品信息
     * @param request
     * @return
     */
    public List<TbItem> getCartListByCookie(HttpServletRequest request){
        //从cookie中取商品
        String json = CookieUtils.getCookieValue(request, "COOKIE_CART_KEY", true);
        if (StringUtils.isNotBlank(json)){
            List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
            return list;
        }
        return new ArrayList<>();
    }



    /**
     * 展示购物车信息(redis和cookie中取)
     * @param request
     * @return
     */
    @RequestMapping("/cart/cart")
    public String showCartList(HttpServletRequest request) {
        //1 从cookie中获取token
        String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
        //2 通过token获取用户信息
        TaotaoResult userByToken = userService.getUserByToken(token);
        //3 判断用户是否登入
        //3.1 用户登入成功
        if (userByToken.getStatus()==200){
            TbUser tbUser = (TbUser) userByToken.getData();
            List<TbItem> cartList = cartService.getCartItemByUserId(tbUser.getId());
            request.setAttribute("cartList",cartList);
        }
        //4 用户不存在
        else {
            //4.1 取购物车商品信息
            List<TbItem> cartList = getCartListByCookie(request);
            request.setAttribute("cartList", cartList);
        }
            return "cart";
    }

    /**
     * 修改购物车商品数量
     * @param itemId
     * @param num
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public TaotaoResult updateItemNum(@PathVariable Long itemId,@PathVariable Integer num,
                                      HttpServletResponse response, HttpServletRequest request){
        //1 从cookie中获取token
        String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
        //2 通过token获取用户信息
        TaotaoResult userByToken = userService.getUserByToken(token);
        //3 判断userByToken是否成功获取
        //3.1  用户登入成功 getStatus()为200表示获取成功，用户登入成功
        if (userByToken.getStatus() == 200) {
            //3.1.1   获取用户信息
            TbUser tbUser = (TbUser) userByToken.getData();
            cartService.updateCartById(tbUser.getId(),itemId,num);
        }
        //3.2 未登入
        else {
            //3.2.1 取购物车商品列表
            List<TbItem> cartItemList = getCartListByCookie(request);
            //3.2.2 遍历列表找到对应的商品
            for (TbItem tbItem: cartItemList) {
                if(tbItem.getId()==itemId.longValue()){
                    tbItem.setNum(num);
                    break;
                }

            }
            //3.2.3 将修改的数据写入到cookie中，COOKIE_CART_KEY是cookie中的key，cookieMaxage是有效时间，isEncode是否编码
            CookieUtils.setCookie(request, response, "COOKIE_CART_KEY",
                    JsonUtils.objectToJson(cartItemList), 604800, true);

        }
        return TaotaoResult.ok();
    }

    /**
     * 删除购物商品
     * @param itemId
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/cart/delete/{itemId}")
    public String deleteItem(@PathVariable Long itemId,HttpServletRequest request,
                                   HttpServletResponse response) {
        //1 从cookie中获取token
        String token = CookieUtils.getCookieValue(request, "TT_TOKEN");
        //2 通过token获取用户信息
        TaotaoResult userByToken = userService.getUserByToken(token);
        //3 判断userByToken是否成功获取
        //3.1 用户登入成功 getStatus()为200表示获取成功，
        if (userByToken.getStatus() == 200) {
            //3.1.1   获取用户信息
            TbUser tbUser = (TbUser) userByToken.getData();
            cartService.deleteCartById(tbUser.getId(),itemId);
        }
        //4 没有登入
        else {
            deleteCookieCart(itemId,request,response);
            String cookies = CookieUtils.getCookieValue(request, "COOKIE_CART_KEY",true);
            List<TbItem> list = JsonUtils.jsonToList(cookies, TbItem.class);
           System.out.println("输出cookie"+list);

        }
        return "redirect:/cart/cart.html";
    }

    private void deleteCookieCart(Long itemId, HttpServletRequest request,HttpServletResponse response) {
        TbItem deleteItem= new TbItem();
        //取购物车商品信息
        List<TbItem> cartItemList = getCartListByCookie(request);
        //遍历列表找到对应的商品
        boolean flag = false;
        for (TbItem tbItem : cartItemList) {
            if (tbItem.getId() == itemId.longValue()) {
               cartItemList.remove(tbItem);
                flag = true;
                break;
            }
        }
        if (!flag) {
            //将修改的数据写入到cookie中，COOKIE_CART_KEY是cookie中的key，cookieMaxage是有效时间，isEncode是否编码
            CookieUtils.setCookie(request, response, "COOKIE_CART_KEY",
                    JsonUtils.objectToJson(cartItemList), 604800, true);
        }
    }
}
