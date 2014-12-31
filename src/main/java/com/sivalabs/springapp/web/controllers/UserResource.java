/**
 * 
 */
package com.sivalabs.springapp.web.controllers;

import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Siva
 *
 */
@RestController
@RequestMapping("/rest/user/")
public class UserResource {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> findAll()
	{
        List<User> users  = userService.findAll();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("users",users);
        userMap.put("success",Boolean.TRUE);
        userMap.put("total",users.size());
        userMap.put("message","All good working as expected");
		return new ResponseEntity<Map<String, Object>>(userMap,HttpStatus.OK) ;
	}
	
	@RequestMapping(value="{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public User findUser(@PathVariable("id") Long id) {
		return userService.findUserById(id);
	}

    @RequestMapping(value="/status/{id}", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User activateOrInactivate(@PathVariable("id") Long userId) {
        return userService.activateOrInactivate(userId);
    }
	
	@RequestMapping(value="/", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<User> createUser(@RequestBody User user) {
		User savedUser = userService.create(user);
		return new ResponseEntity<User>(savedUser, HttpStatus.CREATED);
	}

   /* @RequestMapping(value="/batch", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<User>> createUser(@RequestBody List<User> users) {
        List<User> savedUsers = userService.create(users);
        return new ResponseEntity<List<User>>(savedUsers, HttpStatus.CREATED);
    }*/
	
	@RequestMapping(value="/", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		User savedUser = userService.update(user);
		return new ResponseEntity<User>(savedUser, HttpStatus.OK);
	}
	
	@RequestMapping(value="{id}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Void> deleteUser(@PathVariable("id") int id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
