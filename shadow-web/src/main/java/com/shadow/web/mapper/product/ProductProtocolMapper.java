package com.shadow.web.mapper.product;

import com.shadow.web.model.product.ProductProtocol;
import com.shadow.web.model.product.ProductProtocolExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductProtocolMapper {
    int countByExample(ProductProtocolExample example);

    int deleteByExample(ProductProtocolExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ProductProtocol record);

    int insertSelective(ProductProtocol record);

    List<ProductProtocol> selectByExample(ProductProtocolExample example);

    ProductProtocol selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ProductProtocol record, @Param("example") ProductProtocolExample example);

    int updateByExample(@Param("record") ProductProtocol record, @Param("example") ProductProtocolExample example);

    int updateByPrimaryKeySelective(ProductProtocol record);

    int updateByPrimaryKey(ProductProtocol record);
}