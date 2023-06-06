import { Icon, List } from 'antd';
import React, { Component, Fragment } from 'react';

class BindingView extends Component {
  getData = () => [
    {
      title: 'accountandaccountsettings.binding.taobao',
      description: 'accountandaccountsettings.binding.taobao-description',
      actions: [<a key="Bind">Bind</a>],
      avatar: <Icon type="taobao" className="taobao" />,
    },
    {
      title: 'accountandaccountsettings.binding.alipay',
      description: 'accountandaccountsettings.binding.alipay-description',
      actions: [<a key="Bind">Bind</a>],
      avatar: <Icon type="alipay" className="alipay" />,
    },
    {
      title: 'accountandaccountsettings.binding.dingding',
      description: 'accountandaccountsettings.binding.dingding-description',
      actions: [<a key="Bind">Bind</a>],
      avatar: <Icon type="dingding" className="dingding" />,
    },
  ];

  render() {
    return (
      <Fragment>
        <List
          itemLayout="horizontal"
          dataSource={this.getData()}
          renderItem={item => (
            <List.Item actions={item.actions}>
              <List.Item.Meta
                avatar={item.avatar}
                title={item.title}
                description={item.description}
              />
            </List.Item>
          )}
        />
      </Fragment>
    );
  }
}

export default BindingView;
