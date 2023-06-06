package com.shadow.web.mapper.product;

import com.shadow.web.model.product.ProductServer;
import com.shadow.web.model.product.ProductServerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductServerMapper {
    int countByExample(ProductServerExample example);

    int deleteByExample(ProductServerExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductServer record);

    int insertSelective(ProductServer record);

    List<ProductServer> selectByExample(ProductServerExample example);

    ProductServer selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProductServer record, @Param("example") ProductServerExample example);

    int updateByExample(@Param("record") ProductServer record, @Param("example") ProductServerExample example);

    int updateByPrimaryKeySelective(ProductServer record);

    int updateByPrimaryKey(ProductServer record);
}