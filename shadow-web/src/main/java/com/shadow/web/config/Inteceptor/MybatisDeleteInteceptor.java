package com.shadow.web.config.Inteceptor;

import com.shadow.web.model.result.Result;
import com.shadow.web.utils.ApplicationContextHolder;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 拦截delete操作，改成逻辑删除
 * TODO:先实现简单的把delete改成update set deleted=1的操作
 *      唯一索引冲突后续再处理
 *      可以建表保存下表的唯一索引信息
 * @author wt
 *
 */
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisDeleteInteceptor implements Interceptor {
	private static Logger log = LoggerFactory.getLogger(MybatisDeleteInteceptor.class);
	private static String DEFAULT_DELETE_SQL_ID = ".*\\.delete.*"; // 需要拦截的ID(正则匹配)
		
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取被代理的Executor
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        // 获取MappedStatement
        MappedStatement mappedStatement = (MappedStatement) args[0];
        // 获取执行的参数
        Object parameterObject = args[1];
        // 如果不是delete的sql，按原来的逻辑进行处理
        if (!mappedStatement.getId().matches(DEFAULT_DELETE_SQL_ID) || !(parameterObject instanceof Integer)) {
            return invocation.proceed();
        }
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        DeleteSqlTransformer transformer = new DeleteSqlTransformer(boundSql.getSql());
        transformer.doTransform();
        if(transformer.failed()) {
        	log.error("intercept failed: can not transform delete sql:" + boundSql.getSql() + " error:" + transformer.getErrMsg());
        	throw new PersistenceException("MybatisDeleteInteceptor intecept failed : can not transform delete sql:" + boundSql.getSql() + " error:" + transformer.getErrMsg());
        }
        //UniqueIndexConflictCheck conflictCheck = ApplicationContextHolder.getBean(UniqueIndexConflictCheck.class);
        String tableName = transformer.getTableName();
        UniqueIndexConflictCheck conflictCheck = ApplicationContextHolder.getBean(UniqueIndexConflictCheck.class);
        //检查是否存在唯一索引冲突，存在则返回已删除的冲突的记录的deleted最大值+1
        Result ret = conflictCheck.check(tableName, (Integer)parameterObject);
        if(!ret.success()) {
        	log.error("intercept failed: check unique index conflict failed:" + ret.msg());
        	throw new PersistenceException("MybatisDeleteInteceptor intecept failed : check unique index conflict failed:" + ret.msg());
        }
        Integer deleted = (Integer)ret.value();
        transformer.setDeleted(deleted);
        String updateSql = transformer.getUpdateSql();
        
        MappedStatement newMappedStatement = buildNewMappedStatement(updateSql, mappedStatement, boundSql);
        int result = executor.update(newMappedStatement, parameterObject);
        return result;
    }
    
    private MappedStatement buildNewMappedStatement(String updateSql, MappedStatement old, BoundSql oldBoundSql) {
    	Configuration configuration = old.getConfiguration();
    	StaticSqlSource newSqlSource = new StaticSqlSource(configuration, updateSql, oldBoundSql.getParameterMappings());
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, old.getId().replace("delete", "update"), newSqlSource, old.getSqlCommandType())
                .resource(old.getResource())
                .fetchSize(old.getFetchSize())
                .timeout(old.getTimeout())
                .statementType(old.getStatementType())
                .keyGenerator(old.getKeyGenerator())
                .keyProperty(arrayToString(old.getKeyProperties()))
                .keyColumn(arrayToString(old.getKeyColumns()))
                .databaseId(old.getDatabaseId())
                .lang(old.getLang())
                .resultOrdered(old.isResultOrdered())
                .resultSets(arrayToString(old.getResultSets()))
                .resultMaps(old.getResultMaps())
                .resultSetType(old.getResultSetType())
                .flushCacheRequired(old.isFlushCacheRequired())
                .useCache(old.isUseCache())
                .cache(old.getCache())
                .parameterMap(old.getParameterMap());

        return statementBuilder.build();
    }
    
    @Override
    public void setProperties(Properties properties) {
    	//nothing
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    private String arrayToString(String[] array) {
    	if(null == array) {
    		return null;
    	}
    	StringBuilder sb = new StringBuilder();
    	for(String str : array) {
    		if(sb.length() == 0) {
    			sb.append(str);
    		}else {
    			sb.append(",").append(str);
    		}
    	}
    	return sb.toString();
    }
    
    /**
     * 把 delete from tableX where XXX = ?
     * 转换成update tableX set deleted = 1 where XXX = ?
     * TODO:暂时简单处理，逻辑可能不严谨, 不支持alias
     * @author wt
     *
     */
	private class DeleteSqlTransformer {
		private String deleteSql;
		private String errMsg;
		private String tableName;
		private Integer deleted;
		private String cond;

		public DeleteSqlTransformer(String deleteSql) {
			this.deleteSql = deleteSql;
			this.deleted = 0;
		}

		public void doTransform() {
			int deleteIdx = getIdx("delete", 0);
			if(failed()) {
				return;
			}
            int fromIdx = getIdx("from", deleteIdx + 6);
			if(failed()) {
				return;
			}
			int tableNameIdx = skipWS(fromIdx + 4);
			if(-1 == tableNameIdx) {
				errMsg = "not valid delete sql:" + deleteSql;
				return;
			}
    		String tableName = null;
    		int offset = tableNameIdx;
    		for(; offset < deleteSql.length(); ++offset) {
    			if(deleteSql.charAt(offset) < 0x20) {
    				tableName = deleteSql.substring(tableNameIdx, offset);
    				break;
    			}
    		}
    		if(null == tableName) {
    			errMsg = "not valid delete sql:" + deleteSql;
    			return;
    		}
    		this.tableName = tableName;
    		this.cond = deleteSql.substring(offset);		         
    	}
		
		public String getUpdateSql() {
			if(failed()) {
				return null;
			}
			StringBuilder updateSql = new StringBuilder();
			updateSql.append("update ")
			  .append(this.tableName)
			  .append(" set deleted = ")
			  .append(this.deleted)
			  .append(" ")
			  .append(this.cond);
			return updateSql.toString();
		}
		
		public String getTableName() {
			if(failed()) {
				return null;
			}
			return this.tableName;
		}
		
		public void setDeleted(Integer deleted) {
			if(null != deleted && deleted > 0) {
				this.deleted = deleted;
			}
		}
		
		public String getErrMsg() {
			return errMsg;
		}
		
		private int getIdx(String name, int start) {
			int offset = skipWS(start);
            if(-1 == offset ) {
            	errMsg = "not valid delete sql:" + deleteSql;
				return -1;
			}
			if(matchName(name, offset)) {
				return offset;
			}else {
				errMsg = "not valid delete sql:" + deleteSql;
				return -1;
			}
		}
		
		private boolean matchName(String name, int start) {
			int len = name.length();
			int end = start + len;
			return name.toUpperCase().equals(deleteSql.substring(start, end).toUpperCase()) && (deleteSql.length() == end || deleteSql.charAt(end) == 0x20 );
		}
		
		private int skipWS(int start) {
			for (int i = start; i < deleteSql.length(); i++) {
				if (deleteSql.charAt(i) > 0x20) {
					return i;
				}
			}
			return -1;
		}
		
		public boolean failed() {
			return null != errMsg;
		}

	}

}
