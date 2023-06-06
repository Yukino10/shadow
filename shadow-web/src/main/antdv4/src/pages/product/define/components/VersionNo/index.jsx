import React, { PureComponent } from 'react';
import { Form, Input, Modal } from 'antd';
import { connect } from 'dva';

const FormItem = Form.Item;
@connect(({ product }) => ({
  product,
}))
class VersionNo extends PureComponent {
  onHandle = e => {
    e.preventDefault();
    const { form, onSubmit, product: { record } } = this.props;
    form.validateFields((err, values) => {
      const data = {};
      data.productId = record.id;
      data.versionNo = values.versionNo;
      data.remark = values.remark;

      onSubmit(data);
    })
  }

  render() {
    const { form, modalVisible, handleModalVisible } = this.props;

    return (
      <Modal
        destroyOnClose
        title="版本号"
        visible={modalVisible}
        onOk={this.onHandle}
        onCancel={() => handleModalVisible(false)}
      >
        <p>模型信息发生了变化，需要输入新的版本号</p>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="产品版本号">
          {form.getFieldDecorator('versionNo', {
            rules: [
              {
                required: true,
                message: '请输入产品版本号',
              },
            ],
          })(<Input placeholder="产品版本号"/>)}
        </FormItem>
        <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="备注">
          {form.getFieldDecorator('remark')(<Input placeholder="备注"/>)}
        </FormItem>
      </Modal>
    )
  }
}

export default Form.create()(VersionNo);
