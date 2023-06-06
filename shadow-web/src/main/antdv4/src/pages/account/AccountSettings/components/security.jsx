import React, { Component, Fragment } from 'react';
import { List } from 'antd';

const passwordStrength = {
  strong: <span className="strong">Strong</span>,
  medium: <span className="medium">Medium</span>,
  weak: <span className="weak">Weak Weak</span>,
};

class SecurityView extends Component {
  getData = () => [
    {
      title: 'accountandaccountsettings.security.password',
      description: (
        <Fragment>
          accountandaccountsettings.security.password-description ：{passwordStrength.strong}
        </Fragment>
      ),
      actions: [<a key="Modify">Modify</a>],
    },
    {
      title: 'accountandaccountsettings.security.phone',
      description: `${'accountandaccountsettings.security.phone-description'}：138****8293`,
      actions: [<a key="Modify">Modify</a>],
    },
    {
      title: 'accountandaccountsettings.security.question',
      description: 'accountandaccountsettings.security.question-description',
      actions: [<a key="Set">Set</a>],
    },
    {
      title: 'accountandaccountsettings.security.email',
      description: `${'accountandaccountsettings.security.email-description'}：ant***sign.com`,
      actions: [<a key="Modify">Modify</a>],
    },
    {
      title: 'accountandaccountsettings.security.mfa',
      description: 'accountandaccountsettings.security.mfa-description',
      actions: [<a key="bind">Bind</a>],
    },
  ];

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

export default SecurityView;
