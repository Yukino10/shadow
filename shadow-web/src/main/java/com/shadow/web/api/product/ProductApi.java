package com.shadow.web.api.product;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.params.PackageValidateParams;
import com.shadow.web.model.params.ProductParams;
import com.shadow.web.model.product.Product;
import com.shadow.web.model.product.ProductVersion;
import com.shadow.web.model.result.ApiResult;
import com.shadow.web.model.result.ProductInfo;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.FileService;
import com.shadow.web.service.ProductService;
import com.shadow.web.service.ProductVersionService;
import com.shadow.web.utils.ParamUtils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 产品信息操作API
 *
 * @author wangzhendong
 * @since 2019/10/22 16:04
 */
@Controller
@Slf4j
@RequestMapping("/admin/product")
public class ProductApi {

    @Autowired
    private FileService fileService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductVersionService productVersionService;

    /**
     * 新增产品定义
     *
     * @author wangzhendong
     * @since 2019/10/22 16:20
     */
    @CrossOrigin
    @PostMapping("create")
    public ApiResult createProduct(
            @RequestParam("deviceId") Integer deviceId,
            @RequestParam("name") String name,
            @RequestParam("encryption") String encryption,
            @RequestParam("operateSystem") String operateSystem,
            @RequestParam("protocolList") List<String> protocolList,
            @RequestParam("serverList") List<String> serverList,
            @RequestParam("description") String description) {

        //获取参数信息，新家产品信息
        /*String file_url = null;
        try {
            file_url = fileService.saveFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("存储文件错误！");
            return ApiResult.returnError(1, "存储文件错误！");
        }*/
        Product product = new Product(name, deviceId, encryption,operateSystem,protocolList.size(), serverList.size(), description);
        Result<?> ret = productService.insertProduct(product, protocolList, serverList);
        if (!ret.success()) {
            log.error(ret.msg());
            return ApiResult.returnError(1, "存储文件错误！");
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 按条件查询产品信息
     */
    @PostMapping("tableList")
    public ApiResult searchProductList(@RequestBody Map<String, Object> params) {
        ListParamValidResult<Map<String, Object>> validRet = productService.searchProductParamValid(params);
        Result<PageInfo<ProductInfo>> result = productService.searchProductListByExample(validRet);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess(ParamUtil.genReturnData(result.value()));
    }

    /**
     * 删除产品信息
     *
     * @param id 产品id
     */
    @PostMapping("delete")
    public ApiResult deleteProduct(@RequestBody int id) {
        Result<?> ret = productService.deleteProduct(id);
        if (!ret.success()) {
            log.error(ret.msg());
            return ApiResult.returnError(1, ret.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * @return 批量删除用户
     * @author 10413
     * @since 2021-04-10 16:48
     */
    @PostMapping("deleteProduct")
    public ApiResult deleteProducts(@RequestBody Integer[] idList) {
        Result<?> result = productService.batchDeleteProduct(idList);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 修改产品信息
     *
     * @param params 产品信息
     */
    @PostMapping("update")
    public ApiResult updateProduct(@RequestBody ProductParams params) {
        Product product = params.getProduct();
        Result<?> ret = productService.updateProduct(product, params.getProtocolList(), params.getServerList());
        if (!ret.success()) {
            log.error(ret.msg());
            return ApiResult.returnError(1, "更新错误！");
        }
        return ApiResult.returnSuccess("");
    }

    /**
     * 产品是否更新
     *
     * @param id 产品id
     * @return 是否更新
     * @author szh
     * @since 2021/4/4 11:00
     */
    @PostMapping("isUpdated")
    public ApiResult isUpdated(@RequestBody int id) {
        Result<Product> re = productService.selectById(id);
        if (re.success()) {
            return ApiResult.returnSuccess(String.valueOf(re.value().getUpdated()));
        } else {
            return ApiResult.returnError(1, re.msg());
        }
    }

    /**
     * 验证打包信息
     *
     * @param params 打包信息
     * @return 版本id
     * @author szh
     * @since 2021/4/4 18:33
     */
    @PostMapping("/package/validate")
    public ApiResult packageValidate(@RequestBody PackageValidateParams params) {
        /* 1. 查询产品信息 */
        Result<ProductInfo> productInfoResult = productService.selectProductInfoById(params.getProductId());
        if (!productInfoResult.success()) {
            log.warn("产品打包校验失败：产品不存在，productId={}", params.getProductId());
            return ApiResult.returnError(1, "产品打包校验失败：产品不存在");
        }
        ProductInfo productInfo = productInfoResult.value();

        /* 2. 校验版本 */
        if (productInfo.isUpdated() && StringUtils.isBlank(params.getVersionNo())) {
            log.warn("产品打包校验失败：产品已更新但未传入新的版本号，productId={}", params.getProductId());
            return ApiResult.returnError(1, "产品打包校验失败：未填写新的版本号");
        }

        /* 3. 未更新，返回最新版本 */
        if (!productInfo.isUpdated()) {
            Result<List<ProductVersion>> productVersionRe = productVersionService.getProductVersionInfo(params.getProductId());
            List<ProductVersion> productVersionList = productVersionRe.value();
            if (CollectionUtils.isEmpty(productVersionList)) {
                log.error("产品打包校验失败：产品未更新且没有历史版本信息，productId={}", params.getProductId());
                return ApiResult.returnError(1, "产品打包校验失败：没有历史版本信息");
            }

            ProductVersion productVersion = productVersionList.get(0);
            return ApiResult.returnSuccess(String.valueOf(productVersion.getId()));
        }

        /* 4. 已更新，校验并创建xml */
        Result<String> xmlPathRe = productService.buildXmlFile(productInfo);
        if (!xmlPathRe.success()) {
            return ApiResult.returnError(1, "产品打包校验失败：" + xmlPathRe.msg());
        }
        String xmlPath = xmlPathRe.value();

        /* 5. 创建版本信息 */
        Result<Integer> versionRe = productVersionService.insert(params.getProductId(), params.getVersionNo(), xmlPath, params.getRemark());

        /* 6. 重置更新状态 */
        productService.resetUpdated(params.getProductId());

        return ApiResult.returnSuccess(String.valueOf(versionRe.value()));
    }

    /**
     * 产品打包
     *
     * @param versionId 产品版本id
     * @author szh
     * @since 2019/11/10 17:09
     */
    @GetMapping("/package/download")
    public void packageProduct(@RequestParam int versionId,
                               @RequestParam int userId,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        /* 1. 查找版本信息 */
        Result<ProductVersion> productVersionRe = productVersionService.selectById(versionId);
        if (!productVersionRe.success()) {
            return;
        }
        ProductVersion productVersion = productVersionRe.value();

        /* 2. 获取人员所属服务版本 */
        Result<VersionEnum> versionRes = productVersionService.getVersionByUser(userId);
        VersionEnum version = versionRes.value();

        /* 3. 打包 */
        productService.packageProduct(productVersion, version, request, response);
    }

    /**
     * 查询版本信息
     *
     * @param productId 产品id
     * @return 版本信息列表
     * @author szh
     * @since 2021/4/11 20:00
     */
    @PostMapping("/version")
    public ApiResult getProductVersion(@RequestBody int productId) {
        Result<List<ProductVersion>> productVersionRe = productVersionService.getProductVersionInfo(productId);
        if (!productVersionRe.success()) {
            return ApiResult.returnError(1, productVersionRe.msg());
        }

        return ApiResult.returnSuccess(JSONObject.toJSONString(productVersionRe.value()));
    }

    /**
     * 删除版本信息
     *
     * @param versionId 版本id
     * @author szh
     * @since 2021/4/11 20:00
     */
    @PostMapping("/version/delete")
    public ApiResult deleteVersion(@RequestBody int versionId) {
        Result<?> result = productVersionService.delete(versionId);
        if (!result.success()) {
            return ApiResult.returnError(1, result.msg());
        }

        return ApiResult.returnSuccess();
    }

    /**
     * -----------------------------------------------------测试--------------------------------------------------
     **/
    @GetMapping("/test/file")
    public void saveFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.saveFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
