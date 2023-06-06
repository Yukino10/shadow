package com.shadow.web;

import com.shadow.web.model.product.ProductVersion;
import com.shadow.web.model.result.Result;
import com.shadow.web.service.ProductVersionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WebApplicationTests {

    @Autowired
    private ProductVersionService productVersionService;

    @Test
    public void getProductVersionInfo() {
        Result<List<ProductVersion>> result = productVersionService.getProductVersionInfo(5);
        List<ProductVersion> list = result.value();
        list.forEach(v -> log.error(v.getVersionNo()));
    }

    @Test
    public void insert() {
        Result<Integer> re = productVersionService.insert(5, "3.0", "ddd", "ddd");
        log.error("=======> id=" + re.value());
    }

}
