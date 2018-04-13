package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    Cart selectByUserIdAndProductId(@Param("userId")Integer userId, @Param("productId")Integer productId);

    void deleteByUserIdAndProductIds(@Param("userId")Integer userId,@Param("productIdList")List<Integer> productIdList);

    void updateCheckedOrUnchecked(@Param("userId")Integer userId,@Param("productId")Integer productId,@Param("checked")Integer checked);

    int getCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);
}