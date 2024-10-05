package com.bhim.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bhim.binding.AuthRequest;
import com.bhim.model.UserEntity;
import com.bhim.service.JwtService;
import com.bhim.service.MyUserDetailesService;



@RestController
@RequestMapping("/api")
public class UserRestController {

	Logger logger=LoggerFactory.getLogger(UserRestController.class);
	@Autowired
   private MyUserDetailesService userService;

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JwtService jwtservice;

	@PostMapping("/login")
	public ResponseEntity<String> loginCheck(@RequestBody AuthRequest request) {
		UsernamePasswordAuthenticationToken token = 
				new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPwd());

		try {
			Authentication authenticate = authManager.authenticate(token);

			if (authenticate.isAuthenticated()) {
				String jwtToken = jwtservice.generateToken(request.getUserName());
				return new ResponseEntity<>(jwtToken, HttpStatus.OK);
			}

		} catch (Exception e) {
			logger.warn("Exception occures");
		}

		return new ResponseEntity<String>("Invalid Credentials", HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/register")
	public ResponseEntity<String> registration(@RequestBody UserEntity user) {
		String encodedPwd = pwdEncoder.encode(user.getPwd());
        user.setPwd(encodedPwd);
		boolean saveUser = userService.saveUser(user);
		if(saveUser) {
			return new ResponseEntity<String>("User Registered", HttpStatus.CREATED);
		}
		return new ResponseEntity<String>("Invalid Data", HttpStatus.BAD_REQUEST);
	}
	@GetMapping("/msg")
	public String  greetMsg() {
		String msg="Good morning";
		return msg;
	}

	@GetMapping("/welcome/{name}")
	public String  welcomeMsg(@PathVariable String name) {
		String msg="Wel come to Andhra Pradesh:  "+name;
		return msg;
	}

}
