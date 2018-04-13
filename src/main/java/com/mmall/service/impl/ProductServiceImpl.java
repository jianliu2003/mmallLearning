package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jianl on 2018/3/29.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse addOrUpdateProduct(Product product) {
        //1.检查参数是否正确
        if (product == null) {
            return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
        }
        //2.设置product的mainImage属性
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImages = product.getSubImages().split(",");
            if (subImages.length > 0) {
                product.setMainImage(subImages[0]);
            }
        }
        //3.判断是插入或更新
        if (product.getId() == null) {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("添加商品成功");
            }
            return ServerResponse.createByError("添加商品失败");
        } else {
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("更新商品成功");
            }
            return ServerResponse.createByError("更新商品失败 ");
        }
    }

    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        //1.检查参数
        if (productId == null || status == null) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        //2.创建product对象,并设值
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        //3.更新(修改产品状态)
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServerResponse.createBySuccess("修改产品状态成功");
        }
        return ServerResponse.createByError("修改产品状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        //1.检查参数
        if (productId == null) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        //2.根据productId查询得到product
        Product product = productMapper.selectByPrimaryKey(productId);
        //3.判断product是否为null
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        //4.组装ProductDetailVo对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }


//    public ServerResponse<List<ProductListVo>> getProductList(Integer pageNum,Integer pageSize){
//        PageHelper.startPage(pageNum,pageSize);
//        List<Product> productList = productMapper.selectList();
//        List<ProductListVo> productListVoList = Lists.newArrayList();
//        for(Product productItem : productList){
//            ProductListVo productListVo = assembleProductListVo(productItem);
//            productListVoList.add(productListVo);
//        }
//        PageInfo pageInfo = new PageInfo(productList);
//        return ServerResponse.createBySuccess(productListVoList);
//    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        //1.PageHelper设置pageNum和pageSize
        PageHelper.startPage(pageNum,pageSize);
        //2.进行查询
        List<Product> productList = productMapper.selectList();
        //3.创建productListVoList对象，并将productList->productListVoList对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //4.创建pageInfo对象(用productList对象进行初始化)
        PageInfo pageInfo = new PageInfo(productList);
        //5.设置pageInfo对象中的list的值
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(Integer productId,String queryString,Integer pageNum,Integer pageSize){
        //1.PageHelper设置pageNum和pageSize
        PageHelper.startPage(pageNum,pageSize);
        //2.进行查询
        List<Product> productList = productMapper.selectByQueryStringAndProductId(queryString, productId);
        //3.创建productListVoList对象，并将productList->productListVoList对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //4.创建pageInfo对象(用productList对象进行初始化)
        PageInfo pageInfo = new PageInfo(productList);
        //5.设置pageInfo对象中的list的值
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        //1.检查参数
        if (productId == null) {
            return ServerResponse.createByErrorMessage("参数不正确");
        }
        //2.根据productId查询得到product
        Product product = productMapper.selectByPrimaryKey(productId);
        //3.判断product是否为null
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        //4.判断product的状态
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已经下架或删除");
        }
        //5.组装ProductDetailVo对象
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    @Override
    public ServerResponse<PageInfo> searchProductPortal(String queryString,
                                                        Integer categoryId,
                                                        Integer pageNum,
                                                        Integer pageSize,
                                                        String orderBy) {
        //1.queryString和categoryId都为null，则返回
        if (StringUtils.isBlank(queryString) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //2.根据categoryId查询categoryIdList(该分类及其下的子分类)
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(queryString)) {
                //没有该分类,并且没有关键字,则返回一个空的结果集
                //2.1 PageHelper设置pageNum和pageSize
                PageHelper.startPage(pageNum, pageSize);
                //2.2 创建productListVoList对象
                List<ProductListVo> productListVoList = Lists.newArrayList();
                //2.3 创建pageInfo对象（使用productListVoList对象进行初始化）
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        //3.PageHelper设置pageNum和pageSize
        PageHelper.startPage(pageNum, pageSize);
        //4.orderBy的处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                String orderByString = orderByArray[0] + " " + orderByArray[1];
                PageHelper.orderBy(orderByString);
            }
        }
        //5.根据queryString和categoryIdList进行查询
        List<Product> productList = productMapper.selectByQueryStringAndCategoryIds((StringUtils.isBlank(queryString) ? null : queryString), (categoryIdList.size() == 0 ? null : categoryIdList));
        //6.创建productVolist，并将productList对象转换为productVoList对象
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //7.创建pageInfo对象
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        //8.返回
        return ServerResponse.createBySuccess(pageInfo);
    }








}
