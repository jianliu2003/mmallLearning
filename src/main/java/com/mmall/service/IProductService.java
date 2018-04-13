package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;

import java.util.List;

/**
 * Created by jianl on 2018/3/29.
 */
public interface IProductService {

    public ServerResponse addOrUpdateProduct(Product product);

    public ServerResponse setSaleStatus(Integer productId, Integer status);

    /**
     * 后台
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getDetail(Integer productId);

    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    public ServerResponse<PageInfo> searchProduct(Integer productId,String queryString,Integer pageNum,Integer pageSize);

    /**
     * 前台
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    public ServerResponse<PageInfo> searchProductPortal(String queryString,
                                                        Integer categoryId,
                                                        Integer pageNum,
                                                        Integer pageSize,
                                                        String orderBy);

}
