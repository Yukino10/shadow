import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Button, Card, Col, Form, Input, Row, Divider, Icon, Modal, Select } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { routerRedux } from 'dva/router';
import moment from 'moment';
import StandardTable from '@/components/Tables/StandardTable';
import { filterNullFields } from '@/utils/utils';
import styles from '../styles.less';

const FormItem = Form.Item;

@connect(({ person, loading }) => ({
  person,
  loading: loading.effects['perosn/fetch'],
}))
@Form.create()
export default class Users extends PureComponent {
  // ---------------------------------------------------初始化-----------------------------------------
  componentWillMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'person/fetch',
    });
    // dispatch({
    //   type: 'person/getRoleList',
    // });
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
        type: 'person/fetch',
        payload: values,
      });
      dispatch({
        type: 'person/setFormValues',
        payload: values,
      });
    });
  };

  /* 删除用户事件 */
  handleRemove = e => {
    e.preventDefault();
    const {
      dispatch,
      person: { selectedRows },
    } = this.props;
    const callback = this.callBackRefresh;
    Modal.confirm({
      title: '确定删除选中人员?',
      content: '',
      onOk() {
        if (!selectedRows) return;
        dispatch({
          type: 'person/remove',
          payload: selectedRows,
          callback,
        });
      },
      okText: '确定',
      cancelText: '取消',
    });
    dispatch({
      type: 'person/setSelectRows',
      payload: [],
    });
  };

  /* 重置密码 */
  handleResetPassword = record => {
    const { dispatch } = this.props;
    const callback = this.callBackRefresh;
    Modal.confirm({
      title: '确定重置密码为123456?',
      content: '',
      onOk() {
        dispatch({
          type: 'person/resetPassword',
          payload: record.id,
          callback,
        });
      },
      okText: '确定',
      cancelText: '取消',
    });
  };

  /* 搜索框重置事件 */
  handleFormReset = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: 'person/setFormValues',
      payload: {},
    });
    dispatch({
      type: 'person/fetch',
    });
  };

  /* 选中行触发添加选中信息操作 */
  handleSelectRows = rows => {
    this.props.dispatch({
      type: 'person/setSelectRows',
      payload: rows,
    });
  };

  /* 页数排序过滤显示事件 */
  handleStandardTableChange = (pagination, filtersArg, sorter) => {
    const {
      dispatch,
      person: { formValues },
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
      type: 'person/fetch',
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
        type: 'person/setFormValues',
        payload: values,
      });
      dispatch({
        type: 'person/fetch',
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
            <FormItem label="用户姓名">
              {getFieldDecorator('userName')(<Input placeholder="输入用户姓名" />)}
            </FormItem>
          </Col>
          <Col md={6} sm={24}>
            <FormItem label="手机号">
              {getFieldDecorator('phone')(<Input placeholder="输入用户手机号" />)}
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
      person: { selectedRows, data },
      loading,
    } = this.props;
    // 表格展示列
    const columns = [
      {
        title: '登录名',
        dataIndex: 'username',
        key: 'username',
      },
      {
        title: '姓名',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '手机',
        dataIndex: 'telephone',
        key: 'telephone',
      },
      {
        title: '所属角色',
        dataIndex: 'roleName',
        key: 'roleName',
      },
      {
        title: '注册时间',
        dataIndex: 'createTime',
        key: 'createTime',
        render: val => moment(val).format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        title: '上次登出时间',
        dataIndex: 'lastLogoutTime',
        key: 'lastLogoutTime',
        render: val => moment(val).format('YYYY-MM-DD HH:mm:ss'),
      },
      {
        title: '操作',
        dataIndex: 'operation',
        key: 'operation',
        fixed: 'right',
        width: 100,
        render: (operation, record) => (
          <div>
            <a onClick={() => this.handleResetPassword(record)}>重置密码</a>
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
                    注销用户
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
      </PageHeaderWrapper>
    );
  }
}
