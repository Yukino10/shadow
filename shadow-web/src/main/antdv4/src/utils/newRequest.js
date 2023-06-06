/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import { extend } from 'umi-request';
import { notification } from 'antd';

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};
/**
 * 异常处理程序
 */
const errorHandler = error => {
  const { response } = error;

  if (response && response.status) {
    const errorText = codeMessage[response.status] || response.statusText;
    const { status, url } = response;
    notification.error({
      message: `请求错误 ${status}: ${url}`,
      description: errorText,
    });
  } else if (!response) {
    notification.error({
      description: '您的网络发生异常，无法连接服务器',
      message: '网络异常',
    });
  }

  return response;
};

/**
 * 配置request请求时的默认参数
 */
const newRequest = extend({
});

// request拦截器, 改变url 或 options.
newRequest.interceptors.request.use(async (url, options) => {
  console.log('request data --> ', url, options);
  const defaultOptions = {
    credentials: 'include',
  };
  const newOptions = { ...defaultOptions, ...options };
  // token添加到header
  const token = getToken();
  if (token === undefined) {
    newOptions.headers = { ...newOptions.headers };
  } else {
    newOptions.headers = { Authorization: `Bearer ${token}`, ...newOptions.headers };
  }
  if (
    newOptions.method === 'POST' ||
    newOptions.method === 'PUT' ||
    newOptions.method === 'PATCH' ||
    newOptions.method === 'DELETE'
  ) {
    if (!(newOptions.body instanceof FormData)) {
      newOptions.headers = {
        'Content-Type': 'application/json; charset=utf-8',
        ...newOptions.headers,
      };
      if (newOptions.body === undefined || newOptions.body === '' || newOptions.body == null) {
        // 避免后端在@RequestBody中配置required=false以及判空操作
        newOptions.body = '{}';
      } else {
        newOptions.body = JSON.stringify(newOptions.body);
      }
    } else {
      // newOptions.body is FormData
      newOptions.headers = {
        ...newOptions.headers,
      };
    }
  }
  return {
    url,
    options: newOptions,
  }
});

// response拦截器, 处理response
newRequest.interceptors.response.use((response, options) => {
  console.log('resp data --> ', response, options)
  const resp = checkStatus(response);
  console.log('resp data --> ', resp);
  try {
    const arryresp = resp.arrayBuffer().then(buf => {
      console.log('arrayBuffer then resp data --> ', resp)
      console.log('处理后buf -- > ', buf)
      let msg;
      try {
        msg = proto.return_message.deserializeBinary(buf);
        // console.log("这个是pb解密吗，感觉不对 --> ",msg)
      } catch (e) {
        console.log(`newRequest error:${e}`);
        return { msgCode: 999, msg: `decode failed:${e}` };
      }
      const ret = {};
      ret.msgCode = isEmpty(msg.getMsgCode()) ? 0 : msg.getMsgCode();
      ret.msg = msg.getMsg();
      ret.data = isEmpty(msg.getData()) ? {} : JSON.parse(msg.getData());
      // console.log("这个是返回的值看看是什么”",ret);
      return ret;
    }).catch(error => {
      console.log(error)
    });
  } catch (e) {
    console.log('解析response异常 -> ', e)
    return { msgCode: 0 }; // 反正都登出了，返回0就算更改数据也无所谓了
  }
});

export default newRequest;
