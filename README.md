 ```markdown
# 事件上报系统 Demo

## 一、整体架构设计

### 技术栈
| 组件 | 技术选型 |
|------|----------|
| 语言 | Kotlin |
| 架构模式 | MVVM (Model-View-ViewModel) |
| 依赖注入 | Hilt |
| 本地数据库 | Room |
| 网络请求 | Retrofit (模拟) |
| 异步处理 | Kotlin Coroutines |
| UI 框架 | View System + ViewBinding |

### 项目结构

```text
app/src/main/java/com/lue/eventreport/
├── EventReportApplication.kt      # Application 入口，Hilt 入口
├── MainActivity.kt                 # 主界面 Activity
├── data/
│   ├── local/
│   │   ├── EventDatabase.kt       # Room 数据库
│   │   ├── EventDao.kt            # 数据访问对象
│   │   └── EventTypeConverters.kt # 类型转换器 (Map -> JSON)
│   ├── model/
│   │   ├── Event.kt               # 事件数据模型
│   │   └── EventStatus.kt         # 事件状态枚举
│   ├── remote/
│   │   └── MockNetworkClient.kt   # 模拟网络客户端
│   └── repository/
│       └── EventRepository.kt     # 数据仓库
├── di/
│   ├── DatabaseModule.kt          # 数据库依赖注入模块
│   ├── NetworkModule.kt           # 网络依赖注入模块
│   └── RepositoryModule.kt        # 仓库依赖注入模块
└── ui/
    ├── adapter/
    │   └── EventAdapter.kt        # RecyclerView 适配器
    └── viewmodel/
        └── EventViewModel.kt      # ViewModel
```

### 核心功能实现

#### 统一的事件格式

```kotlin
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @SerializedName("event_name")
    val eventName: String,

    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @SerializedName("properties")
    val properties: Map<String, Any> = emptyMap(),

    val status: EventStatus = EventStatus.PENDING,
    val retryCount: Int = 0
)
```

#### 重试策略
- 自动重试机制，最多重试 3 次
- 每次失败后增加重试计数
- 超过 3 次标记为永久失败

#### 本地持久化
- 使用 Room 数据库存储所有事件
- 支持 App 崩溃或退出后数据不丢失
- 下次启动可继续处理未发送事件

#### 异步处理
- 所有数据库和网络操作在 IO 线程执行
- 使用 Kotlin Coroutines 进行异步处理
- 不阻塞主线程，保证 UI 流畅

#### 可插拔的发送方式
- `MockNetworkClient`: 模拟网络发送（30% 失败率）
- 支持通过 Hilt 轻松替换为真实的 Retrofit 服务

### 数据流程图

```
用户操作 → ViewModel → Repository → Room 数据库 (持久化)
                                          ↓
                              MockNetworkClient (模拟发送)
                                          ↓
                    成功 → 更新状态为 SUCCESS
                    失败 → 重试 (最多 3 次) → FAILED
```

---

## 二、如何运行 Demo

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 11 或更高版本
- Android SDK 24+ (最低支持版本)
- Gradle 8.0+

### 运行步骤

#### 1. 克隆项目
```bash
git clone <repository-url>
cd EventReport
```

#### 2. 同步 Gradle
- 打开 Android Studio
- 点击 "Sync Project with Gradle Files"
- 等待依赖下载完成

#### 3. 运行应用
- 连接 Android 设备或启动模拟器 (API 24+)
- 点击 Run 按钮 (或使用快捷键 Shift+F10)
- 应用将安装并启动

### 使用说明

#### 选择事件
从下拉框中选择一个预定义的事件：
- 用户进入首页
- 用户通过关卡
- 用户点击按钮
- 用户登录
- 用户退出
- 用户购买道具
- 用户分享应用
- 用户观看广告

#### 发送事件
1. 点击"发送事件"按钮
2. 事件会被记录到数据库
3. 自动尝试发送到服务器（模拟）
4. 在下方列表中查看事件状态

#### 测试重试机制
- 勾选"模拟网络失败"
- 开启后所有发送都会失败
- 观察事件的重试次数增加
- 3 次失败后状态变为"失败"

#### 查看事件列表
| 图标 | 状态 | 说明 |
|------|------|------|
| ⏳ | 待发送 | 等待发送的事件 |
| 📤 | 发送中 | 正在发送 |
| ✅ | 已发送 | 发送成功 |
| ❌ | 失败 | 超过重试次数 |

---

## 三、可以继续改进的地方

### 网络层
- [ ] 集成真实的 Retrofit + OkHttp 网络请求
- [ ] 添加真实的 API 接口和服务器地址
- [ ] 实现网络状态检测（WiFi/4G/5G）
- [ ] 添加请求超时和取消机制
- [ ] 实现请求拦截器和响应拦截器

### 安全性
- [ ] 添加 API 认证机制（Token/OAuth）
- [ ] 实现数据加密传输（HTTPS + 数据加密）
- [ ] 添加事件签名防篡改
- [ ] 实现请求频率限制

### 性能优化
- [ ] 实现事件批量发送（减少网络请求次数）
- [ ] 添加事件压缩（减少流量消耗）
- [ ] 优化数据库查询性能（添加索引）
- [ ] 实现事件分页加载（避免一次性加载过多数据）

### 可靠性
- [ ] 添加 WorkManager 实现定时任务（即使 App 未启动也能发送）
- [ ] 实现网络恢复后自动重试
- [ ] 添加事件去重机制
- [ ] 实现事件优先级队列（重要事件优先发送）

### 监控与日志
- [ ] 集成日志系统（Timber/Log4j）
- [ ] 添加崩溃上报
- [ ] 实现发送成功率统计
- [ ] 添加性能监控（ANR 检测）

### 用户体验
- [ ] 添加事件过滤和搜索功能
- [ ] 实现事件导出功能（JSON/CSV）
- [ ] 添加批量操作（批量删除、批量重发）
- [ ] 优化 UI 界面（Material Design 3）

### 架构扩展
- [ ] 支持多种事件类型（点击事件、页面事件、自定义事件）
- [ ] 实现事件模板功能
- [ ] 添加 AOP 切面自动埋点
- [ ] 支持插件化架构

---

## 四、AI 使用说明

### 使用的 AI 助手
**通义灵码 (Lingma)**

### 使用方式

#### 代码生成
通过自然语言描述需求，让 AI 生成基础代码结构
> 例如："创建一个 Room Database，包含事件表"

#### 代码优化
询问代码中的错误和解决方案
> 例如："为什么 Room 报错说无法保存 Map 类型？"

#### 架构建议
咨询最佳实践和架构设计
> 例如："如何使用 Hilt 进行依赖注入？"

#### 问题调试
粘贴错误日志，获取解决方案
> 例如："Hilt Activity must be attached to an @HiltAndroidApp Application"

#### 代码审查
检查代码中的潜在问题
> 例如："为什么数据库中没有成功的或失败的事件？"

### AI 贡献比例
约 **60%** 的代码由 AI 辅助生成，**40%** 由人工修改和优化
```
