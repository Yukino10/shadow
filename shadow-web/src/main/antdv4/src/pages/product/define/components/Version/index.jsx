import React, { PureComponent } from 'react';
import { Modal, Divider, Table } from 'antd';
import { connect } from 'dva';
import moment from 'moment';

@connect(({ product }) => ({
  product,
}))
export default class Version extends PureComponent {
  handleDelete = item => {
    const { dispatch } = this.props;

    Modal.confirm({
      title: '确认删除版本信息',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'product/deleteProductVersion',
          payload: item.id,
          callback: () => {
            this.callbackRefresh(item);
          },
        })
      },
    });
  }

  callbackRefresh = item => {
    const { dispatch } = this.props;

    dispatch({
      type: 'product/getVersion',
      payload: item.productId,
    })
  }

  handleDownload = item => {
    Modal.confirm({
      title: '确认打包',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        document.getElementById(`version${item.id}`).click();
      },
    });
  }

  render() {
    const { modalVisible, handleModalVisible, product: { versionList, record } } = this.props;

    const columns = [
      { title: '版本号', dataIndex: 'versionNo', key: 'versionNo' },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        render: val => <span>{moment(val).format('YYYY-MM-DD HH:mm')}</span>,
      },
      { title: '备注', dataIndex: 'remark', key: 'remark' },
      {
        title: '操作',
key: 'operation',
render: (text, item) => (
          <span>
            {/* <a type="dashed">查看</a> */}
            {/* <Divider type="vertical"/> */}
            <a type="dashed" onClick={() => this.handleDownload(item)}>下载</a>
            <Divider type="vertical"/>
            <a type="dashed" onClick={() => this.handleDelete(item)}>删除</a>
            <a id={`version${item.id}`} href={`/admin/product/package/download?versionId=${item.id}`} download={`${record.name}.zip`} />
          </span>
        ),
      },
    ];

    return (
      <Modal
        destroyOnClose
        title="产品版本"
        visible={modalVisible}
        onCancel={() => handleModalVisible(false)}
        footer={null}
      >
        <h2>产品：{record.name}</h2>
        <Table
          columns={columns}
          dataSource={versionList}
          pagination
        />
      </Modal>
    );
  }
}
