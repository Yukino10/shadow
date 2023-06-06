package com.shadow.web.mapper.auth;

import com.shadow.web.model.auth.InformationSchema;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/7 16:18
 * @Description:
 */
public interface CommonMapper {

    List<InformationSchema> selectRelationTable(String tableName);

    int selectRelationRecordCount(@Param("tableName") String tableName, @Param("columnName") String columnName, @Param("fkValue") Integer fkValue);

}
