package com.shadow.web.utils.ParamUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParamUtil {

    public static String sortOpertion(String sort) {
        String sortOption = Constants.DEFAULT_SORTER_OPTION;
        if (StringUtils.isEmpty(sort)) {
            return sortOption;
        }
        int index = sort.lastIndexOf("_");
        if (0 == index) {
            return sortOption;
        }
        String regex = sort.substring(index + 1);
        String field = sort.substring(0, index);
        if (Constants.ASCEND.equals(regex)) {
            sortOption = field + " ASC";
        } else if (Constants.DESCEND.equals(regex)) {
            sortOption = field + " DESC";
        } else {
            sortOption = Constants.DEFAULT_SORTER_OPTION;
        }
        return sortOption;
    }

    public static List<? extends Object> toFilterList(String filters) {
        String[] strs = filters.split(",");
        return Arrays.asList(strs);
    }

    public static String genReturnData(PageInfo pageInfo) {
        JSONObject data = new JSONObject();
        data.put("list", pageInfo.getList());

        JSONObject pagination = new JSONObject();
        pagination.put("total", pageInfo.getTotal());
        pagination.put("pageSize", pageInfo.getPageSize());
        pagination.put("pageNum", pageInfo.getPageNum());
        data.put("pagination", pagination);

        return data.toString();
    }

    /**
     * @return 转换String为Integer列表
     * @auther wangzhendong
     * @date 2018/12/7 15:47
     */
    public static List<Integer> idsStrToList(String ids) {
        String[] idAry = ids.split(",");
        List<Integer> idList = new ArrayList<>();
        Arrays.stream(idAry)
                .map(Integer::valueOf)
                .forEach(idList::add);
        return idList;
    }
}
