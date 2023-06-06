package com.shadow.web.mapper.device;

import com.shadow.web.model.device.DevicePropChild;
import com.shadow.web.model.device.DevicePropChildExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DevicePropChildMapper {
    int countByExample(DevicePropChildExample example);

    int deleteByExample(DevicePropChildExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DevicePropChild record);

    int insertSelective(DevicePropChild record);

    List<DevicePropChild> selectByExample(DevicePropChildExample example);

    DevicePropChild selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DevicePropChild record, @Param("example") DevicePropChildExample example);

    int updateByExample(@Param("record") DevicePropChild record, @Param("example") DevicePropChildExample example);

    int updateByPrimaryKeySelective(DevicePropChild record);

    int updateByPrimaryKey(DevicePropChild record);
}