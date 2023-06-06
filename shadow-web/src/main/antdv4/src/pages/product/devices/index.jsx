import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Table, Button, Card, Divider, Modal } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import DeviceCreate from '@/pages/product/devices/Create/DeviceCreate';
import StandardTable from '@/components/Tables/StandardTable';

@connect(({ devices, loading }) => ({
  devices,
  loading: loading.effects['devices/fetch'],
}))
export default class Devices extends PureComponent {
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/fetch',
    });
  }

  handleAddDevice(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changeDeviceCreateModal',
      payload: data,
    });
  }

  showViewModal = record => {
    const { dispatch } = this.props;
    dispatch(routerRedux.push('/admin/product/devices/propType', record));
    dispatch({
      type: 'devices/setDeviceDetail',
      payload: record,
    });
    dispatch({
      type: 'devices/fetchProp',
      payload: record.id,
    });
  };

  deleteRecord = record => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: '确认删除物模型信息',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'devices/delete',
          payload: record.id,
          callback: () => {
            this.callBackRefresh();
          },
        });
      },
    });
  };

  // 回调 刷新
  callBackRefresh = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/fetch',
    });
  };

  render() {
    const {
      devices: { data, createModalVisible, editModalVisible },
      loading,
    } = this.props;
    // 子Table
    const columns = [
      { title: '物模型名称', dataIndex: 'deviceName', key: 'deviceName' },
      { title: '描述', dataIndex: 'description', key: 'description' },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        render: val => <span>{moment(val).format('YYYY-MM-DD HH:mm')}</span>,
      },
      {
        title: '操作',
        key: 'operation',
        render: (text, record) => (
          <span>
            <a type="dashed" onClick={() => this.showViewModal(record)}>
              查看
            </a>
            <Divider type="vertical" />
            <a type="dashed" onClick={() => this.deleteRecord(record)}>
              删除
            </a>
          </span>
        ),
      },
    ];
    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div>
            <Button icon="plus" type="primary" onClick={() => this.handleAddDevice(true)}>
              新增物模型
            </Button>
          </div>
          <br />
          <Table loading={loading} dataSource={data.list} columns={columns} />
          <DeviceCreate callBackRefresh={this.callBackRefresh} />
        </Card>
      </PageHeaderWrapper>
    );
  }
}
