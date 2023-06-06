import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Row, Col, Card, Form, Input, Button, Divider } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';
import { routerRedux } from 'dva/router';
import StandardTable from '@/components/Tables/StandardTable';
import RoleCreateModal from './RoleCreateModal';
import RoleUpdateModal from './RoleUpdateModal';
import RoleUserModal from './RoleUserModal';
import RolePermissionModal from './RolePermissionModal';
import styles from '../styles.less';

const FormItem = Form.Item;

@connect(({ role, authTree, loading }) => ({
  role,
  authTree,
  loading: loading.effects['devices/fetch'],
}))
@Form.create()
export default class Role extends PureComponent {
  // --------------------------------------初始化----------------------
  componentWillMount() {
    const { dispatch } = this.props;
    this.setSelectedRows([]);
    dispatch({
      type: 'role/fetch',
    });
  }

  // ---------------------------------------事件------------------------
  /* 查询事件 */
  handleSearch = e => {
    e.preventDefault();
    const { dispatch, form } = this.props;

    form.validateFields((err, fieldsValue) => {
      if (err) return;
      const values = {
        ...fieldsValue,
        updatedAt: fieldsValue.updatedAt && fieldsValue.updatedAt.valueOf(),
      };
      this.setFormValues(values);
      console.log('搜索的条件', values);
      dispatch({
        type: 'role/fetch',
        payload: values,
      });
    });
  };

  /* 新建显示框事件 */
  handleModalVisible = flag => {
    this.setCreateModalVisible(!!flag);
  };

  /* 删除事件 */
  handleMenuClick = e => {
    e.preventDefault();
    const {
      dispatch,
      role: { selectedRows },
    } = this.props;
    if (!selectedRows) return;

    dispatch({
      type: 'role/remove',
      payload: selectedRows.map(row => row.id).join(','),
      callback: () => {
        this.callBackRefresh();
      },
    });
  };

  /* 把列表中系统数据禁用编辑 */
  handleProcessData = data => {
    if (!data) {
      return data;
    }
    const { list, pagination } = data;
    const newList = list.map(item => ({ ...item, disabled: item.isSystem == 1 }));
    console.log('ssssaa', newList);
    return { list: newList, pagination };
  };

  /* 编辑框显示事件 */
  handleEditModalVisible = (flag, record) => {
    const { dispatch } = this.props;
    return () => {
      dispatch({
        type: 'role/saveUpdateRole',
        payload: record,
      });
      this.setUpdateModalVisible(flag);
    };
  };

  /* 编辑设置权限显示事件 */
  handleAuthModalVisible = record => {
    const { dispatch } = this.props;
    const seAuthModalVisible = this.setAuthModalVisible;
    return () => {
      dispatch({
        type: 'role/saveUpdateRole',
        payload: record,
      });
      dispatch({
        type: 'authTree/fetch',
        callback: () => {
          dispatch({
            type: 'role/fetchRoleFunctions',
            payload: record.id,
            callback: functions => {
              const functionIds = [];
              functions.forEach(data => functionIds.push(data.id));
              dispatch({
                type: 'authTree/checkedKeys',
                payload: functionIds,
              });
              dispatch({
                type: 'authTree/expandedKeys',
                payload: functionIds,
              });
              seAuthModalVisible(true);
            },
          });
        },
      });
    };
  };

  /* 校验角色名称唯一性 */
  validRoleName = (value, callback) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/validRoleName',
      payload: value,
      callback,
    });
  };

  /* 点击设置角色成员框事件 */
  handleChangeMember = (flag, record) => {
    const { dispatch } = this.props;
    const that = this;
    return () => {
      dispatch({
        type: 'role/saveUpdateRole',
        payload: record,
      });
      dispatch({
        type: 'role/fetchUserRoles',
        payload: record.id,
        callback: userRoles => {
          const userIds = [];
          userRoles.forEach(userRole => userIds.push(userRole.userId));
          dispatch({
            type: 'userTransfer/targetKeys',
            payload: userIds,
          });
          that.setChangeMemberModalVisible(flag);
        },
      });
    };
  };

  /* 设置选中行 */
  setSelectedRows = rows => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/saveSelectedRows',
      payload: rows,
    });
  };

  /* 设置查询搜索框数据 */
  setFormValues = values => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/setFormValues',
      payload: values,
    });
  };

  /* 设置显示框是否显示事件 */
  setCreateModalVisible = flag => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/setCreateModalVisible',
      payload: flag,
    });
  };

  /* 重置搜索框 */
  resetFormAfterDispatch(form, reset) {
    if (reset) {
      form.resetFields();
    }
  }

  /* 重置按钮 */
  handleFormReset = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    this.setFormValues({});
    dispatch({
      type: 'role/fetch',
    });
  };

  /* 新建提交 */
  handleAdd = (fields, form) => {
    this.props.dispatch({
      type: 'role/add',
      payload: fields,
      callback: success => {
        this.callBackRefresh();
        this.setCreateModalVisible(!success);
        this.resetFormAfterDispatch(form, success);
      },
    });
  };

  /* 更新提交 */
  handleUpdate = (fields, form) => {
    this.props.dispatch({
      type: 'role/update',
      payload: fields,
      callback: success => {
        this.callBackRefresh();
        this.setUpdateModalVisible(!success);
        this.resetFormAfterDispatch(form, success);
      },
    });
  };

  /* 设置人员提交 */
  handleChange = (fields, form) => {
    this.props.dispatch({
      type: 'role/changeUpdate',
      payload: fields,
      callback: success => {
        this.callBackRefresh();
        this.setChangeMemberModalVisible(!success);
        this.resetFormAfterDispatch(form, success);
      },
    });
  };

  /* 设置权限提交 */
  handleAuthSetting = (fields, form) => {
    this.props.dispatch({
      type: 'role/addAuth',
      payload: fields,
      callback: success => {
        this.callBackRefresh();
        this.setAuthModalVisible(!success);
        this.resetFormAfterDispatch(form, success);
      },
    });
  };

  /* 关闭编辑窗口 */
  hideUpdateModal = () => {
    const { dispatch } = this.props;
    this.setUpdateModalVisible(false);
    dispatch({
      type: 'role/saveUpdateRole',
      payload: {},
    });
  };

  // 修改显示更新modal的值
  setUpdateModalVisible = flag => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/setUpdateModalVisible',
      payload: flag,
    });
  };

  // 关闭设置人员窗口
  hideChangeMemberModal = () => {
    const { dispatch } = this.props;
    this.setChangeMemberModalVisible(false);
    dispatch({
      type: 'userTransfer/targetKeys',
      payload: [],
    });
  };

  // 修改设置人员modal的值
  setChangeMemberModalVisible = flag => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/setChangeModalVisible',
      payload: flag,
    });
  };

  // 关闭权限设置窗口
  hideAuthSettingModal = () => {
    const { dispatch } = this.props;
    this.setAuthModalVisible(false);
    dispatch({
      type: 'role/saveUpdateRole',
      payload: {},
    });
  };

  // 修改显示权限设置modal的值
  setAuthModalVisible = flag => {
    const { dispatch } = this.props;
    dispatch({
      type: 'role/setAuthModalVisible',
      payload: flag,
    });
  };

  /* 选择行事件 */
  handleSelectRows = rows => {
    this.setSelectedRows(rows);
  };

  // 列表的翻页和过滤排序
  handleStandardTableChange = (pagination, filtersArg, sorter) => {
    const {
      dispatch,
      role: { formValues },
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
      ...filters,
    };
    if (sorter.columnKey) {
      params.sorter = `${sorter.columnKey}_${sorter.order}`;
    }

    dispatch({
      type: 'role/fetch',
      payload: params,
    });
  };

  /* 回调刷新 */
  callBackRefresh = () => {
    const { dispatch, form } = this.props;
    this.setSelectedRows([]);
    form.validateFields((err, fieldsValue) => {
      if (err) return;

      const values = {
        ...fieldsValue,
        updatedAt: fieldsValue.updatedAt && fieldsValue.updatedAt.valueOf(),
      };
      this.setFormValues(values);
      dispatch({
        type: 'role/fetch',
        payload: values,
      });
    });
  };

  // ----------------------------------------组件-------------------------
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSearch} layout="inline">
        <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
          <Col md={8} sm={24}>
            <FormItem label="角色名称">
              {getFieldDecorator('roleName')(<Input placeholder="请输入" />)}
            </FormItem>
          </Col>
          <Col md={8} sm={24}>
            <span className={styles.submitButtons}>
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
      role: {
        selectedRows,
        data,
        createModalVisible,
        updateModalVisible,
        changeModalVisible,
        authModalVisible,
      },
      authTree: { expandedKeys, checkedKeys, autoExpandParent, treeData },
      loading,
    } = this.props;

    // 权限控制
    // const isEdit = authorities.indexOf('am_securityRole_edit') > -1;
    // const isOperate = authorities.indexOf('am_securityRole_setMember') > -1;
    // const isCreate = authorities.indexOf('am_securityRole_create') > -1;
    // const isDelete = authorities.indexOf('am_securityRole_delete') > -1;
    const isEdit = true;
    const isOperate = true;
    const isCreate = true;
    const isDelete = true;

    // 表格列名
    const columns = [
      {
        title: '角色名称',
        dataIndex: 'roleName',
        align: 'center',
      },
      {
        title: '角色所属版本',
        dataIndex: 'coLevel',
        align: 'center',
        render: (text, record) => {
          if (text === 0) {
            return '版本A';
          } if (text === 1) {
            return '版本B';
          } if (text === 2) {
            return '版本C';
          }
            return text;
        },
      },
      {
        title: '角色描述',
        dataIndex: 'roleDesc',
        align: 'center',
      },
      {
        title: '操作',
        align: 'center',
        render: (text, record) => {
          if (record.isSystem) {
            return '系统数据禁止操作';
          }
            return (
              <span>
                <a onClick={this.handleEditModalVisible(true, record)}>编辑</a>
                <Divider type="vertical" />
                <a onClick={this.handleChangeMember(true, record)}>设置人员</a>
                <Divider type="vertical" />
                <a onClick={this.handleAuthModalVisible(record)}>权限设置</a>
              </span>
            );
        },
      },
    ];

    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListForm}>{this.renderForm()}</div>
            <div className={styles.tableListOperator}>
              <Button icon="plus" type="primary" onClick={() => this.handleModalVisible(true)}>
                新建角色
              </Button>
              {selectedRows.length > 0 && (
                <span>
                  <Button style={{ marginLeft: 5 }} icon="minus" onClick={this.handleMenuClick}>
                    删除
                  </Button>
                </span>
              )}
            </div>
            <StandardTable
              selectedRows={selectedRows}
              loading={loading}
              data={this.handleProcessData(data)}
              columns={columns}
              rowKey={record => record.id}
              onSelectRow={this.handleSelectRows}
              onChange={this.handleStandardTableChange}
              scroll={{ x: 800 }}
            />
          </div>
        </Card>
        <RoleCreateModal
          modalVisible={createModalVisible}
          handleModalVisible={this.handleModalVisible}
          handleValid={this.validRoleName}
          onSubmit={this.handleAdd}
        />
        <RoleUpdateModal
          modalVisible={updateModalVisible}
          handleModalVisible={this.hideUpdateModal}
          handleValid={this.validRoleName}
          onSubmit={this.handleUpdate}
        />
        <RoleUserModal
          modalVisible={changeModalVisible}
          handleModalVisible={this.hideChangeMemberModal}
          onSubmit={this.handleChange}
          isEdit={isOperate}
        />
        <RolePermissionModal
          modalVisible={authModalVisible}
          handleModalVisible={this.hideAuthSettingModal}
          onSubmit={this.handleAuthSetting}
          isEdit={isEdit}
          expandedKeys={expandedKeys}
          checkedKeys={checkedKeys}
          autoExpandParent={autoExpandParent}
          treeData={treeData}
        />
      </PageHeaderWrapper>
    );
  }
}
