package com.shadow.web.model.params;

import com.shadow.web.model.product.Product;
import lombok.Data;

import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2019/10/31 16:13
 * @Description:
 */
@Data
public class ProductParams {
    private Integer id;

    private String name;

    private List<String> oldProtocolList;

    private List<String> oldServerList;

    private List<String> protocolList;

    private List<String> serverList;

    private String description;

    public Product getProduct(){
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setProtocolSize(this.protocolList.size());
        product.setServerSize(this.serverList.size());
        product.setDescription(this.description);
        return product;
    }

}
