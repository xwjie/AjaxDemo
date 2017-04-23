package cn.xiaowenjie;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.xiaowenjie.beans.Param;
import cn.xiaowenjie.beans.ResultBean;

@CrossOrigin
@RestController
public class TestController {

	@GetMapping("/get1")
	public ResultBean<String> get1() {
		System.out.println("\n-------TestController.get1()\n");
		return new ResultBean<String>("get1 ok");
	}

	@PostMapping("/post1")
	public ResultBean<String> post1() {
		System.out.println("\n--------TestController.post1()\n");
		return new ResultBean<String>("post1 ok");
	}

	@PostMapping("/post2")
	public ResultBean<String> post2(Param param) {
		System.out.println("\n--------TestController.post2, param=" + param);
		return new ResultBean<String>("post2 ok, param=" + param);
	}

	@PostMapping("/post3")
	public ResultBean<String> post3(@RequestBody Param param) {
		System.out.println("\n--------TestController.post3, param=" + param);
		return new ResultBean<String>("post3 ok, param=" + param);
	}
}
