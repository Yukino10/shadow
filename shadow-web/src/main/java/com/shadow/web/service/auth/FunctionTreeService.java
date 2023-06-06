package com.shadow.web.service.auth;

import com.shadow.web.model.auth.Function;
import com.shadow.web.model.auth.FunctionExample;
import com.shadow.web.model.params.Tree;
import com.shadow.web.model.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: wangzhendong
 * @Date: 2018/12/9 20:54
 * @Description:
 */
@Service
public class FunctionTreeService {
    private static final Logger log = LoggerFactory.getLogger(FunctionTreeService.class);

    @Autowired
    FunctionService functionService;

    private Result<List<Function>> queryAllTop() {
        FunctionExample example = new FunctionExample();
        example.createCriteria().andDeletedEqualTo(0).andParentIdIsNull();
        return functionService.findByExample(example);
    }

    private Result<List<Integer>> queryAllParentIds() {
        return functionService.findAllParentIds();
    }

    private Tree convertNodeToTree(Function node) {
        Tree tree = new Tree();
        tree.setId(node.getId());
        tree.setName(node.getName());
        tree.setKey(node.getId().toString());
        tree.setTitle(node.getName());
        return tree;
    }

    private Result<List<Function>> queryChildren(Integer parentId) {
        FunctionExample example = new FunctionExample();
        example.createCriteria().andDeletedEqualTo(0).andParentIdEqualTo(parentId);
        return functionService.findByExample(example);
    }

    private Result<Boolean> doFillInfo(Tree node, List<Integer> parentIds) {
        Integer id = node.getId();
        if(!parentIds.contains(id)){
            return Result.returnSuccess();
        }
        Result<List<Function>> ret = queryChildren(id);
        if(!ret.success()) {
            log.error("doFillInfo failed: {}" , ret.msg());
            return Result.returnError("doFillInfo failed:" + ret.msg());
        }
        List<Function> children = ret.value();
        if(null != children && !children.isEmpty()) {
            List<Tree> trees = new ArrayList<>();
            for(Function child : children) {
                Tree tree = convertNodeToTree(child);
                doFillInfo(tree, parentIds);
                trees.add(tree);
            }
            node.setChildren(trees);
        }
        return Result.returnSuccess();
    }

    /**
     * 获取权限树状图
     *
     * @return 树形结构的Function对象
     */
    public Result<List<Tree>> queryTreeNode() {
        /** step1: 查询出所有顶级节点*/
        Result<List<Function>> queryRet = queryAllTop();
        if(!queryRet.success()) {
            log.error("queryTreeNode failed: {}" , queryRet.msg());
            return Result.returnError("queryTreeNode failed:" + queryRet.msg());
        }
        List<Function> tops = queryRet.value();
        /** step2: 查询所有父节点ID*/
        Result<List<Integer>> queryParentIdRet = queryAllParentIds();
        if(!queryParentIdRet.success()) {
            log.error("queryTreeNode failed: {}" , queryParentIdRet.msg());
            return Result.returnError("queryTreeNode failed:" + queryParentIdRet.msg());
        }
        List<Integer> parentIds = queryParentIdRet.value();
        /** step3: 循环填充子节点信息点*/
        List<Tree> trees = new ArrayList<>();
        for(Function top: tops) {
            Tree tree = convertNodeToTree(top);
            Result<Boolean> ret = doFillInfo(tree, parentIds);
            if(!ret.success()) {
                log.error("queryTreeNode failed: {}" , ret.msg());
                return Result.returnError("queryTreeNode failed:" + ret.msg());
            }
            trees.add(tree);
        }
        return Result.returnSuccess(trees);
    }
}
