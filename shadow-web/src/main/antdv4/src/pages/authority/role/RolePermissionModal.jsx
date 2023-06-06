import React, { Component } from 'react';
import { Row, Col, Form, Input, Modal } from 'antd';

import { connect } from 'dva';
import RolePermissionSetTree from './RolePermissionSetTree';

const FormItem = Form.Item;

@connect(({ role }) => ({
  role,
}))
class RolePermissionModal extends Component {
  constructor(props) {
    super(props);
  }

  okHandle = () => {
    const {
      form,
      role: { updateRole },
    } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      fieldsValue.roleId = updateRole.id;
      this.props.onSubmit(fieldsValue, form);
    });
  };

  onSelectUserChange = selectedUsers => {
    const { form } = this.props;
    form.setFieldsValue({
      permissions: selectedUsers,
    });
  };

  render() {
    const {
      form,
      handleModalVisible,
      modalVisible,
      expandedKeys,
      checkedKeys,
      autoExpandParent,
      treeData,
      isEdit,
      role: { updateRole },
    } = this.props;

    const TreeProps = {
      isEdit,
      expandedKeys,
      checkedKeys,
      autoExpandParent,
      treeData,
      onChange: keys => {
        form.setFieldsValue({ functions: keys });
      },
    };

    return (
      <Modal
        destroyOnClose
        width={600}
        title="设置权限"
        visible={modalVisible}
        onOk={this.okHandle}
        onCancel={handleModalVisible}
        maskClosable={false}
      >
        <Row gutter={8}>
          <Col span={12}>
            <h3>
              角色名称: <span style={{ paddingLeft: '5px' }}>{updateRole.roleName}</span>
            </h3>
          </Col>
        </Row>

        <FormItem>
          {form.getFieldDecorator('functions', {
            initialValue: checkedKeys,
          })(<RolePermissionSetTree {...TreeProps} />)}
        </FormItem>
      </Modal>
    );
  }
}

export default Form.create()(RolePermissionModal);
