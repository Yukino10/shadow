package com.shadow.web.mapper.device;

import com.shadow.web.model.device.DevicePropArray;
import com.shadow.web.model.device.DevicePropArrayExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DevicePropArrayMapper {
    int countByExample(DevicePropArrayExample example);

    int deleteByExample(DevicePropArrayExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(DevicePropArray record);

    int insertSelective(DevicePropArray record);

    List<DevicePropArray> selectByExample(DevicePropArrayExample example);

    DevicePropArray selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") DevicePropArray record, @Param("example") DevicePropArrayExample example);

    int updateByExample(@Param("record") DevicePropArray record, @Param("example") DevicePropArrayExample example);

    int updateByPrimaryKeySelective(DevicePropArray record);

    int updateByPrimaryKey(DevicePropArray record);
}