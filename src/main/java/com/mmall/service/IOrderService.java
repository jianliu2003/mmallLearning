package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * Created by jianl on 2018/4/8.
 */
public interface IOrderService {
    public ServerResponse pay(Integer userId, Long orderNo, String path);

    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    public ServerResponse alipayCallback(Map<String,String> params);

    public ServerResponse<OrderVo> create(Integer userId, Integer shippingId);

    public ServerResponse cancel(Integer userId,Long orderNo);

    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    public ServerResponse<OrderVo> detail(Integer userId, Long orderNo);

    public ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

    public ServerResponse<PageInfo> manageList(Integer pageNum,Integer pageSize);

    public ServerResponse<OrderVo> manageDetail(Long orderNo);

    public ServerResponse<PageInfo> manageSearch(Long orderNo, Integer pageNum, Integer pageSize);

    public ServerResponse<String> manageSendGoods(Long orderNo);

}
