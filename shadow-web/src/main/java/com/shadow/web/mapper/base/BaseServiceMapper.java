package com.shadow.web.mapper.base;

import com.shadow.web.model.base.BaseService;
import com.shadow.web.model.base.BaseServiceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BaseServiceMapper {
    int countByExample(BaseServiceExample example);

    int deleteByExample(BaseServiceExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BaseService record);

    int insertSelective(BaseService record);

    List<BaseService> selectByExample(BaseServiceExample example);

    BaseService selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BaseService record, @Param("example") BaseServiceExample example);

    int updateByExample(@Param("record") BaseService record, @Param("example") BaseServiceExample example);

    int updateByPrimaryKeySelective(BaseService record);

    int updateByPrimaryKey(BaseService record);
}