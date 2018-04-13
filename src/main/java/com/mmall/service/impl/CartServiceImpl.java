package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianl on 2018/4/3.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 根据userId查询CartVo对象
     * @param userId
     * @return
     */
    private CartVo getCartVo(Integer userId){
        CartVo cartVo = new CartVo();
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        BigDecimal cartTotalPrice = new BigDecimal("0");
        //遍历cartList，将Cart对象转为CartProductVo对象，并添加到cartProductVoList中
        for (Cart cart : cartList){
            CartProductVo cartProductVo = new CartProductVo();
            cartProductVo.setId(cart.getId());
            cartProductVo.setUserId(userId);
            cartProductVo.setProductId(cart.getProductId());
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if(null != product){
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductStock(product.getStock());
                int buyNum = 0;
                if(cart.getQuantity()>product.getStock()){
                    //库存不足(购物车中商品的数量>商品库存数量),则设置为LIMIT_NUM_FAIL，并且要更新购物车中商品的数量
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    buyNum = product.getStock();
                    Cart updateCart = new Cart();
                    updateCart.setId(cart.getId());
                    updateCart.setQuantity(buyNum);
                    cartMapper.updateByPrimaryKeySelective(updateCart);
                }else {
                    //库存充足
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    buyNum = cart.getQuantity();
                }
                cartProductVo.setQuantity(buyNum);
                //计算productTotalPrice
                cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),buyNum));
            }
            cartProductVo.setProductChecked(cart.getChecked());
            if(cart.getChecked() == Const.Cart.CHECKED){
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if (null == userId)
            return false;
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }


    @Override
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(null == productId || count < 0)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (null != cart){
            //用户添加的商品在购物车中,则更新用户在购物车中该商品的数量
            Cart cartUpdate = new Cart();
            cartUpdate.setId(cart.getId());
            cartUpdate.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cartUpdate);
        }else {
            //用户添加的商品不在购物车中
            Cart cartNew = new Cart();
            cartNew.setUserId(userId);
            cartNew.setProductId(productId);
            cartNew.setQuantity(count);
            cartNew.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cartNew);
        }
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    @Override
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(null == productId || count < 0)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (null != cart){
            //用户添加的商品在购物车中,则更新用户在购物车中该商品的数量
            Cart cartUpdate = new Cart();
            cartUpdate.setId(cart.getId());
            cartUpdate.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cartUpdate);
        }
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        if(StringUtils.isBlank(productIds))
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        String[] productIdStrArray = productIds.split(",");
        List<Integer>  productIdList = new ArrayList<Integer>();
        for(String productIdStr : productIdStrArray){
            Integer productIdInt = Integer.parseInt(productIdStr);
            productIdList.add(productIdInt);
        }
        cartMapper.deleteByUserIdAndProductIds(userId,productIdList);
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.updateCheckedOrUnchecked(userId,productId,checked);
        CartVo cartVo = this.getCartVo(userId);
        return ServerResponse.createBySuccess(cartVo);
    }


    public ServerResponse<Integer> getCartProductCount(Integer userId){
        return ServerResponse.createBySuccess(cartMapper.getCartProductCount(userId));
    }



}
