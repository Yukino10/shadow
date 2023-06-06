import React from 'react';
import { Input, Modal, Form, Upload, Button, Icon, Checkbox, Col, Row, Select, Radio, Table } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;

@connect(({devices, loading}) => ({
  devices,
  loading: loading.effects['devices/fetch'],
}))
class DeviceCreate extends React.Component {
  handleModalVisible(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changeDeviceCreateModal',
      payload: data,
    });
  }

  onHandle = e => {
    e.preventDefault();
    const { form, dispatch, callBackRefresh } = this.props;
    form.validateFields((err, values) => {
      console.log('表单提交的数据:', { ...values });
      if (!err) {
        dispatch({
          type: 'devices/create',
          payload: values,
          callback: () => {
            callBackRefresh();
            this.handleModalVisible(false);
          },
        });
      }
    });
  };

  handleCreateModalVisible(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changeDeviceCreateModal',
      payload: data,
    });
  }

  render() {
    const { form, devices: { deviceCreateVisible } } = this.props;
    const dataType = [
      <Option key="int">int</Option>,
      <Option key="float">float</Option>,
      <Option key="double">double</Option>,
      <Option key="boolean">boolean</Option>,
      <Option key="text">text</Option>,
      <Option key="date">date</Option>,
    ];
    return (
      <Modal
        destroyOnClose
        title="新增物模型"
        visible={deviceCreateVisible}
        onOk={this.onHandle}
        onCancel={() => this.handleCreateModalVisible(false)}
      >
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="物模型名称">
          {form.getFieldDecorator('deviceName', {
            rules: [{ required: true }],
          })(<Input placeholder="请输入"/>)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="标识符">
          {form.getFieldDecorator('deviceTag', {
            rules: [{ required: true },
            ],
          })(<Input placeholder="请输入"/>)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="描述">
          {form.getFieldDecorator('description')(<TextArea rows={4}/>)}
        </FormItem>
      </Modal>
    )
  }
}

export default Form.create()(DeviceCreate);
