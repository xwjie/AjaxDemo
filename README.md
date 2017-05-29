# ajaxdemo
给部门写的跨域访问相关知识培训资料。主要内容分为如下几点

> * 搭建环境测试
> * 什么是跨域访问安全错误
> * 产生跨域错误的条件
> * 解决的几种思路
> * 带cookie的跨域请求
> * 带自定义header的跨域请求
> * 总结
> * 培训的问题列表

![大纲](/pictures/all.png) 


# 搭建环境测试
1. 使用springboot搭建后台服务，编写get请求。
```Java
@RestController
public class TestController {
	@GetMapping("/get1")
	public ResultBean<String> get1() {
		System.out.println("\n-------TestController.get1()\n");
		return new ResultBean<String>("get1 ok");
	}
}
```
2. 配置host，把a.com和b.com都指向本地

![host配置](/pictures/hosts.png) 

3. 编写请求页面，使用jq发送get的ajax请求，请求地址中使用b.com的绝对地址
```JavaScript
function get1() {
	$.get("http://b.com:8080/get1", function(data) {
		console.log("get1 Loaded: ", data);
	});
}
```

4. 访问a.com,点击get的ajax请求，发生跨域错误

![](/pictures/get1.png)

> 注意！划重点！后台的get请求是执行成功了的！虽然前台报跨域错误！

![](/pictures/get1-2.png)

返回码是200。

![](/pictures/get1result.png)

后台代码正常执行。

5. 编写无参数post请求post1
```Java
@PostMapping("/post1")
public ResultBean<String> post1() {
	System.out.println("\n--------TestController.post1()\n");
	return new ResultBean<String>("post1 ok");
}
```

6. 编写前台调用代码
```JavaScript
function post1() {
	$.ajax({
		type : "POST",
		url : "http://b.com:8080/post1",
		dataType : "json",
		success : function(data) {
			console.log("post1 Loaded: ", data);
		}
	});
}
```

7. 执行post1，前台报跨域错误，后台同样执行成功！

![](/pictures/post1.png)

![](/pictures/post1-2.png)

8. 编写带参数post请求post2，参数格式为form-urlencoded格式（就是a=1&b=2这种）
```Java
@PostMapping("/post2")
public ResultBean<String> post2(Param param) {
	System.out.println("\n--------TestController.post2, param=" + param);
	return new ResultBean<String>("post2 ok, param=" + param);
}
```

9. 编写前台调用代码
```JavaScript
function post2() {
	var data = {
		key1 : '以form-urlencoded格式发送参数',
		id1 : 12345
	}

	$.ajax({
		type : "POST",
		data : data,
		url : "http://b.com:8080/post2",
		dataType : "json",
		success : function(data) {
			console.log("post2 Loaded: ", data);
		}
	});
}

```

10. 执行post2，前台报跨域错误，后台同样执行成功！
![](/pictures/post2-1.png)
![](/pictures/post2-2.png)

11. 编写带参数post请求post3，参数格式为json格式
```Java
@PostMapping("/post3")
public ResultBean<String> post3(@RequestBody Param param) {
	System.out.println("\n--------TestController.post3, param=" + param);
	return new ResultBean<String>("post3 ok, param=" + param);
}
```

12. 编写前台调用代码
```JavaScript
function post3() {
	var data = {
		key1 : '以json格式发送',
		id1 : 12345
	}

	$.ajax({
		type : "POST",
		contentType : "application/json",
		data : JSON.stringify(data),
		url : "http://b.com:8080/post3",
		dataType : "json",
		success : function(data) {
			console.log("post3 Loaded: ", data);
		}
	});
}
```

10. 执行post3，前台报跨域错误，`后台没有执行post3代码`！
![](/pictures/post3.png)
可以看出post2是post命令，而且返回码是200，就是服务器已经正确执行了。
而post3是options命令，返回的是403，服务器并没有执行命令！
原因后面会解析。


5. 测试结束

# 什么是跨域访问安全错误
不照抄网上的语言了，用我的理解来说，就是浏览器出于安全考虑，在XMLHttpRequest请求其他域的url的时候，会判断服务器是否允许跨域，如果不允许就会抛出跨域错误。

# 产生跨域错误的条件

1. 必须是浏览器上发出的请求

其实就是浏览器多管闲事，觉得【可能】有安全问题，所以不允许。非浏览器发生的请求没有这个问题，如你在java代码中掉任何域都不可能报这个问题。

2.必须是XMLHttpRequest请求

直接访问肯定是不会错误的。

![](/pictures/get2.png)

3. 跨域

就是协议，域名，端口任何一个不同就算跨域。

> 重点：跨域和异步请求是浏览器的概念，服务器没有跨域和异步请求的概念。

# 解决的几种思路
针对上面产生的3个条件，我们有对应的解决方法。

## 针对浏览器，指定参数让浏览器闭嘴，不检查。
以chrome为例，增加参数--disable-web-security --user-data-dir=C:\MyChromeDevUserData 启动chrome即可解决，由于实际意义不大，不单独演示，大家有兴趣本机自己尝试即可。

## 针对XMLHttpRequest请求，使用jsonp
`jsonp`是比较上古的方式了，现在很多老系统还能看到。jsonp其实就插入了一个script标签来【同步】加载代码。服务器由原来的返回json数据，变成返回了调用函数的【js脚本】给浏览器执行，这个函数就是jsonp里面的callback函数，函数名是需要前台传给后台的。

我们来测试一下，编写json调用代码。主要就是 `dataType: "jsonp"`
```JavaScript
function getByJsonp(){
	$.ajax({
		  type: "GET",
		  url: "http://b.com:8080/get1",
		  dataType: "jsonp",
		  success: function(data){
			  console.log("jsonp Loaded: " , data);
		  }
		});
}
```

分别执行原来的ajax请求和新写的jsonp 请求，可以看到原来的get请求出现在XHR `XMLHttpRequest缩写`异步请求上，而jsonp请求没有出现在XHR上，只在所有请求上，可以看出`jsonp不是XHR请求`。（其实jsonp的实现机制的动态插入script标签实现的，script标签在非ie浏览器在H5下也支持异步。）

截图中可以看到，jsonp请求的时候，jq会自动增加call函数。

![](/pictures/jsonp1.png)

![](/pictures/jsonp2.png)

如果服务器没有支持jsonp，返回的仍然是json格式，浏览器会报错（把json当做js执行了）。

![](/pictures/jsonp3.png)

如果服务器要支持jsonp，springboot下可以增加以下配置 `AbstractJsonpResponseBodyAdvice`

```Java
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {
	public JsonpAdvice() {
		super("callback");
	}
}
```
增加后会自动判断是否是jsonp，如果是json就返回对应的js。

再次调用，已经能正确获取数据打印结果了。

发送的请求，带上了jq随机生成的函数名 `jQuery111309350735532809726_1493608521320` 。

```
http://b.com:8080/get1?callback=jQuery111309350735532809726_1493608521320&_=1493608521322
```

返回的结果，是调用 `jQuery111309350735532809726_1493608521320` 函数的js语句。

```
/**/jQuery111309350735532809726_1493608521320({"code":0,"msg":"success","data":"get1 ok"});
```

![](/pictures/jsonp5.png)

![](/pictures/jsonp4.png)

> 重要：就算你明白了jsonp的工作原理，也不要自己编码实现jsonp，主流框架都支持jsonp的配置，直接使用即可。

### jsonp有明显的几点硬伤

- 返回json变成返回js了，所以服务器是要改动支持的，不是调用方一厢情愿说用就能用的。
- 由于是动态内嵌script标签，那么肯定是不支持post方法了

所以，jsonp使用越来越少，不推荐。

## 针对跨域，有2种解决方法

### 服务器返回支持跨域信息
由于是浏览器出于安全考虑才限制跨域访问，那么我们可以在服务器中返回允许跨域的信息，让浏览器知道这个服务器请求支持跨域，请求就可以正常执行。

最简单的方式是增加@CrossOrigin注解，该注解可以加在类上也可以加在方法上。默认允许所有域名跨域。

```Java
@CrossOrigin
@RestController
public class TestController 
```

再次调用所有的请求，全部成功！表明，已经可以支持跨域了。

返回允许跨域信息

![](/pictures/crossorigin-0.png)

![](/pictures/crossorigin-1.png)

![](/pictures/crossorigin-2.png)

对于post3，可以看出先发出了一个`OPTIONS咨询命令` 看是否可以跨域，服务器在响应头里面告诉浏览器可以跨域，然后post3请求才真正执行。

所以post3会有2条请求记录。

![](/pictures/crossorigin-3.png)

> `OPTIONS咨询命令` 并不是每次都会发送，第一次查询的返回的头里面有一条 `Access-Control-Max-Age:1800`，是表示有效期，这个时间段不会再次发送 `OPTIONS咨询命令` 了。 

### 使用反向代理解决

既然浏览器觉得其他域名可能有安全问题，那么我们只需要把其他域名的东西变成自己域名的东西，跨域就可以解决。

我们使用反向代理，代理非本域名的请求，在外面看来就是同一个系统的请求，自然不用担心跨域问题。

以nginx配置为例，配置非常简单，配置如下：
```
 server {
        listen       80;
        server_name  a.com;

	location / {
	    proxy_pass http://a.com:8080/;
        }

        location /bcom/ {
	    proxy_pass http://b.com:8080/;
        }
}
```
表示 `/bcom` 开头的请求都转发到 http://b.com:8080/

然后我们把上面页面另存一份nginx.html，里面的请求地址由绝对地址 `http://b.com:8080/xxx` 改成相对地址 `/bcom/xxx`。

重新测试，全部成功！

![](/pictures/nginx.png)

可以看到所有请求都是 http://a.com/bcom/ 开头的。

# 带cookie的跨域请求
默认跨域都是不带cookie的。

但我们很多时候需要发送cookie（如会话等），这种情况发送XMLHttpRequest请求的时候，**客户端**需要设置 `withCredentials` 为true，然后**服务端**需要返回支持cookie配置，需要返回 `Access-Control-Allow-Credentials : true` 和 `Access-Control-Allow-Origin : 对应的域名` ，注意：`此处不能用*，必须是具体的域名`。

编写js代码

```JavaScript
function getWithCookie() {
	$.ajax({
		type : "GET",
		url : "http://b.com:8080/getWithCookie",
		xhrFields : {
			withCredentials : true
		},
		success : function(data) {
			console.log("getWithCookie Loaded: ", data);
		}
	})
}
```

编写java代码，后台使用spring的@CookieValue注解获取cookie值。

```Java
@GetMapping("/getWithCookie")
public ResultBean<String> getWithCookie(@CookieValue(required=false) String cookie1) {
	System.out.println("\n-------TestController.getWithCookie(), cookie1="+cookie1);
	return new ResultBean<String>("getWithCookie ok, cookie1="+cookie1);
}
```

然后，在b.com上添加对应的cookie（使用工具或者document.cookie上增加），从a.com上发送getWithCookie请求到b.com，成功。

![](/pictures/cookie1.png)

如果发送的请求里面没有对应的cookie，会报错

```
{"timestamp":1493552031929,"status":400,"error":"Bad Request","exception":"org.springframework.web.bind.ServletRequestBindingException","message":"Missing cookie 'cookie1' for method parameter of type String","path":"/getWithCookie"}
```

服务器设置 `@CookieValue(required=false)` 即可

# 带自定义header的跨域请求

很多时候，我们需要发送自定义的header，这个时候首先先要在服务器配置能接受哪些header。并使用 `@RequestHeader` 得到头字段。

```Java
@GetMapping("/getWithHeader")
@CrossOrigin(allowedHeaders = { "X-Custom-Header1", "X-Custom-Header2", "X-Custom-Header4" })
public ResultBean<String> getWithHeader(
		@RequestHeader(required = false, name = "X-Custom-Header1") String header1) {
	System.out.println("\n-------TestController.getWithHeader(), header1=" + header1);
	return new ResultBean<String>("getWithHeader ok, header1=" + header1);
}
```

> 注意，`@CrossOrigin(allowedHeaders = { "X-Custom-Header1", "X-Custom-Header2", "X-Custom-Header4" })`需要配置在方法上，不要配在类上面的 `@CrossOrigin` 注解上，否则会导致一些问题。

编写js代码，JQ里面增加自定义头有2种方法。`headers` 和 `beforeSend事件` 加。 

```JavaScript
function getWithHeader() {
	$.ajax({
		type : "GET",
		url : "http://b.com:8080/getWithHeader",
		headers : {
			"X-Custom-Header1" : "can not include zhongwen1111"
		},
		beforeSend : function(xhr) {
			xhr.setRequestHeader("X-Custom-Header2", "can not include zhongwen2222");
			xhr.setRequestHeader("X-Custom-Header3", "can not include zhongwen3333");
		},
		success : function(data) {
			console.log("getWithHeader Loaded: ", data);
		},
		error:function(data) {
			console.log("getWithHeader error: ", data);
		},
	})
}

```

> **注意1：一开始用 jquery.1.6.1 上面代码怎么样都发送不成功，后面换了 1.11.3 版本成功了。**

> 注意2：header里面的值不能直接放中文，中文必须自己编码。

> 注意3：自定义头都使用 `X-` 开头，养成好习惯。

发送请求前，先发送 `OPTIONS咨询命令` 看看服务器是否允许发送这些自定义头。

![](/pictures/header1.png)

> 发送请求的头会包含此次所有的自定义头列表，使用Spring`@CrossOrigin` 注解支持跨域的时候，服务器返回服务器支持的头和你请求的头的**交集**。服务器并没有告诉你所有支持的头。

Options命令会返回200（成功），里面会包含允许的列表，浏览器**自己判断**不一样，就会报错。

```
XMLHttpRequest cannot load http://b.com:8080/getWithHeader. Request header field X-Custom-Header3 is not allowed by Access-Control-Allow-Headers in preflight response.
```
![](/pictures/header2.png)

我们修改上面js代码，去掉服务器没有配置的 `X-Custom-Header3` ， 重新测试，取值成功。

![](/pictures/header3.png)

![](/pictures/header4.png)

# 总结

本着工匠精神就写细一点，发现东西还是比较多的，本来觉得写4个小时应该就能写完了，结果周末花了快2天才写完，最后总结一下，对工作中用得上的知识点。

* 发生跨域访问的三个条件：浏览器端，跨域，异步。
* 针对异步的解决方法jsonp有很多硬伤，并不推荐。
* 浏览器发送跨域请求之前会区分简单请求还是非简单请求，**简单请求**是直接请求，请求完再根据响应头信息判断（如果不支持跨域，尽管服务器成功执行返回200，但浏览器还是报错），**非简单请求**会先发送 `OPTIONS咨询命令`（如果不支持跨域，返回403禁止访问错误，支持则返回200，但并不一定就代表该请求能发出去，某些情况服务器还需要额外判断）。
* 工作中遇到比较常见的非简单请求就是发送json数据的和带自定义头的。（带cookie的是简单请求）
* 使用Spring的 `@CrossOrigin` 能很方便的解决跨域访问问题，几乎只需要一行代码。
* 使用反向代理也是比较好的解决方法，公司内部配置也比较简单，反向代理能封装很多细节，增加很多其他特性。
* 学会注解 `@RequestHeader` 和 `@CookieValue` 的使用，不要自己去request对象上获取这些信息。

# 培训的问题列表

培训中问的问题列表，单独列出来供大家参考。

## jsonp为什么只支持get，不支持post？
jsonp不是使用xhr发送的，是使用动态插入script标签实现的，当前无法指定请求的method，只能是get。调用的地方看着一样，实际上和普通的ajax有2点明显差异：1. 不是使用xhr 2.服务器返回的不是json数据，而是js代码。

## 在a上跨域访问b的时候，发送cookie的时候发送的是a.com 的还是 b.com 的cookie？
这个很明显是发送b.com的。cookie都是发送请求的url的域名上的。b.com上面不可能访问到a.com的cookie的。

那为什么实践中，调用其他公司内的子系统，会有a.com上的一些cookie？那是因为公司的sso单点登录的时候，把cookie设置到一级域名 `.huawei.com` ,而且 `hostOnly` 为false，所以二级域名都能访问到对应的cookie。所以同一个大域名下公司做单点登录太简单了。 

另外说一点：`cookie不区分端口`。



