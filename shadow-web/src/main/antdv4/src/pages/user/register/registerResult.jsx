import React from 'react';
import { formatMessage, FormattedMessage } from 'umi/locale';
import { Button } from 'antd';
import Link from 'umi/link';
import Result from '@/components/Result';
import styles from './styles.less';

const actions = (
  <div className={styles.actions}>
    <Link to="/user/login">
      <Button size="large">返回登录页</Button>
    </Link>
  </div>
);

const RegisterResult = ({ location }) => (
  <Result
    className={styles.registerResult}
    type="success"
    title={<div className={styles.title}>你的账户：{location.state.account} 注册成功</div>}
    description="注册成功，请返回登录页面登录账户。"
    actions={actions}
    style={{ marginTop: 56 }}
  />
);

export default RegisterResult;
