package com.shadow.web.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;
import com.shadow.web.mapper.device.DeviceMapper;
import com.shadow.web.mapper.product.ProductListMapper;
import com.shadow.web.mapper.product.ProductMapper;
import com.shadow.web.mapper.product.ProductProtocolMapper;
import com.shadow.web.mapper.product.ProductServerMapper;
import com.shadow.web.model.authority.JwtUser;
import com.shadow.web.model.device.Device;
import com.shadow.web.model.device.DeviceProp;
import com.shadow.web.model.device.DevicePropChild;
import com.shadow.web.model.enums.DevicePropEnum;
import com.shadow.web.model.enums.VersionEnum;
import com.shadow.web.model.params.ListParamValidResult;
import com.shadow.web.model.product.*;
import com.shadow.web.model.result.ProductInfo;
import com.shadow.web.model.result.Result;
import com.cislc.shadow.utils.enums.Encryption;
import com.cislc.shadow.utils.enums.Protocol;
import com.shadow.web.model.xml.FieldDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

import static com.shadow.web.utils.ParamUtils.Constants.MSG_DATABASE_OPERATION_ERROR;
import static com.shadow.web.utils.ParamUtils.Constants.MSG_QUERY_FAILED;

/**
 * @author wangzhendong
 * @since 2019/10/28 12:33
 */
@Slf4j
@Service
public class ProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductListMapper productListMapper;
    @Resource
    private ProductProtocolMapper productProtocolMapper;
    @Resource
    private ProductServerMapper productServerMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private PackageService packageService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private FileService fileService;

    //增
    @Transactional(rollbackFor = Exception.class)
    public Result<?> insertProduct(Product product, List<String> protocolList, List<String> serverList) {
        int ret = productMapper.insertSelective(product);
        if (ret == -1) {
            return Result.returnError("新增产品失败");
        }
        for (String protocol : protocolList) {
            ProductProtocol productProtocol = new ProductProtocol(product.getId(), protocol);
            int protocolRet = productProtocolMapper.insert(productProtocol);
            if (protocolRet == -1) {
                return Result.returnError("新增产品失败");
            }
        }
        for (String server : serverList) {
            ProductServer productServer = new ProductServer(product.getId(), server);
            int serverRet = productServerMapper.insert(productServer);
            if (serverRet == -1) {
                return Result.returnError("新增产品失败");
            }
        }
        return Result.returnSuccess();
    }

    //删
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteProduct(Integer id) {
        //删除中间表的协议
        ProductProtocolExample productProtocolExample = new ProductProtocolExample();
        productProtocolExample.createCriteria().andProductIdEqualTo(id);
        int protocolRet = productProtocolMapper.deleteByExample(productProtocolExample);
        //公共服务信息
        ProductServerExample productServerExample = new ProductServerExample();
        productServerExample.createCriteria().andProductIdEqualTo(id);
        int serverRet = productServerMapper.deleteByExample(productServerExample);
        //
        int ret = productMapper.deleteByPrimaryKey(id);
        if (ret == -1 || protocolRet == -1 || serverRet == -1) {
            return Result.returnError("删除产品失败");
        }
        return Result.returnSuccess();
    }

    /**
     * @return 批量删除用户
     * @author 10413
     * @since 2021-04-10 16:49
     */
    @Transactional
    public Result<?> batchDeleteProduct(Integer[] idList) {
        boolean deleteSuccess = true;
        String errMsg = "";
        for (int id : idList) {
            Result<?> result = deleteProduct(id);
            if (!result.success()) {
                deleteSuccess = false;
                errMsg = result.msg();
                break;
            }
        }
        if (deleteSuccess) {
            return Result.returnSuccess();
        } else {
            return Result.returnError(errMsg);
        }
    }

    //改
    @Transactional(rollbackFor = Exception.class)
    public Result<?> updateProduct(Product product, List<String> protocolList, List<String> serverList) {
        //删
        ProductProtocolExample productProtocolExample = new ProductProtocolExample();
        productProtocolExample.createCriteria().andProductIdEqualTo(product.getId());
        productProtocolMapper.deleteByExample(productProtocolExample);
        //公共服务信息
        ProductServerExample productServerExample = new ProductServerExample();
        productServerExample.createCriteria().andProductIdEqualTo(product.getId());
        productServerMapper.deleteByExample(productServerExample);
        //增
        for (String protocol : protocolList) {
            ProductProtocol productProtocol = new ProductProtocol(product.getId(), protocol);
            productProtocolMapper.insert(productProtocol);
        }
        for (String server : serverList) {
            ProductServer productServer = new ProductServer(product.getId(), server);
            productServerMapper.insert(productServer);
        }
        //改
        product.setUpdated(1);
        int ret = productMapper.updateByPrimaryKeySelective(product);
        if (ret == -1) {
            return Result.returnError("更新产品失败");
        }
        return Result.returnSuccess();
    }

    //查
    public Result<List<ProductInfo>> selectProduct() {
        return Result.returnSuccess(productListMapper.select(null));
    }

    /**
     * 查询产品，参数校验，初始化分页参数
     * @author 10413
     * @since 2021-04-22 14:24
     */
    public ListParamValidResult<Map<String, Object>> searchProductParamValid(Map<String, Object> input) {
        ListParamValidResult<Map<String, Object>> ret = new ListParamValidResult<>(input);
        Map<String, Object> params = new HashMap<>();
        // 产品名称
        String name = (String) input.get("productName");
        if (!StringUtils.isEmpty(name)) {
            params.put("name", name);
        }
        // 当前登录用户的id
        JwtUser userDetails = (JwtUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Integer userId = userDetails.getId();
        String userName = userDetails.getUsername();
        if(!userName.equals("admin")){
            params.put("userId", userId);
        }
        ret.setExample(params);
        return ret;
    }

    /**
     * @return 查询产品列表
     * @author 10413
     * @since 2021-04-10 14:31
     */
    public Result<PageInfo<ProductInfo>> searchProductListByExample(ListParamValidResult<Map<String, Object>> validRet) {
        PageHelper.startPage(validRet.getPageNum(), validRet.getPageSize(), validRet.getOrderBy());
        try {
            List<ProductInfo> infoList = productListMapper.searchProductByExample(validRet.getExample());
            PageInfo<ProductInfo> pageInfo = new PageInfo<>(infoList);
            return Result.returnSuccess(pageInfo);
        } catch (Exception e) {
            log.error(MSG_DATABASE_OPERATION_ERROR + "\n" + e.toString());
            return Result.returnError(MSG_QUERY_FAILED);
        }
    }
    /**
     * 使用id查询产品
     *
     * @param id 产品id
     * @return 产品
     * @author szh
     * @since 2021/4/4 10:58
     */
    public Result<Product> selectById(int id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (product != null) {
            return Result.returnSuccess(product);
        } else {
            return Result.returnError("产品不存在，id=" + id);
        }
    }

    /**
     * 使用id查询产品详细信息
     *
     * @param id 产品id
     * @return 产品
     * @author szh
     * @since 2021/4/4 10:58
     */
    public Result<ProductInfo> selectProductInfoById(int id) {
        List<ProductInfo> productInfoList = productListMapper.select(id);
        if (CollectionUtils.isEmpty(productInfoList)) {
            return Result.returnError("产品不存在，id=" + id);
        } else {
            return Result.returnSuccess(productInfoList.get(0));
        }
    }

    /**
     * 设置产品为已更新
     *
     * @param productList 产品列表
     * @author szh
     * @since 2021/4/2 10:52
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> setProductUpdated(List<Product> productList) {
        productList.forEach(product -> {
            product.setUpdated(1);
            productMapper.updateByPrimaryKeySelective(product);
        });
        return Result.returnSuccess();
    }

    /**
     * 重置更新状态
     *
     * @param productId 产品id
     * @author szh
     * @since 2021/4/11 22:10
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> resetUpdated(int productId) {
        Product product = new Product();
        product.setId(productId);
        product.setUpdated(0);
        productMapper.updateByPrimaryKeySelective(product);

        return Result.returnSuccess();
    }

    /**
     * 通过设备id查询产品
     *
     * @param deviceId 设备id
     * @return 产品
     * @author szh
     * @since 2021/4/2 13:59
     */
    public Result<List<Product>> selectByDeviceId(int deviceId) {
        ProductExample example = new ProductExample();
        example.createCriteria().andDeviceIdEqualTo(deviceId).andDeletedEqualTo(0);
        List<Product> productList = productMapper.selectByExample(example);

        return Result.returnSuccess(productList);
    }

    /**
     * 产品打包
     * @param productVersion 产品版本
     * @param version 版本
     * @author szh
     * @since 2019/11/8 9:00
     */
    public void packageProduct(ProductVersion productVersion,
                               VersionEnum version,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        // 查询产品
        Result<ProductInfo> productInfoRe = selectProductInfoById(productVersion.getProductId());
        if (!productInfoRe.success()) {
            return;
        }
        ProductInfo product = productInfoRe.value();
        // 协议
        Protocol protocol = Protocol.getByName(product.getProtocolList().get(0));
        // 加密算法
        Encryption encryption = Encryption.getEncryption(product.getEncryption());
        // jar包名
        String jarName = product.getName() + ".zip";
        // xml文件
        File xmlFile = new File(productVersion.getFileUrl());
        if (!xmlFile.exists()) {
            log.error("打包失败：xml不存在，jarName={}，protocol={}", jarName, product);
            return;
        }

        long current = System.currentTimeMillis();
        log.info("打包开始：jarName={}, protocol={}, start={}", jarName, product, current);
        packageService.packageJar(Collections.singletonList(xmlFile), protocol, encryption, jarName, version, request, response);
        log.info("打包结束：用时={}", System.currentTimeMillis() - current);
    }

    /**
     * 创建xml文件
     *
     * @param product 产品信息
     * @return xml路径
     * @author szh
     * @since 2021/4/4 20:25
     */
    public Result<String> buildXmlFile(ProductInfo product) {
        // 构建xml
        String xmlStr;
        try {
            xmlStr = buildXmlProductStr(product);
        } catch (Exception ex) {
            log.error("创建xml文件失败：{}", ex.getMessage());
            return Result.returnError(ex.getMessage());
        }
        String xmlName = product.getName() + System.currentTimeMillis() + ".xml";

        // 写入xml
        String fileUrl = fileService.saveFile(xmlStr, xmlName);
        if (StringUtils.isBlank(fileUrl)) {
            log.error("创建xml文件失败：写入文件失败");
            return Result.returnError("创建xml文件失败：写入文件失败");
        }

        return Result.returnSuccess(fileUrl);
    }

    /**
     * 创建产品xml内容
     * @param product 产品
     * @return xml内容
     */
    private String buildXmlProductStr(ProductInfo product) {
        // 查找产品关联的设备
        Device device = deviceMapper.selectByPrimaryKey(product.getDeviceId());
        // 设备关联的属性
        Result<List<DeviceProp>> propRes = this.deviceService.selectDeviceProp(product.getDeviceId());
        List<DeviceProp> deviceProps = propRes.value();

        List<FieldDetail> fieldProp = new ArrayList<>();
        List<DeviceProp> structProp = new ArrayList<>();
        List<DeviceProp> videoProp = new ArrayList<>();

        for (DeviceProp deviceProp : deviceProps) {
            if ("struct".equals(deviceProp.getConstruction())) {
                structProp.add(deviceProp);
            } else if ("video".equals(deviceProp.getConstruction())) {
                videoProp.add(deviceProp);
            } else {
                DevicePropEnum propEnum = DevicePropEnum.getEnum(deviceProp.getConstruction());
                fieldProp.add(new FieldDetail(propEnum.getXmlConstruction(), deviceProp.getStructName(), deviceProp.getName()));
            }
        }

        // 构建xml
        validateEn("产品名称", product.getName());
        validateEn("物模型标识符", device.getDeviceTag());

        String tableName = this.getTableName(device.getDeviceTag());
        String deviceName = device.getDeviceName();
        StringBuffer xmlSb = new StringBuffer();
        xmlSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<classes>\n").append("\t<class name=\"")
                .append(product.getName())
                .append("\" table=\"")
                .append(tableName)
                .append("\" device=\"true\"");
        if (StringUtils.isNotBlank(deviceName)) {
            xmlSb.append(" comment=\"")
                    .append(deviceName)
                    .append("\"");
        }
        xmlSb.append(">\n");

        // 普通属性
        buildXmlProps(xmlSb, fieldProp, structProp, videoProp, tableName);
        xmlSb.append("\t</class>\n");

        // 结构体属性
        buildXmlStruct(xmlSb, structProp);

        xmlSb.append("</classes>\n");

        return xmlSb.toString();
    }

    /**
     * 构建xml属性
     */
    private void buildXmlProps(StringBuffer xmlSb, List<FieldDetail> fieldProp, List<DeviceProp> structProp, List<DeviceProp> videoProp, String tableName) {
        // 普通属性
        for (FieldDetail fieldDetail : fieldProp) {
            validateEn("物模型功能标识符", fieldDetail.getFieldName());
            xmlSb.append(
                    String.format("\t\t<field type=\"%s\" table=\"%s\" column=\"%s\"",
                            fieldDetail.getFieldType(), tableName, fieldDetail.getFieldName())
            );
            if (StringUtils.isNotBlank(fieldDetail.getFieldComment())) {
                xmlSb.append(" comment=\"")
                        .append(fieldDetail.getFieldComment())
                        .append("\"");
            }
            xmlSb.append(">")
                    .append(fieldDetail.getFieldName())
                    .append("</field>\n");
        }

        // 结构体属性
        for (DeviceProp deviceProp : structProp) {
            xmlSb.append(
                    String.format("\t\t<list type=\"%s\" table=\"%s\"",
                            getEntityName(deviceProp.getStructName()),
                            getTableName(deviceProp.getStructName()))
            );
            if (StringUtils.isNotBlank(deviceProp.getName())) {
                xmlSb.append(" comment=\"")
                        .append(deviceProp.getName())
                        .append("\"");
            }
            xmlSb.append(">")
                    .append(deviceProp.getStructName())
                    .append("List</list>\n");
        }

        // 视频属性
        for (DeviceProp deviceProp : videoProp) {
            xmlSb.append("\t\t<video");
            if (StringUtils.isNotBlank(deviceProp.getName())) {
                xmlSb.append(" comment=\"")
                        .append(deviceProp.getName())
                        .append("\"");
            }
            xmlSb.append(">").append(deviceProp.getStructName()).append("</video>\n");
        }

    }

    /**
     * 构建结构体
     */
    private void buildXmlStruct(StringBuffer xmlSb, List<DeviceProp> structProp) {
        for (DeviceProp deviceProp : structProp) {
            validateEn("物模型功能标识符", deviceProp.getStructName());

            String tableName = getTableName(deviceProp.getStructName());

            xmlSb.append("\t<class name=\"")
                    .append(this.getEntityName(deviceProp.getStructName()))
                    .append("\" table=\"")
                    .append(tableName)
                    .append("\"");
            if (StringUtils.isNotBlank(deviceProp.getName())) {
                xmlSb.append(" comment=\"")
                        .append(deviceProp.getName())
                        .append("\"");
            }
            xmlSb.append(">\n");

            Result<List<DevicePropChild>> propChildRe = deviceService.selectDevicePropChild(deviceProp.getId());
            List<DevicePropChild> propChildren = propChildRe.value();

            for (DevicePropChild propChild : propChildren) {
                validateEn("物模型" + deviceProp.getStructName() + "属性结构体标识符", propChild.getTag());

                DevicePropEnum propEnum = DevicePropEnum.getEnum(propChild.getConstruction());
                xmlSb.append(
                        String.format("\t\t<field type=\"%s\" table=\"%s\" column=\"%s\"",
                                propEnum.getXmlConstruction(), tableName, propChild.getTag())
                );
                if (StringUtils.isNotBlank(propChild.getName())) {
                    xmlSb.append(" comment=\"")
                            .append(propChild.getName())
                            .append("\"");
                }
                xmlSb.append(">")
                        .append(propChild.getTag())
                        .append("</field>\n");
            }

            xmlSb.append("\t</class>\n");
        }

    }

    /**
     * 获取实体名称
     * 首字母大写
     *
     * @param name 属性名
     * @author szh
     * @since 2021/4/4 22:22
     */
    private String getEntityName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 生成表名
     * 驼峰转下划线
     *
     * @param name 类名
     * @author szh
     * @since 2021/4/4 22:23
     */
    private String getTableName(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getEntityName(name));
    }

    /**
     * 校验英文
     *
     * @param key 字符串key
     * @param str 字符串
     * @author szh
     * @since 2021/4/4 20:43
     */
    private void validateEn(String key, String str) {
        if (!str.matches("^[a-zA-Z]+$")) {
            String info = String.format("校验失败：\"%s\"的值\"%s\"中含有非英文字符", key, str);
            throw new RuntimeException(info);
        }
    }



}
