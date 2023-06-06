package com.shadow.web.model.params;

import lombok.Data;

import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/9 20:56
 * @Description:
 */
@Data
public class Tree {

    private Integer id;
    private String title;
    private String name;
    private String key;
    private List<Tree> children;
}
