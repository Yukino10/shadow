import React, { PureComponent } from 'react';
import {
  Input,
  Modal,
  Form,
  Upload,
  Button,
  Icon,
  Checkbox,
  Col,
  Row,
  Select,
  Radio,
  Table,
  Grid,
} from 'antd';
import { connect } from 'dva';
import VirtualTable from '@/pages/product/devices/components/VirtualTable';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;

@connect(({ devices, loading }) => ({
  devices,
  loading: loading.effects['devices/fetch'],
}))
class DevicePropCreate extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      documentType: 'base', // base | array | struct
    };
  }

  changeStructElement = data => {
    const { dispatch } = this.props;
    console.log(data);
    if (data === 'struct' || data === 'array') {
      dispatch({
        type: 'devices/changeStructElement',
        payload: data,
      });
    } else {
      dispatch({
        type: 'devices/changeStructElement',
        payload: 'base',
      });
    }
  };

  handleModalVisible(visible) {
    const { dispatch } = this.props;
    dispatch({
      type: 'devices/changeCreateModal',
      payload: visible,
    });
  }

  onHandle = e => {
    e.preventDefault();
    const {
      form,
      handleModalVisible,
      dispatch,
      callBackRefresh,
      devices: { structElement, propChildList, deviceData },
    } = this.props;
    form.validateFields((err, values) => {
      console.log('表单提交的属性值:', { ...values });
      console.log('表单提交的属性值:', structElement);
      console.log('表单提交的属性值:', deviceData);
      if (!err) {
        const result = {
          ...values,
          deviceId: deviceData.id,
        };
        if (structElement !== 'base') {
          if (structElement === 'struct') {
            result.propChildList = propChildList;
          }
          alert(JSON.stringify(result));
        }
        dispatch({
          type: 'devices/createDeviceProp',
          payload: result,
          callback: () => {
            callBackRefresh();
            this.handleModalVisible(false);
          },
        });
      }
    });
  };
  // 加载 子属性的值

  render() {
    const {
      handleModalVisible,
      modalState,
      devices: { structElement, createModalVisible, viewRecord },
    } = this.props;
    const { form } = this.props;
    const isView = modalState !== 'create';
    console.log(viewRecord);
    const funcType = [
      <Option key="property">属性</Option>,
      <Option key="incident">事件</Option>,
      <Option key="server">服务</Option>,
    ];
    const dataType = [
      <Option key="int">int</Option>,
      <Option key="float">float</Option>,
      <Option key="double">double</Option>,
      <Option key="boolean">boolean</Option>,
      <Option key="text">text</Option>,
      <Option key="string">url</Option>,
      <Option key="date">date</Option>,
      <Option key="struct">struct</Option>,
    ];
    return (
      <Modal
        destroyOnClose
        title="创建物模型属性"
        visible={createModalVisible}
        onOk={this.onHandle}
        onCancel={() => handleModalVisible(false)}
      >
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="功能类型">
          {form.getFieldDecorator('type', {
            rules: [{ required: true }],
          })(
            <Select style={{ width: '100%' }} placeholder="请选择" disabled={isView} value="属性">
              {funcType}
            </Select>,
          )}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="功能名称">
          {form.getFieldDecorator('name', {
            rules: [
              {
                required: true,
                message: '请输入产品的名称',
              },
            ],
          })(<Input placeholder="请输入" value={!isView ? '' : viewRecord.name} />)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="标识符">
          {form.getFieldDecorator('structName', {
            rules: [
              {
                required: true,
              },
            ],
          })(<Input placeholder="请输入" />)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="数据类型">
          {form.getFieldDecorator('construction', {
            rules: [{ required: true }],
          })(
            <Select
              style={{ width: '100%' }}
              placeholder="Please select"
              onChange={this.changeStructElement}
            >
              {dataType}
            </Select>,
          )}
        </FormItem>
        {structElement === 'base' ? (
          ''
        ) : structElement === 'struct' ? (
          <FormItem label="结构体对象">
            <VirtualTable />
          </FormItem>
        ) : (
          <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 150 }} label="元素类型">
            {form.getFieldDecorator('arrayType', {
              rules: [{ required: true }],
            })(
              <Radio.Group onChange={null}>
                <Radio value="int">int</Radio>
                <Radio value="float">float</Radio>
                <Radio value="double">double</Radio>
                <Radio value="text">text</Radio>
              </Radio.Group>,
            )}
          </FormItem>
        )}
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="描述">
          {form.getFieldDecorator('description')(<TextArea rows={4} />)}
        </FormItem>
      </Modal>
    );
  }
}

export default Form.create()(DevicePropCreate);
