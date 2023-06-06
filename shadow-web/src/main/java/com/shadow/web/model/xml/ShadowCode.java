package com.shadow.web.model.xml;

import lombok.Getter;

import java.util.Map;

/**
 * @ClassName ShadowCode
 * @Description 影子相关生成代码
 * @Date 2019/6/12 21:12
 * @author szh
 **/
@Getter
public class ShadowCode {

    /** 实体代码 */
    private Map<String, String> entityCode;
    /** 数据库映射代码 */
    private Map<String, String> repositoryCode;
    /** 初始化类代码 */
    private Map<String, String> initCode;
    /** 实体名称代码 */
    private Map<String, String> entityNameCode;
    /** 安卓实体代码 */
    private Map<String, String> androidEntityCode;
    /** 安卓实体名称代码 */
    private Map<String, String> androidEntityNameCode;

    public ShadowCode() {}

    public ShadowCode(Map<String, String> entityCode, Map<String, String> repositoryCode,
                      Map<String, String> initCode, Map<String, String> entityNameCode,
                      Map<String, String> androidEntityCode, Map<String, String> androidEntityNameCode) {
        this.entityCode = entityCode;
        this.repositoryCode = repositoryCode;
        this.initCode = initCode;
        this.entityNameCode = entityNameCode;
        this.androidEntityCode = androidEntityCode;
        this.androidEntityNameCode = androidEntityNameCode;
    }

}
