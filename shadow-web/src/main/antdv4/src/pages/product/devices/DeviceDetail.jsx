import React, { Fragment, PureComponent } from 'react';
import { connect } from 'dva';
import { Table, Button, Card, Icon, Divider, Modal } from 'antd';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import Styles from './styles.less';
import DevicePropCreate from '@/pages/product/devices/Create/DevicePropCreate';

@connect(({ devices, loading }) => ({
  devices,
  loading: loading.effects['devices/fetch'],
}))
export default class DeviceDetail extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      viewState: 'create',
    };
  }

  // 修改产品新增框显示状态
  handleCreateModalVisible = visible => {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changeCreateModal',
      payload: visible,
    });
    dispatch({
      type: 'devices/queryPropChildList',
      payload: [],
    });
    this.setState({
      viewState: 'create',
    });
  };

  goBack = () => {
    const { dispatch } = this.props;
    dispatch(routerRedux.push('/admin/product/devices'));
  };

  deleteRecord = record => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: '确认删除',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'devices/deleteProp',
          payload: record.id,
          callback: () => {
            this.callBackRefresh();
          },
        });
      },
    });
  };

  viewRecord = record => {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/handleRecord',
      payload: record,
    });
    this.setState({
      viewState: 'view',
    });
    dispatch({
      type: 'devices/changeCreateModal',
      payload: true,
    });
    dispatch({
      type: 'devices/queryPropChildList',
      payload: [],
    });
  };

  // 回调 刷新
  callBackRefresh = () => {
    const {
      dispatch,
      devices: { deviceData },
    } = this.props;
    dispatch({
      type: 'devices/fetchProp',
      payload: deviceData.id,
    });
  };

  render() {
    const {
      devices: { createModalVisible, editModalVisible, deviceData, propList },
    } = this.props;
    console.log('propList', propList);
    // 子Table
    const columns = [
      { title: '功能名称', dataIndex: 'name', key: 'name' },
      { title: '标识符', dataIndex: 'structName', key: 'mark' },
      { title: '数据类型', dataIndex: 'construction', key: 'structure' },
      { title: '描述', dataIndex: 'description', key: 'definition' },
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
            {record.construction === 'struct' ? (
              <div style={{ display: 'inline' }}>
                <a type="dashed" onClick={() => this.viewRecord(record)}>
                  查看
                </a>
                <Divider type="vertical" />
              </div>
            ) : (
              ''
            )}
            <a type="dashed" onClick={() => this.deleteRecord(record)}>
              删除
            </a>
          </span>
        ),
      },
    ];
    return (
      <div className={Styles.boardCard}>
        <Card bordered={false}>
          <div className={Styles.header}>
            <a type="dashed" onClick={() => this.goBack()} className={Styles.back}>
              <Icon type="rollback" />
              返回
            </a>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <div className={Styles.detailHead}>{deviceData.deviceName}</div>
          </div>
          <br />
          <span>标识符：</span>
          <span className={Styles.bodyFont}>{deviceData.deviceTag}</span>
          <br />
          <span>描述： </span>
          <span className={Styles.bodyFont}>{deviceData.description}</span>
        </Card>
        <br />
        <Card bordered={false}>
          <Table columns={columns} dataSource={propList} pagination={false} />
          <Button
            style={{
              width: '100%',
              marginTop: 16,
              marginBottom: 8,
            }}
            type="dashed"
            onClick={() => this.handleCreateModalVisible(true)}
            icon="plus"
          >
            新增
          </Button>
          <DevicePropCreate
            handleModalVisible={this.handleCreateModalVisible}
            callBackRefresh={this.callBackRefresh}
            modalState={this.state.viewState}
          />
        </Card>
      </div>
    );
  }
}
