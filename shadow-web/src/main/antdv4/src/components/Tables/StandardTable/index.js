import React, { PureComponent, Fragment } from 'react';
import { Table, Alert } from 'antd';
import styles from './index.less';

function initTotalList(columns) {
  const totalList = [];
  columns.forEach(column => {
    if (column.needTotal) {
      totalList.push({ ...column, total: 0 });
    }
  });
  return totalList;
}

class StandardTable extends PureComponent {
  constructor(props) {
    super(props);
    const { columns } = props;
    const needTotalList = initTotalList(columns);

    this.state = {
      selectedRowKeys: [],
      needTotalList,
    };
  }

  componentWillReceiveProps(nextProps) {
    // clean state
    if (nextProps.selectedRows.length === 0) {
      const needTotalList = initTotalList(nextProps.columns);
      this.setState({
        selectedRowKeys: [],
        needTotalList,
      });
    }
  }

  handleRowSelectChange = (selectedRowKeys, selectedRows) => {
    let needTotalList = [...this.state.needTotalList];
    needTotalList = needTotalList.map(item => ({
        ...item,
        total: selectedRows.reduce((sum, val) => sum + parseFloat(val[item.dataIndex], 10), 0),
      }));

    if (this.props.onSelectRow) {
      this.props.onSelectRow(selectedRows);
    }

    this.setState({ selectedRowKeys, needTotalList });
  };

  handleTableChange = (pagination, filters, sorter) => {
    this.props.onChange(pagination, filters, sorter);
  };

  cleanSelectedKeys = () => {
    this.handleRowSelectChange([], []);
  };

  render() {
    const { selectedRowKeys, needTotalList } = this.state;
    const {
      data: { list, pagination },
      loading,
      columns,
      rowKey,
      noSelect,
      scroll,
    } = this.props;

    let newPagination = { ...pagination };
    if (pagination.current == undefined) {
      newPagination = { ...pagination, current: pagination.pageNum };
    }

    function showTotal(total, range) {
      return () => <span className={styles.showTotal}>共 {total} 条</span>;
    }

    const paginationProps = {
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: showTotal(newPagination.total),
      ...newPagination,
    };

    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
      getCheckboxProps: record => ({
        disabled: record.disabled,
      }),
    };

    let divStyle = null;
    let rowSelectionProp = { rowSelection };
    if (noSelect) {
      divStyle = { display: 'none' };
      rowSelectionProp = {};
    }

    return (
      <div className={styles.standardTable}>
        <div className={styles.tableAlert} style={divStyle}>
          {selectedRowKeys.length > 0 ? (
            <Alert
              message={
                <Fragment>
                  已选择 <a style={{ fontWeight: 600 }}>{selectedRowKeys.length}</a> 项&nbsp;&nbsp;
                  {needTotalList.map(item => (
                    <span style={{ marginLeft: 8 }} key={item.dataIndex}>
                      {item.title}总计&nbsp;
                      <span style={{ fontWeight: 600 }}>
                        {item.render ? item.render(item.total) : item.total}
                      </span>
                    </span>
                  ))}
                  <a onClick={this.cleanSelectedKeys} style={{ marginLeft: 24 }}>
                    清空
                  </a>
                </Fragment>
              }
              type="info"
              showIcon
            />
          ) : (
            ''
          )}
        </div>
        <Table
          loading={loading}
          rowKey={rowKey || 'key'}
          {...rowSelectionProp}
          dataSource={list}
          columns={columns}
          pagination={paginationProps}
          onChange={this.handleTableChange}
          scroll={scroll || undefined}
        />
      </div>
    );
  }
}

export default StandardTable;
