package com.shadow.web.mapper.product;

import com.shadow.web.model.result.ProductInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther: wangzhendong
 * @Date: 2019/10/31 14:05
 * @Description:
 */
public interface ProductListMapper {

    List<ProductInfo> select(@Param("productId") Integer productId);

    List<ProductInfo> searchProductByExample(Map<String, Object> input);
}
