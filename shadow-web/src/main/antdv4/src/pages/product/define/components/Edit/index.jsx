import React, { PureComponent } from 'react';
import { Input, Modal, Form, Upload, Button, Icon, Checkbox, Col, Row, Select } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;

@connect(({ product, baseService }) => ({
  product,
  baseService,
  record: product.record,
  loading: product.fetch,
}))
class Edit extends PureComponent {
  onHandle = e => {
    e.preventDefault();
    const { form, handleModalVisible, dispatch, callBackRefresh, record } = this.props;
    form.validateFields((err, values) => {
      const data = {};
      data.id = record.id;
      data.name = values.name;
      data.oldProtocolList = record.protocolList;
      data.oldServerList = record.serverList;
      data.protocolList = values.protocolList;
      data.serverList = values.serverList;
      data.description = values.description;
      console.log('表单提交的原数据:', { ...data });
      dispatch({
        type: 'product/update',
        payload: data,
        callback: () => {
          handleModalVisible(false);
          callBackRefresh();
        },
      });
    });
  };

  render() {
    const {
      modalVisible,
      handleModalVisible,
      record,
      baseService: { servicelist },
    } = this.props;
    const { form } = this.props;
    console.log('修改参数', record);
    return (
      <Modal
        destroyOnClose
        title="修改产品"
        visible={modalVisible}
        onOk={this.onHandle}
        onCancel={() => handleModalVisible(false)}
      >
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="产品名称">
          {form.getFieldDecorator('name', {
            initialValue: record.name,
            rules: [
              {
                required: true,
                message: '请输入产品的名称',
              },
            ],
          })(<Input placeholder="请输入" />)}
        </FormItem>
        <FormItem
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 15, align: 'middle' }}
          label="产品协议"
        >
          {form.getFieldDecorator('protocolList', {
            initialValue: record.protocolList,
            rules: [{ required: true }],
          })(
            <Checkbox.Group style={{ width: '100%' }}>
              <Row>
                <Col span={8}>
                  <Checkbox value="mqtt">MQTT</Checkbox>
                </Col>
                <Col span={8}>
                  <Checkbox value="coap">COAP</Checkbox>
                </Col>
              </Row>
            </Checkbox.Group>,
          )}
        </FormItem>
        <FormItem
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 15, align: 'middle' }}
          label="操作系统"
        >
          {form.getFieldDecorator('operateSystem', {
            initialValue: record.operateSystem,
          })(
            <Select style={{ width: '100%' }} placeholder="Please select">
              <Option key="Linux">Linux</Option>
              <Option key="Android">Android</Option>
            </Select>,
          )}
        </FormItem>
        <FormItem
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 15, align: 'middle' }}
          label="云端系统"
        >
          {form.getFieldDecorator('cloudSystem', {
            initialValue: record.cloudSystem,
          })(
            <Select style={{ width: '100%' }} placeholder="Please select">
              <Option key="spring boot">spring boot</Option>
              <Option key="python">Python</Option>
            </Select>,
          )}
        </FormItem>
        <FormItem
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 15, align: 'middle' }}
          label="加密方式"
        >
          {form.getFieldDecorator('encryption', {
            initialValue: record.encryption,
          })(
            <Select style={{ width: '100%' }} placeholder="Please select">
              <Option key="none">无加密</Option>
              <Option key="rsa">RSA</Option>
              <Option key="aes">AES</Option>
            </Select>,
          )}
        </FormItem>
        <FormItem
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 15, align: 'middle' }}
          label="基础服务"
        >
          {form.getFieldDecorator('baseService', {
            initialValue: record.serverList,
          })(
            <Select mode="multiple" style={{ width: '100%' }} placeholder="Please select">
              {servicelist.map(d => (
                <Option key={d.id}>{d.serviceName}</Option>
              ))}
            </Select>,
          )}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="产品描述">
          {form.getFieldDecorator('description', {
            initialValue: record.description,
          })(<TextArea rows={4} />)}
        </FormItem>
      </Modal>
    );
  }
}

export default Form.create()(Edit);
