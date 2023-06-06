package com.shadow.web.service;

import com.shadow.web.mapper.auth.AdminRoleMapper;
import com.shadow.web.mapper.product.ProductVersionMapper;
import com.shadow.web.model.auth.Role;
import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.product.ProductVersion;
import com.shadow.web.model.product.ProductVersionExample;
import com.shadow.web.model.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 产品版本服务
 *
 * @author szh
 * @since 2021/4/4 0:09
 **/
@Service
@Slf4j
public class ProductVersionService {

    @Resource
    private ProductVersionMapper productVersionMapper;
    @Resource
    private AdminRoleMapper adminRoleMapper;

    /**
     * 查询产品相关版本信息
     * 按插入时间倒序排列
     *
     * @param productId 产品id
     * @return 版本信息
     * @author szh
     * @since 2021/4/4 0:19
     */
    public Result<List<ProductVersion>> getProductVersionInfo(int productId) {
        ProductVersionExample example = new ProductVersionExample();
        example.createCriteria().andProductIdEqualTo(productId).andDeletedEqualTo(0);
        example.setOrderByClause("create_time desc");
        List<ProductVersion> list = productVersionMapper.selectByExample(example);
        return Result.returnSuccess(list);
    }

    /**
     * 插入版本信息
     *
     * @param productId 产品id
     * @param versionNo 版本号
     * @param fileUrl 文件路径
     * @param remark 备注
     * @return 版本id
     * @author szh
     * @since 2021/4/4 21:33
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> insert(int productId, String versionNo, String fileUrl, String remark) {
        ProductVersion productVersion = new ProductVersion();
        productVersion.setProductId(productId);
        productVersion.setVersionNo(versionNo);
        productVersion.setFileUrl(fileUrl);
        productVersion.setRemark(remark);
        productVersionMapper.insertSelective(productVersion);

        return Result.returnSuccess(productVersion.getId());
    }

    /**
     * 使用id查询产品版本
     *
     * @param versionId 版本id
     * @return 产品版本
     * @author szh
     * @since 2021/4/4 22:11
     */
    public Result<ProductVersion> selectById(int versionId) {
        ProductVersion version = productVersionMapper.selectByPrimaryKey(versionId);
        if (version == null) {
            log.error("查询产品版本失败：产品版本不存在，id={}", versionId);
            return Result.returnError("产品版本不存在");
        }
        return Result.returnSuccess(version);
    }

    /**
     * 删除版本信息
     *
     * @param versionId 版本id
     * @author szh
     * @since 2021/4/11 19:58
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> delete(int versionId) {
        ProductVersion version = productVersionMapper.selectByPrimaryKey(versionId);
        if (version == null) {
            log.error("查询产品版本失败：产品版本不存在，id={}", versionId);
            return Result.returnError("产品版本不存在");
        }
        productVersionMapper.deleteByPrimaryKey(versionId);
        return Result.returnSuccess();
    }

    /**
     * 获取用户对应版本号
     *
     * @param userId 用户id
     * @return 版本号
     * @author szh
     * @since 2021/4/27 12:43
     */
    public Result<VersionEnum> getVersionByUser(int userId) {
        List<Role> roles = adminRoleMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return Result.returnSuccess(VersionEnum.A);
        }

        Role role = roles.get(0);
        VersionEnum version = VersionEnum.getEnumByLevel(role.getCoLevel());
        return Result.returnSuccess(version);
    }

}
