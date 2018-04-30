package cn.xiaowenjie;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cn.xiaowenjie.beans.Param;
import cn.xiaowenjie.beans.ResultBean;

@RestController
public class XSSController {

	@GetMapping("/xss1")
	public ResultBean<String> xss1() throws InterruptedException {
		System.out.println("\n-------xss1()\n");
		return new ResultBean<String>(
				"\u003cimg src=1 onerror=alert(/xss333333333333/) \u003e");
	}

}
