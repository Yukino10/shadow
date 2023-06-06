import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Button, Card, Col, Form, Input, Row, Divider, Icon, Modal, Select } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import moment from 'moment';
import StandardTable from '@/components/Tables/StandardTable';
import { filterNullFields } from '@/utils/utils';
import Create from '@/pages/product/define/components/Create';
import Edit from '@/pages/product/define/components/Edit';
import Version from '@/pages/product/define/components/Version';
import VersionNo from '@/pages/product/define/components/VersionNo';
import styles from '../../authority/styles.less';

const FormItem = Form.Item;

@connect(({ product, loading, devices }) => ({
  product,
  devices,
  loading: loading.effects['product/fetchList'],
}))
@Form.create()
export default class DefineList extends PureComponent {
  // ---------------------------------------------------初始化-----------------------------------------
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/fetchList',
    });
  }

  // ---------------------------------------------------事件------------------------------------------
  /* 查询确定事件 */
  handleSearch = e => {
    e.preventDefault();
    const { dispatch, form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      filterNullFields(fieldsValue);
      /* 过滤掉空字符串参数 */
      const values = {
        ...fieldsValue,
        updatedAt: fieldsValue.updatedAt && fieldsValue.updatedAt.valueOf(),
      };
      dispatch({
        type: 'product/fetchList',
        payload: values,
      });
      dispatch({
        type: 'product/setFormValues',
        payload: values,
      });
    });
  };

  /* 删除用户事件 */
  handleRemove = e => {
    e.preventDefault();
    const {
      dispatch,
      product: { selectedRows },
    } = this.props;
    const callback = this.callBackRefresh;
    Modal.confirm({
      title: '确定删除选中产品?',
      content: '',
      onOk() {
        if (!selectedRows) return;
        dispatch({
          type: 'product/remove',
          payload: selectedRows,
          callback,
        });
      },
      okText: '确定',
      cancelText: '取消',
    });
    dispatch({
      type: 'product/setSelectRows',
      payload: [],
    });
  };

  // 修改事件
  handleEdit = item => {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/saveRecord',
      payload: item,
    });
    this.handleEditModalVisible(true);
  };

  // 修改产品编辑框显示状态
  handleEditModalVisible = visible => {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/changeEditModalVisible',
      payload: visible,
    });
  };

  // 打包
  handlePackage = item => {
    const { dispatch } = this.props;
    Modal.confirm({
      title: '确认打包',
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        dispatch({
          type: 'product/saveRecord',
          payload: item,
        });

        dispatch({
          type: 'product/isUpdated',
          payload: item.id,
          callback: updated => {
            if (updated === 1) {
              this.handleVersionNoModalVisible(true);
            } else {
              const data = {};
              data.productId = item.id;
              this.handlePackageValidate(data);
            }
          },
        });
      },
    });
  };

  // 版本号编辑框显示状态
  handleVersionNoModalVisible = visible => {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/changeVersionNoModalVisible',
      payload: visible,
    });
  };

  // 打包信息验证
  handlePackageValidate = data => {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/packageValidate',
      payload: data,
      callback: versionId => {
        this.handleVersionNoModalVisible(false);
        this.packageProduct(data.productId, versionId);
      },
    });
  };

  // 打包
  packageProduct = (productId, versionId) => {
    document
      .getElementById(`product${productId}`)
      .setAttribute('href', `/admin/product/package/download?versionId=${versionId}`);
    document.getElementById(`product${productId}`).click();
  };

  // 显示产品版本信息事件
  handleVersion = item => {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/saveRecord',
      payload: item,
    });

    dispatch({
      type: 'product/getVersion',
      payload: item.id,
    });
    this.handleVersionModalVisible(true);
  };

  // 产品版本列表显示状态
  handleVersionModalVisible = visible => {
    const { dispatch } = this.props;
    dispatch({
      type: 'product/changeVersionModalVisible',
      payload: visible,
    });
  };

  /* 搜索框重置事件 */
  handleFormReset = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: 'product/setFormValues',
      payload: {},
    });
    dispatch({
      type: 'product/fetchList',
    });
  };

  /* 选中行触发添加选中信息操作 */
  handleSelectRows = rows => {
    this.props.dispatch({
      type: 'product/setSelectRows',
      payload: rows,
    });
  };

  /* 页数排序过滤显示事件 */
  handleStandardTableChange = (pagination, filtersArg, sorter) => {
    const {
      dispatch,
      product: { formValues },
    } = this.props;

    const filters = Object.keys(filtersArg).reduce((obj, key) => {
      const newObj = { ...obj };
      newObj[key] = getValue(filtersArg[key]);
      return newObj;
    }, {});

    const params = {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...formValues,
      filter: JSON.stringify(filters),
    };
    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }

    dispatch({
      type: 'product/fetchList',
      payload: params,
    });
  };

  /* 回调刷新列表事件 */
  callBackRefresh = () => {
    const { dispatch, form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const values = {
        ...fieldsValue,
        updatedAt: fieldsValue.updatedAt && fieldsValue.updatedAt.valueOf(),
      };
      dispatch({
        type: 'product/setFormValues',
        payload: values,
      });
      dispatch({
        type: 'product/fetchList',
        payload: values,
      });
    });
  };

  // --------------------------------------------------- 展示组件----------------------------------------
  // 查询筛选框
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 6, lg: 24, xl: 48 }}>
          <Col md={6} sm={24}>
            <FormItem label="产品名称">
              {getFieldDecorator('productName')(<Input placeholder="输入产品名称" />)}
            </FormItem>
          </Col>
          <Col md={6} sm={24}>
            <span style={{ float: 'left', marginBottom: 24 }}>
              <Button type="primary" htmlType="submit">
                查询
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                重置
              </Button>
            </span>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      product: { selectedRows, data },
      product: { createModalVisible, editModalVisible, versionModalVisible, versionNoModalVisible },
      loading,
    } = this.props;
    // 表格展示列
    const columns = [
      {
        title: '产品名称',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '关联物理模型',
        dataIndex: 'deviceName',
        key: 'deviceName',
      },
      {
        title: '加密方式',
        dataIndex: 'encryption',
        key: 'encryption',
        render: data => {
          if (data === undefined || data === 'none') {
            return <div>无加密</div>;
          }
            return <div>{data}</div>;
        },
      },
      {
        title: '所用公共服务',
        dataIndex: 'serverSize',
        key: 'serverSize',
      },
      {
        title: '所用协议',
        dataIndex: 'protocolSize',
        key: 'protocolSize',
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        render: val => moment(val).format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        fixed: 'right',
        width: 300,
        render: (operation, record) => (
          <div>
            <a onClick={() => this.handleEdit(record)}>编辑</a>
            <Divider type="vertical" />
            <a onClick={() => this.handlePackage(record)}>打包</a>
            <Divider type="vertical" />
            <a onClick={() => this.handleVersion(record)}>查看历史版本</a>
          </div>
        ),
      },
    ];

    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListForm}>{this.renderForm()}</div>
            <div className={styles.tableListOperator}>
              {selectedRows.length > 0 && (
                <span>
                  <Button icon="minus" onClick={this.handleRemove}>
                    删除
                  </Button>
                </span>
              )}
            </div>
          </div>
          <Divider style={{ marginBottom: 32, marginTop: 0 }} />
          <StandardTable
            selectedRows={selectedRows}
            loading={loading}
            data={data}
            columns={columns}
            rowKey={record => record.id}
            onSelectRow={this.handleSelectRows}
            onChange={this.handleStandardTableChange}
          />
        </Card>
        <Create
          modalVisible={createModalVisible}
          handleModalVisible={this.handleCreateModalVisible}
          callBackRefresh={this.callBackRefresh}
        />
        <Edit
          modalVisible={editModalVisible}
          handleModalVisible={this.handleEditModalVisible}
          callBackRefresh={this.callBackRefresh}
        />
        <Version
          modalVisible={versionModalVisible}
          handleModalVisible={this.handleVersionModalVisible}
        />
        <VersionNo
          modalVisible={versionNoModalVisible}
          handleModalVisible={this.handleVersionNoModalVisible}
          onSubmit={this.handlePackageValidate}
        />
      </PageHeaderWrapper>
    );
  }
}
