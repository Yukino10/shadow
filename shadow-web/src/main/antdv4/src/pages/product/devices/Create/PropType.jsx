import React from 'react';
import { Input, Modal, Form, Upload, Button, Icon, Checkbox, Col, Row, Select, Radio, Table } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;
const { Option } = Select;

@connect(({devices, loading}) => ({
  devices,
  loading: loading.effects['devices/fetch'],
}))
class PropType extends React.Component {
  handleModalVisible(visible) {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changePropCreateModal',
      payload: visible,
    });
  }

  onHandle = e => {
    e.preventDefault();
    const { form, handleModalVisible, dispatch, callBackRefresh, devices: { propChildList } } = this.props;
    form.validateFields((err, values) => {
      console.log('数据:', { ...values });
      if (!err) {
        propChildList.push(values);
        dispatch({
          type: 'devices/queryPropChildList',
          payload: propChildList,
        });
        this.handleModalVisible(false);
      }
    });
  };

  handleCreateModalVisible(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changePropCreateModal',
      payload: data,
    });
  }

  render() {
    const { form, devices: { propCreateVisible } } = this.props;
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
        title="新增参数"
        visible={propCreateVisible}
        onOk={this.onHandle}
        onCancel={() => this.handleCreateModalVisible(false)}
      >
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="参数名称">
          {form.getFieldDecorator('propChildName', {
            rules: [{ required: true }],
          })(<Input placeholder="请输入"/>)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="标识符">
          {form.getFieldDecorator('propChildTag', {
            rules: [{ required: true },
            ],
          })(<Input placeholder="请输入"/>)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="数据类型">
          {form.getFieldDecorator('construction', {
            rules: [{ required: true }],
          })(
            <Select
              style={{ width: '100%' }}
              placeholder="Please select"
              onChange={null}
            >
              {dataType}
            </Select>,
          )}
        </FormItem>
      </Modal>
    )
  }
}

export default Form.create()(PropType);
