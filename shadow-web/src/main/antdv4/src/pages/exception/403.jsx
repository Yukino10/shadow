import { Button, Result } from 'antd';
import React from 'react';
import router from 'umi/router';
// 这里应该使用 antd 的 404 result 组件，
// 但是还没发布，先来个简单的。

const NoAuthPage = () => (
  <Result
    status="403"
    title="403"
    subTitle="Sorry, You have no authority."
    extra={
      <Button type="primary" onClick={() => router.push('/')}>
        Back Home
      </Button>
    }
  ></Result>
);

export default NoAuthPage;
