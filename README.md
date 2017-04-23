# ajaxdemo
给部门写的跨域访问相关知识培训资料。主要内容分为如下几点

> * 搭建环境测试
> * 什么是跨域访问安全错误
> * 产生跨域错误的条件
> * 解决的几种思路
> * 浏览器如何处理跨域访问
> * h5
> * 总结

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
`注意！划重点！后台的get请求是执行成功了的！`
![](/pictures/get1result.png)

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
			console.log("jsonp Loaded: ", data);
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
			console.log("jsonp Loaded: ", data);
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
			console.log("jsonp Loaded: ", data);
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
不照抄网上的语言了，用我的理解来说，就是浏览器出于安全考虑，在异步请求其他域的url的时候，会判断服务器是否允许跨域，如果不允许就会抛出跨域错误。

# 产生跨域错误的条件

1. 必须是浏览器上发出的请求
其实就是浏览器多管闲事，觉得【可能】有安全问题，所以不允许。非浏览器发生的请求没有这个问题，如你在java代码中掉任何域都不可能报这个问题。

2.必须是ajax异步请求
直接访问肯定是不会错误的。
![](/pictures/get2.png)

3. 跨域
就是协议，域名，端口任何一个不同就算跨域。

> 重点：跨域和异步请求是浏览器的概念，服务器没有跨域和异步请求的概念。

# 解决的几种思路
针对上面产生的3个条件，我们有对应的解决方法。

## 针对浏览器，指定参数让浏览器闭嘴，不检查。
以chrome为例，增加参数--disable-web-security --user-data-dir=C:\MyChromeDevUserData 启动chrome即可解决，由于实际意义不大，不单独演示，大家有兴趣本机自己尝试即可。

## 针对异步请求，使用jsonp，异步变同步
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

分别执行原来的ajax请求和新写的jsonp 请求，可以看到原来的get请求出现在XHR `XMLHttpRequest缩写`异步请求上，而jsonp请求没有出现在XHR上，只在所有请求上，可以看出jsonp是同步的请求。（简单这样理解，XMLHttpRequest也能发送同步请求。）截图中可以看到，jsonp请求的时候，jq会自动增加call函数。
![](/pictures/jsonp1.png)

![](/pictures/jsonp2.png)

如果服务器没有支持jsonp，返回的仍然是json格式，浏览器会报错（把json当做js执行了）。
![](/pictures/jsonp3.png)

如果服务器要支持jsonp，springboot下可以增加以下配置
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
![](/pictures/jsonp4.png)

重要：就算你明白了jsonp的工作原理，也不要自己编码实现jsonp，主流框架都支持jsonp的配置，直接使用即可。

### jsonp有明显的几点硬伤

- 异步变成同步了
- 返回json变成返回js了，所以服务器是要改动支持的，不是调用方一厢情愿说用就能用的。
- 由于是动态内嵌script标签，那么肯定是不支持post方法了

所以，jsonp使用越来越少，不推荐。

## 针对跨域，有2种解决方法

## 服务器返回支持跨域信息
由于是浏览器出于安全考虑才限制跨域访问，那么我们可以在服务器中返回允许跨域的信息，让浏览器知道这个服务器请求支持跨域，请求就可以正常执行。
最简单的方式是增加@CrossOrigin注解，该注解可以加在类上也可以加在方法上。默认允许所有域名跨域。

```Java
@CrossOrigin
@RestController
public class TestController 
```

再次调用所有的请求，全部成功！表明，已经可以支持跨域了。
![](/pictures/crossorigin-1.png)
![](/pictures/crossorigin-2.png)

对于post3，可以看出先发出了一个options命令咨询是否可以跨域，服务器在响应头里面告诉浏览器可以跨域，如何post3请求才真正执行。所以post3会有2条请求记录。
![](/pictures/crossorigin-2.png)


## 使用反向代理解决
既然浏览器觉得其他域名可能有安全问题，那么我们只需要把其他域名的东西变成自己域名的东西，跨域就可以解决。我们使用反向代理，代理非本域名的请求，在外面看来就是同一个系统的请求，自然不用担心跨域问题。
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
表示 /bcom开头的请求都转发到 http://b.com:8080/

然后我们把上面页面另存一份nginx.html，里面的请求地址由绝对地址 `http://b.com:8080/xxx` 改成相对地址 `/bcom/xxx`。
重新测试，全部成功！
![](/pictures/nginx.png)

可以看到所有请求都是 http://a.com/bcom/ 开头的。

# 浏览器如何处理跨域访问

