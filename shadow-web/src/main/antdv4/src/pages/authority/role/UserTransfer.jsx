import { Transfer } from 'antd';
import { connect } from 'dva/index';

@connect(({ userTransfer }) => ({
  userTransfer,
}))
class UserTransfer extends React.Component {
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userTransfer/fetch',
    });
  }

  /**
   * 选项在两栏之间转移时的回调函数
   * @param nextTargetKeys 最新的右侧key集合
   * @param direction to left or to right
   * @param moveKeys 被移动的key集合
   */
  handleChange = (nextTargetKeys, direction, moveKeys) => {
    this.props.dispatch({
      type: 'userTransfer/targetKeys',
      payload: nextTargetKeys,
    });

    const { onSelectUserChange } = this.props;
    if (onSelectUserChange) {
      onSelectUserChange(nextTargetKeys.length > 0 ? nextTargetKeys : undefined);
    }
  };

  /**
   *  选中项发生改变时的回调函数
   * @param sourceSelectedKeys
   * @param targetSelectedKeys
   */
  handleSelectChange = (sourceSelectedKeys, targetSelectedKeys) => {
    this.props.dispatch({
      type: 'userTransfer/selectedKeys',
      payload: [...sourceSelectedKeys, ...targetSelectedKeys],
    });
  };

  handleScroll = (direction, e) => {};

  /**
   *  搜索过滤
   * @param inputValue 输入
   * @param option option集合
   * @returns {boolean}
   */
  filterOption = (inputValue, option) => option.name.indexOf(inputValue) > -1;

  /**
   *  渲染展示的Option
   * @param item 数据条目
   * @returns {{label: *, value: (string|*)}}
   */
  renderItem = item => {
    const customLabel = (
      <span>
        {item.name} ({item.type}|{item.cellPhone ? item.cellPhone : item.pheon})
      </span>
    );

    return {
      label: customLabel, // for displayed item
      value: item.id, // for title and filter matching
    };
  };

  render() {
    const {
      userTransfer: { data, targetKeys, selectedKeys },
      isEdit,
    } = this.props;
    return (
      <Transfer
        disabled={isEdit === undefined ? false : isEdit}
        showSearch
        filterOption={this.filterOption}
        dataSource={data}
        titles={['未选', '已选']}
        listStyle={{ width: 300, height: 300 }}
        targetKeys={targetKeys} //显示在右侧框数据的key集合
        selectedKeys={selectedKeys} //设置哪些项应该被选中
        onChange={this.handleChange}
        onSelectChange={this.handleSelectChange}
        //onScroll={this.handleScroll}
        rowKey={record => record.id}
        render={this.renderItem}
      />
    );
  }
}

export default UserTransfer;
