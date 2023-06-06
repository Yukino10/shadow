package com.shadow.web.mapper.core;

import com.shadow.web.model.core.TableUniqueIndex;
import com.shadow.web.model.core.UniqueIndexInfo;

import java.util.List;
import java.util.Map;


/**
 * 逻辑删除拦截器中,使用此Mapper处理唯一索引冲突的问题
 * 1、先根据id查询deleted字段之外的索引字段值
 * 2、然后根据索引字段值查询对应表记录中deleted的最大值
 * 3、把最大值+1作为逻辑删除时要set的deleted的值
 * @author Administrator
 *
 */
public interface LogicDeleteMapper {
	/**
	 * 根据id查询唯一索引字段的值
	 * @param info
	 * @return
	 */
	Map<String, Object> queryFieldVal(UniqueIndexInfo info);
	
	/**
	 * 根据索引字段的值，查询已逻辑删除的记录中满足条件的记录deleted的最大值
	 * @param info
	 * @return
	 */
	Integer selectMaxDeleted(UniqueIndexInfo info);
	
	
	/**
	 * 根据表名查询配置的唯一索引信息
	 * @param tableName
	 * @return
	 */
    List<TableUniqueIndex> selectIndexInfo(String tableName);
	//List<UniqueIndexInfo> selectIndexInfo(String tableName);

}
