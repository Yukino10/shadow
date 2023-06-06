import React, { Component } from 'react';
import { Tree } from 'antd';
import { connect } from 'dva/index';

const { TreeNode } = Tree;

@connect(({ authTree }) => ({
  authTree,
}))
class TreeDemo extends Component {
  onExpand = expandedKeys => {
    const { dispatch } = this.props;
    dispatch({
      type: 'authTree/expandedKeys',
      payload: expandedKeys,
    });
    dispatch({
      type: 'authTree/autoExpandParent',
      payload: false,
    });
  };

  onCheck = checkedKeys => {
    const { onChange, dispatch } = this.props;
    onChange(checkedKeys);
    dispatch({
      type: 'authTree/checkedKeys',
      payload: checkedKeys,
    });
  };

  renderTreeNodes = data => data.map(item => {
      if (item.children) {
        return (
          <TreeNode title={item.title} key={item.key} dataRef={item} selectable={false}>
            {this.renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode {...item} />;
    });

  render() {
    const {
      treeData,
      authTree: { expandedKeys, checkedKeys, autoExpandParent },
      isEdit,
    } = this.props;
    return (
      <Tree
        disabled={!isEdit}
        checkable
        onExpand={this.onExpand}
        expandedKeys={expandedKeys}
        autoExpandParent={autoExpandParent}
        onCheck={this.onCheck}
        checkedKeys={checkedKeys}
      >
        {this.renderTreeNodes(treeData)}
      </Tree>
    );
  }
}

export default TreeDemo;
