import defaultSettings from './defaultSettings'; // https://umijs.org/config/

import slash from 'slash2';
import webpackPlugin from './plugin.config';
const { pwa, primaryColor } = defaultSettings; // preview.pro.ant.design only do not use in your production ;
// preview.pro.ant.design 专用环境变量，请不要在你的项目中使用它。

const { ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION } = process.env;
const isAntDesignProPreview = ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION === 'site';
const plugins = [
  [
    'umi-plugin-react',
    {
      antd: true,
      dva: {
        hmr: true,
      },
      locale: {
        // default false
        enable: true,
        // default zh-CN
        default: 'zh-CN',
        // default true, when it is true, will use `navigator.language` overwrite default
        baseNavigator: true,
      },
      // dynamicImport: {
      //   loadingComponent: './components/PageLoading/index',
      //   webpackChunkName: true,
      //   level: 3,
      // },
      pwa: pwa
        ? {
            workboxPluginMode: 'InjectManifest',
            workboxOptions: {
              importWorkboxFrom: 'local',
            },
          }
        : false, // default close dll, because issue https://github.com/ant-design/ant-design-pro/issues/4665
      // dll features https://webpack.js.org/plugins/dll-plugin/
      // dll: {
      //   include: ['dva', 'dva/router', 'dva/saga', 'dva/fetch'],
      //   exclude: ['@babel/runtime', 'netlify-lambda'],
      // },
    },
  ],
  [
    'umi-plugin-pro-block',
    {
      moveMock: false,
      moveService: false,
      modifyRequest: true,
      autoAddMenu: true,
    },
  ],
]; // 针对 preview.pro.ant.design 的 GA 统计代码

if (isAntDesignProPreview) {
  plugins.push([
    'umi-plugin-ga',
    {
      code: 'UA-72788897-6',
    },
  ]);
}

export default {
  plugins,
  history: 'hash',
  // Ant Design Pro 使用的 Umi 支持两种路由方式：browserHistory 和 hashHistory。https://pro.ant.design/docs/deploy-cn
  targets: {
    ie: 11,
  },
  // umi routes: https://umijs.org/zh/guide/router.html
  routes: [
    {
      path: '/',
      component: '../layouts/BlankLayout',
      routes: [
        {
          path: '/',
          redirect: '/user',
        },
        {
          path: '/user',
          component: '../layouts/UserLayout',
          routes: [
            {
              path: '/user',
              redirect: '/user/login',
            },
            {
              name: 'login',
              icon: 'smile',
              path: '/user/login',
              component: './user/login',
            },
            {
              name: 'register-result',
              icon: 'smile',
              path: '/user/register-result',
              component: './user/register/registerResult',
            },
            {
              name: 'register',
              icon: 'smile',
              path: '/user/register',
              component: './user/register',
            },
            {
              component: './exception/404',
            },
          ],
        },
        {
          path: '/admin',
          component: '../layouts/BasicLayout',
          //Routes: ['src/pages/Authorized'],
          authority: ['am_product_view', 'am_device_view'],
          routes: [
            {
              path: '/admin',
              redirect: '/admin/welcome',
            },
            {
              path: '/admin/product',
              name: 'product',
              icon: 'dashboard',
              authority: ['am_product_view', 'am_device_view'],
              routes: [
                {
                  name: 'define',
                  icon: 'smile',
                  path: '/admin/product/define',
                  authority: ['am_product_view'],
                  component: './product/define',
                },
                {
                  name: '产品列表',
                  icon: 'book',
                  path: '/admin/product/list',
                  authority: ['am_product_view'],
                  component: './product/list',
                },
                {
                  name: 'devices',
                  icon: 'smile',
                  path: '/admin/product/devices',
                  authority: ['am_device_view'],
                  component: './product/devices',
                },
                {
                  path: '/admin/product/devices/propType',
                  component: './product/devices/DeviceDetail',
                },
              ],
            },
            {
              path: '/admin/welcome',
              name: 'welcome',
              icon: 'smile',
              component: './Welcome',
            },
            {
              path: '/admin/account',
              name: 'account',
              icon: 'smile',
              routes: [
                {
                  name: '个人中心',
                  icon: 'smile',
                  path: '/admin/account/center',
                  component: './account/AccountCenter',
                },
                {
                  name: '个人设置',
                  icon: 'smile',
                  path: '/admin/account/settings',
                  component: './account/AccountSettings',
                },
              ],
            },
            {
              path: '/admin/authority',
              name: '权限管理',
              icon: 'setting',
              authority: ['cm_user_view', 'cm_role_view'],
              routes: [
                {
                  name: '人员管理',
                  icon: 'user',
                  path: '/admin/authority/user',
                  component: './authority/users',
                  authority: ['cm_user_view'],
                },
                {
                  name: '角色管理',
                  icon: 'team',
                  path: '/admin/authority/role',
                  component: './authority/role',
                  authority: ['cm_role_view'],
                },
              ],
            },
            {
              component: './exception/404',
            },
            {
              component: './exception/403',
            },
          ],
        },
      ],
    },
  ],
  // Theme for antd: https://ant.design/docs/react/customize-theme-cn
  theme: {
    'primary-color': primaryColor,
  },
  define: {
    ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION:
      ANT_DESIGN_PRO_ONLY_DO_NOT_USE_IN_YOUR_PRODUCTION || '', // preview.pro.ant.design only do not use in your production ; preview.pro.ant.design 专用环境变量，请不要在你的项目中使用它。
  },
  ignoreMomentLocale: true,
  lessLoaderOptions: {
    javascriptEnabled: true,
  },
  disableRedirectHoist: true,
  cssLoaderOptions: {
    modules: true,
    getLocalIdent: (context, _, localName) => {
      if (
        context.resourcePath.includes('node_modules') ||
        context.resourcePath.includes('ant.design.pro.less') ||
        context.resourcePath.includes('global.less')
      ) {
        return localName;
      }

      const match = context.resourcePath.match(/src(.*)/);

      if (match && match[1]) {
        const antdProPath = match[1].replace('.less', '');
        const arr = slash(antdProPath)
          .split('/')
          .map(a => a.replace(/([A-Z])/g, '-$1'))
          .map(a => a.toLowerCase());
        return `antd-pro${arr.join('-')}-${localName}`.replace(/--/g, '-');
      }

      return localName;
    },
  },
  manifest: {
    basePath: '/',
  },
  //chainWebpack: webpackPlugin,
  proxy: {
    '/admin/**': 'http://localhost:8080/',
    '/auth/**': 'http://localhost:8080/', // '/api': 'http://localhost:8080/'
  },
};
