import React from 'react';
import { Card, Typography, Alert, Divider } from 'antd';
import { PageHeaderWrapper } from '@ant-design/pro-layout';

const { Text } = Typography;
const { Title } = Typography;
const { Paragraph } = Typography;
const { Link } = Typography;

const CodePreview = ({ children }) => (
  <pre
    style={{
      background: '#f2f4f5',
      padding: '12px 20px',
      margin: '12px 0',
    }}
  >
    <Text>{children}</Text>
  </pre>
);

export default () => (
  <PageHeaderWrapper>
    <Card>
      <Alert
        message="欢迎使用物联网设备管理平台"
        type="success"
        showIcon
        banner
        style={{
          margin: -12,
          marginBottom: 24,
        }}
      />
      <Title level={3}>预用服务列表</Title>

      <Typography>
        <Text strong>
          <a href="https://www.rabbitmq.com/download.html">RabbitMQ消息中间件</a>
        </Text>
        <CodePreview>
          <h5>版本：稳定版 3.7.15</h5>
          <h5>
            地址：<Text copyable>https://www.rabbitmq.com/download.html</Text>
          </h5>
          <Text>
            <h5>简介：</h5>
            <Paragraph>
              RabbitMQ是实现了高级消息队列协议（AMQP）的开源消息代理软件（亦称面向消息的中间件）。RabbitMQ服务器是用Erlang语言编写的，
              <br />
              而聚类和故障转移是构建在开放电信平台框架上的。所有主要的编程语言均有与代理接口通讯的客户端库。
            </Paragraph>
          </Text>
        </CodePreview>
        <Text
          strong
          style={{
            marginBottom: 12,
          }}
        >
          <a rel="noopener noreferrer" href="http://docs.minio.org.cn/docs/">
            MinIO对象数据库
          </a>
        </Text>
        <CodePreview>
          <h5>版本：稳定版 3.6</h5>
          <h5>
            地址：<Text copyable>http://docs.minio.org.cn/docs/</Text>
          </h5>
          <Text>
            <h5>简介：</h5>
            <Paragraph>
              MinIO是在Apache License v2.0下发布的对象存储服务器.
              它与AmazonS3云存储服务兼容。它最适合存储非结构化数据，
              <br />
              如照片，视频，日志文件，备份和容器映像。 对象的大小可以从几KB到最大5TB。
            </Paragraph>
          </Text>
        </CodePreview>
        <Text
          strong
          style={{
            marginBottom: 12,
          }}
        >
          <a
            target="_blank"
            rel="noopener noreferrer"
            href="http://support.ntp.org/bin/view/Support/InstallingNTP"
          >
            NTP授时服务
          </a>
        </Text>
        <CodePreview>
          <h5>版本：稳定版 3.6</h5>
          <h5>
            地址：<Text copyable>http://support.ntp.org/bin/view/Support/InstallingNTP</Text>
          </h5>
          <Text>
            <h5>简介：</h5>
            <Paragraph>
              NTP服务器[Network Time
              Protocol（NTP）]是用来使计算机时间同步化的一种协议，它可以使计算机对其服务器或时钟源（如石英钟，GPS等等)做同步化，
              <br />
              它可以提供高精准度的时间校正（LAN上与标准间差小于1毫秒，WAN上几十毫秒）。
            </Paragraph>
          </Text>
        </CodePreview>
        <Text
          strong
          style={{
            marginBottom: 12,
          }}
        >
          <a
            target="_blank"
            rel="noopener noreferrer"
            href="https://help.aliyun.com/product/31815.html?spm=5176.7933691.J_7985555940.2.2e394c59egxx44"
          >
            OSS阿里对象存储服务
          </a>
        </Text>
        <CodePreview>
          <h5>版本：稳定版 2.5.0</h5>
          <h5>
            地址：
            <Text copyable>
              https://help.aliyun.com/product/31815.html?spm=5176.7933691.J_7985555940.2.2e394c59egxx44
            </Text>
          </h5>
          <Text>
            <h5>简介：</h5>
            <Paragraph>
              阿里云对象存储服务（Object Storage
              Service，简称OSS），是阿里云对外提供的海量、安全、低成本、高可靠的云存储服务。
              <br />
              您可以通过本文档提供的简单的REST接口，在任何时间、任何地点、任何互联网设备上进行上传和下载数据。基于OSS，您可以搭建出各种多媒体分享网站、网盘、个人和企业数据备份等基于大规模数据的服务。
            </Paragraph>
          </Text>
        </CodePreview>
      </Typography>
    </Card>
    <p
      style={{
        textAlign: 'center',
        marginTop: 24,
      }}
    >
      Want to add more pages? Please refer to{' '}
      <a href="https://pro.ant.design/docs/block-cn" target="_blank" rel="noopener noreferrer">
        use block
      </a>
      。
    </p>
  </PageHeaderWrapper>
);
