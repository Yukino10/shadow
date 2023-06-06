package com.shadow.web.mapper.device;

import com.shadow.web.model.device.DeviceProp;
import com.shadow.web.model.device.DevicePropExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DevicePropMapper {
    int countByExample(DevicePropExample example);

    int deleteByExample(DevicePropExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DeviceProp record);

    int insertSelective(DeviceProp record);

    List<DeviceProp> selectByExample(DevicePropExample example);

    DeviceProp selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DeviceProp record, @Param("example") DevicePropExample example);

    int updateByExample(@Param("record") DeviceProp record, @Param("example") DevicePropExample example);

    int updateByPrimaryKeySelective(DeviceProp record);

    int updateByPrimaryKey(DeviceProp record);
}