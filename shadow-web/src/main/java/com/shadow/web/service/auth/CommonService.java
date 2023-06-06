package com.shadow.web.service.auth;

import com.shadow.web.mapper.auth.CommonMapper;
import com.shadow.web.model.auth.InformationSchema;
import com.shadow.web.model.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/7 15:56
 * @Description:
 */
@Service
public class CommonService {
    private Logger log = LoggerFactory.getLogger(CommonService.class);

    private final static Map<String, String> TABLES;

    static {
        TABLES = new ConcurrentHashMap<>();
        TABLES.put("cm_role", "cm_role");
        TABLES.put("cm_role_permission","cm_role_permission");
        TABLES.put("am_community","am_community");
        TABLES.put("am_block","am_block");
    }

    @Resource
    private CommonMapper mapper;


    public Result<String> verifyRelationData(String tableName, Integer id) {
        return verifyRelationData(tableName, id, null);
    }

    /**
     * 根据表名查出关联表，再通过查询关联表是否有关联数据
     * 有关联数据，返回成功，返回表名； 没有关联数据，返回成功，返回null
     * 出错，返回失败，返回错误信息
     * @param tableName 表名
     * @param id        删除记录的主键
     * @param filter    不需要被校验的表
     * @return
     */
    public Result<String> verifyRelationData(String tableName, Integer id, Set<String> filter) {
        String relTableName = TABLES.get(tableName);
        if (null == relTableName) {
            return Result.returnError("TABLE_NOT_FOUND");
        }
        try {
            List<InformationSchema> informationSchemas = mapper.selectRelationTable(tableName);
            for (InformationSchema info : informationSchemas) {
                String checkTableName = info.getTableName();
                if (null == filter || !filter.contains(checkTableName)) {
                    int i = mapper.selectRelationRecordCount(info.getTableName(), info.getColumnName(), id);
                    if (0 < i) {
                        return Result.returnSuccess(info.getTableName());
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("删除时校验关联数据异常%s", e));
            return Result.returnError("删除时校验关联数据异常");
        }
        return Result.returnSuccess();
    }


}
