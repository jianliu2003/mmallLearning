package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jianl on 2018/4/4.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired ShippingMapper shippingMapper;

    public ServerResponse<Integer> add(Integer userId, Shipping shipping){
        if(null == shipping)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySuccess(shipping.getId());
        }
        return ServerResponse.createByErrorMessage("新增地址失败");
    }


    public ServerResponse del(Integer userId, Integer shippingId){
        if(null == shippingId)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        int rowCount = shippingMapper.deleteByShippingIdAndUserId(userId, shippingId);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }


    public ServerResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }


    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        if(null == shippingId)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        Shipping shipping = shippingMapper.selectByShippingIdAndUserId(userId, shippingId);
        if (null == shipping){
            return ServerResponse.createByErrorMessage("没有查询到该地址");
        }
        return ServerResponse.createBySuccess(shipping);
    }


    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectListByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
