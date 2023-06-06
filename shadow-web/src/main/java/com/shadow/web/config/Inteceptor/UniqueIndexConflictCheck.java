package com.shadow.web.config.Inteceptor;

import com.shadow.web.mapper.core.LogicDeleteMapper;
import com.shadow.web.model.core.TableUniqueIndex;
import com.shadow.web.model.core.UniqueIndexField;
import com.shadow.web.model.core.UniqueIndexInfo;
import com.shadow.web.model.core.UniqueIndexInfo.IndexFieldInfo;
import com.shadow.web.model.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 逻辑删除的时候，检查是否存在唯一索引冲突，
 * 存在的话 ，返回已删除的存在冲突的记录的deleted的最大值+1
 * @author Administrator
 *
 */
@Component
public class UniqueIndexConflictCheck {
	
	private static Logger log = LoggerFactory.getLogger(UniqueIndexConflictCheck.class);
	
	@Resource
	LogicDeleteMapper mapper;
	
	/**
	 * 根据表名查询配置的唯一索引信息
	 * @param tableName
	 * @return
	 */
	private Result<List<TableUniqueIndex>> queryIndexInfo(String tableName) {
		try {
		    List<TableUniqueIndex> indexes = mapper.selectIndexInfo(tableName);
		    return Result.returnSuccess(indexes);
		}catch(Exception e) {
			log.error("queryIndexInfo error:" + e);
			return Result.returnError("查询唯一索引信息失败" + e);
		}		
	}
	
	/**
	 * 把查询到的索引信息转化成查询索引字段值用的参数类
	 * 本来想把索引查询结果 用作 入参类，试验后发现不行：
	 * TODO：
	 * @param index
	 * @param id
	 * @return
	 */
	private UniqueIndexInfo prepareQueryParam(TableUniqueIndex index, Integer id) {
		UniqueIndexInfo info = new UniqueIndexInfo();
		info.setId(id);
		info.setTableName(index.getTableName());
		List<IndexFieldInfo> fieldInfoList = new ArrayList<>();
		for(UniqueIndexField field : index.getFieldList()) {
			IndexFieldInfo fieldInfo = new IndexFieldInfo();
			fieldInfo.setFieldName(field.getFieldName());
			fieldInfo.setFieldType(field.getFieldType());
			fieldInfoList.add(fieldInfo);
		}
		info.setFieldInfos(fieldInfoList);
		return info;
	}
	
	/**
	 * 查询索引字段的值
	 * @param index
	 * @return
	 */
	private Result<Map<String, Object>> queryFieldVal(UniqueIndexInfo index) {
		try {
		    Map<String, Object> valueInfo = mapper.queryFieldVal(index);
		    return Result.returnSuccess(valueInfo);
		}catch(Exception e) {
			log.error("queryFieldVal error:" + e);
			return Result.returnError("查询唯一索引字段值失败" + e);
		}
	}
	
	/**
	 * 把索引字段的值填充到入参
	 * @param index
	 * @param fieldValueInfo
	 */
	private void prepareParam(UniqueIndexInfo index, Map<String, Object> fieldValueInfo) {
		for(IndexFieldInfo fieldInfo : index.getFieldInfos()) {
			Object fieldValue = fieldValueInfo.get(fieldInfo.getFieldName());
			fieldInfo.setFieldValue(fieldValue);
		}		
	}
	
	/**
	 * 查询有冲突的记录的deleted的最大值
	 * @param index
	 * @return
	 */
	private Result<Integer> selectMaxDeleted(UniqueIndexInfo index) {
		try {
		    return Result.returnSuccess(mapper.selectMaxDeleted(index));
		}catch(Exception e) {
			log.error("selectMaxDeleted error:" + e);
			return Result.returnError("查询最大deleted值败" + e);
		}
	}
	
	private Result<Integer> checkIndex(TableUniqueIndex index, Integer id) {
		/** step1: 检查索引字段是否为空，空则不存在冲突*/
		List<UniqueIndexField> fieldInfos = index.getFieldList();
		if(null == fieldInfos || fieldInfos.isEmpty()) {
			return Result.returnSuccess(0);
		}
		/** step1.1: 组装入参 **/
		UniqueIndexInfo info = prepareQueryParam(index,id);
		/** step2:根据id查询索引字段的值*/
		info.setId(id);
		Result<Map<String, Object>> findFieldRet = queryFieldVal(info);
		if(!findFieldRet.success()) {
			log.error("checkIndex failed:" + findFieldRet.msg());
			return Result.returnError("checkIndex failed:" + findFieldRet.msg());
		}
		Map<String, Object> fieldValueInfo = findFieldRet.value();
		//查询失败
		if(null == fieldValueInfo) {
			return Result.returnError("查询唯一索引字段值失败，id不存在或者唯一索引数据有误，字段不存在");
		}
		/** step3: 把查到的值设置到参数上*/
		prepareParam(info, fieldValueInfo);
		/** step3: 查询最大deleted*/
		Result<Integer> findMaxDeletedRet = selectMaxDeleted(info);
		if(!findMaxDeletedRet.success()) {
			log.error("checkIndex failed:" + findMaxDeletedRet.msg());
			return Result.returnError("checkIndex failed:" + findMaxDeletedRet.msg());
		}
		Integer maxDeleted = findMaxDeletedRet.value();
		if(null == maxDeleted) {
			//查不到说明不存在冲突
			return Result.returnSuccess(0);
		}
		return Result.returnSuccess(maxDeleted);		
	}
	
	public Result<Integer> check(String tableName, Integer id) {
		Integer maxDeleted = 0;
		/** step1: 根据表名查询 配置的唯一索引信息*/
		Result<List<TableUniqueIndex>> findRet = queryIndexInfo(tableName);
		if(!findRet.success()) {
			log.error("check table[" + tableName + "] failed:" + findRet.msg());
			return Result.returnError("检查[" + tableName + "]表的唯一索引冲突失败：" + findRet.msg());
		}
		/** step 2:检查查询结果*/
		List<TableUniqueIndex> indexes = findRet.value();
		if(null == indexes || indexes.isEmpty()) {
			//查不到唯一索引信息，说明不可能存在唯一索引冲突，返回deleted=1
			return Result.returnSuccess(1);
		}
		/** step3: 遍历唯一索引，检查每个索引的冲突情况，确定最大deleted*/
		for(TableUniqueIndex index : indexes) {
			Result<Integer> ret = checkIndex(index, id);
			if(!ret.success()) {
				log.error("check table[" + tableName + "] indexName[" + index.getIndexName() + "] failed:" + ret.msg());
				return Result.returnError("检查[" + tableName + "]表的唯一索引[" + index.getIndexName() + "]唯一索引冲突失败：" + ret.msg());
			}else {
				if(maxDeleted < (Integer)ret.value()) {
					maxDeleted = (Integer)ret.value();
				}
			}
		}
		return Result.returnSuccess(maxDeleted + 1);		 
	}
	
	

}
