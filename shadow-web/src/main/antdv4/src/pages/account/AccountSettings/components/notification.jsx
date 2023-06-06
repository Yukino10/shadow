import { List, Switch } from 'antd';
import React, { Component, Fragment } from 'react';

class NotificationView extends Component {
  getData = () => {
    const Action = (
      <Switch
        checkedChildren="accountandaccountsettings.settings.open"
        unCheckedChildren="accountandaccountsettings.settings.close"
        defaultChecked
      />
    );
    return [
      {
        title: 'accountandaccountsettings.notification.password',
        description: 'accountandaccountsettings.notification.password-description',
        actions: [Action],
      },
      {
        title: 'accountandaccountsettings.notification.messages',
        description: 'accountandaccountsettings.notification.messages-description',
        actions: [Action],
      },
      {
        title: 'accountandaccountsettings.notification.todo',
        description: 'accountandaccountsettings.notification.todo-description',
        actions: [Action],
      },
    ];
  };

  render() {
    const data = this.getData();
    return (
      <Fragment>
        <List
          itemLayout="horizontal"
          dataSource={data}
          renderItem={item => (
            <List.Item actions={item.actions}>
              <List.Item.Meta title={item.title} description={item.description} />
            </List.Item>
          )}
        />
      </Fragment>
    );
  }
}

export default NotificationView;
