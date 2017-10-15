# 《图解 HTTP》笔记
## 第 1 章  了解 Web 及网络基础

HTTP: HyperText Transfer Protocol, 超文本传输协议。

PS: HTTP 通常被译为超文本传输协议，但并不严谨（只是已经约定俗成）。严谨的译名应该为“超文本转移协议”。



WWW: World Wide Web, 万维网。

HTML: HyperText Markup Language, 超文本标记语言。

URL: Uniform Resource Locator, 统一资源定位符。

URI: Uniform Resource Identifier, 统一资源标识符。

FTP: File Transfer Protocol, 文件传输系统。

DNS: Domain Name System, 域名系统，提供域名到 IP 地址之间的解析服务。

NIC: Network Interface Card, 网络适配器，即网卡。

IP: Internet Protocol, 网际协议。

MAC 地址: Media Access Control Address, 网卡所属的固定地址。

ARP: Address Resolution Protocol, 地址解析协议，根据 IP 地址获取其对应的 MAC 地址。



传输层：

TCP: Transmission Control Protocol, 传输控制协议。TCP 位与传输层，提供可靠的字节流服务。

所谓字节流服务是指：为了方便传输，将大块数据分割成以报文段（segment）为单位的数据包进行管理。

三次握手示意图：

【PICS】



UDP: User Data Protocol, 用户数据报协议。



## 第 2 章  简单的 HTTP 协议

请求访问文本或图像等资源的一端成为客户端，提供资源响应的一端称为服务端。



HTTP 是一种不保存状态，即无状态（stateless）协议。HTTP 协议自身不对请求和响应状态进行保存。

原因：为了更快地处理大量事务，确保协议的可伸缩性（为了保存用户的状态，引入了 Cookie 技术）。



### 告知服务器意图的 HTTP 方法

- GET: 获取资源
- POST: 传输实体主体
- PUT: 传输文件
- HEAD: 获得报文首部
- DELETE: 删除文件
- OPTIONS: 询问支持的方法
- TRACE: 追踪路径
- CONNECT: 要求用隧道协议连接代理



SSL: Secure Sockets Layer, 安全套接层。

TLS: Transport Layer Security, 传输层安全。

### 持久连接节省通信量

HTTP 协议的初始版本中，每进行一次 HTTP 通信就要断开一次 TCP 连接：

【TCP 连接】



持久连接（HTTP Persistent Connections，也称为 HTTP keep-alive 或 HTTP connection reuse）：

【TCP 持久连接】

持久连接的特点：只要任意一端没有明确提出断开连接，则保持 TCP 连接状态。

持久连接旨在建立 1 次 TCP 连接后进行多次请求和响应的交互。

持久连接的好处在于减少了 TCP 连接的重复建立和断开所造成的额外开销，减轻了服务端的负载。

在 HTTP/1.1 中，所有的连接默认都是持久连接。



### 管线化

持久连接使得多数请求以管线化（pipelining）方式发送成为可能。

这样能够做到同时并行发送多个请求，而不需要一个接一个地等待响应了。

【管线化】



### 使用 Cookie 的状态管理

HTTP 是无状态协议，它不对之前发生过的请求和响应的状态进行管理。

Cookie 技术通过在请求和响应报文中写入 Cookie 信息来控制客户端的状态。

Cookie 会根据从服务器端发送的响应报文内的一个叫做 Set-Cookie 的首部字段信息，通知客户端保存 Cookie。当下次客户端再往该服务器发送请求时，客户端会自动在请求报文中加入 Cookie 值后发送出去。

服务器端发现客户发送过来的 Cookie 后，回去检查是从哪一个客户端发来的连接请求，然后对比服务器上的记录，最后得到之前的状态信息。

【Cookie 图1】

【Cookie 图2】



## 第 3 章  HTTP 报文内的 HTTP 信息



- HTTP 报文

用于 HTTP 协议交互的信息被称为 HTTP 报文。请求端（客户端）的 HTTP 报文叫做请求报文，响应端（服务器端）的叫做响应报文。HTTP 报文本身是由多行数据构成的字符串文本。

【HTTP 报文结构】



### 报文& 实体

- 报文（message）

是 HTTP 通信中的基本单位，由 8 位组字节流（octet sequence）组成，通过 HTTP 通信传输。

- 实体（entity）

作为请求或响应的有效载荷数据（补充项）被传输，其内容由实体首部和实体主体组成。

HTTP 报文的主体用于传输请求或响应的实体主体。通常，报文主体等于实体主体。只有当传输中进行编码操作时，实体主体的内容才会发生变化，导致二者的差异。



- 内容编码

【pic】



- 分块传输编码（Chunked Transfer Coding）

【pic】



发送邮件时，我们可以在邮件里写入文字并添加多份附件，这是因为采用了 MIME（Multipurpose Internet Mail Extensions, 多用途因特网邮件扩展）机制，它允许邮件处理文本、图片、视频等多个不同类型的数据。



- 内容协商返回最合适的内容

同一个 Web 网站有可能存在着多份相同内容的页面。比如英文版和中文版的 Web 页面，它们内容上虽相同，但使用的语言却不同。这样的机制称为内容协商（Content Negotiation）。例如：

有 3 种类型：

1. 服务器驱动协商（Server-driven Negotiation）
2. 客户端驱动协商（Agent-drive Negotiation）
3. 透明协商（Transparent Negotiation）



## 第 4 章  返回结果的 HTTP 状态码

HTTP 状态码负责表示客户端 HTTP 请求的返回结果、标记服务器端的处理是否正常、通知出现的错误等工作。



状态码如 200 OK，以 3 位数字和原因短语组成。数字中的第一位指定了响应类别，后两位无分类。响应类别有以下五种：

|      | 类别                     | 原因短语          |
| ---- | ---------------------- | ------------- |
| 1XX  | Informational（信息性状态码）  | 接收的请求正在处理     |
| 2XX  | Success（成功状态码）         | 请求正常处理完毕      |
| 3XX  | Redirection（重定向状态码）    | 需要进行附加操作以完成请求 |
| 4XX  | Client Error（客户端错误状态码） | 服务器无法处理请求     |
| 5XX  | Server Error（服务端错误状态码） | 服务器处理请求出错     |



### 常用状态码举例



#### 2XX 成功

- 200 OK

从客户端发来的请求在服务器端被正常处理了。

- 204 No Content

服务器接收的请求已成功处理，但返回的响应报文不含实体的主体部分。

- 206 Partial Content

Partial Content，示意图：



#### 3XX 重定向

- 301 Moved Permanently

永久性重定向。请求的资源已经被分配了新的 URI，以后应使用资源现在所指的 URI。

- 302 Found

临时性重定向。请求的资源已经被分配了新的 URI，希望用户（本次）能使用新的 URI 访问。

#### 4XX 客户端错误

- 400 Bad Request

请求报文中存在语法错误。

- 401 Unauthorized

发送的请求需要有同过 HTTP 认证的认证信息。

- 403 Forbidden

该状态码表明对请求资源的访问被服务器拒绝了。服务器端没有必要给出拒绝的详细理由。

- 404 Not Found

服务器上无法找到请求的资源。

 #### 5XX 服务器错误

- 500 Internal Server Error

服务器端在执行请求时发生了错误。也可能是 Web 应用存在的 bug 或某些临时的故障。

- 503 Service Unavailable

服务器暂时处于超负荷或正在进行停机维护，现在无法处理请求。











