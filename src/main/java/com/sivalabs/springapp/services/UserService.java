/**
 * 
 */
package com.sivalabs.springapp.services;

import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

//import com.sivalabs.springapp.dao.UserDao;


/**
 * @author Siva
 *
 */
@Service
@Transactional
public class UserService 
{
	//private UserDao userDao;
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    private BillService billService;

    @Autowired
    private DailyOrderService dailyOrderService;
	/*
	@Autowired
	public UserService(UserDao userDao) {
		this.userDao = userDao;
	}
	*/

    public List<User> findByIsActiveTrue(){
        return userRepository.findByIsActiveTrue();
    }

	public List<User> findAll() {
		//return userDao.findAll();
		return userRepository.findAll(new Sort(Sort.DEFAULT_DIRECTION,"sector"));
	}

	public User create(User user) {
		//return userDao.create(user);
        userRepository.save(user);
        dailyOrderService.recalcuateDailyOrderForUser(user);
		return user;
	}

    public List<User> create(List<User> users) {
        //return userDao.create(user);
        for(User user:users){
            create(user);
        }
        return users;
    }

	public User findUserById(Long id) {
		//return userDao.findUserById(id);
		return userRepository.findOne(id);
	}

	public User login(String email, String password) {
		//return userDao.login(email,password);
		//return userRepository.login(email,password);
		return userRepository.findByEmailAndPassword(email,password);
	}

	public User update(User user) {
		return userRepository.save(user);
	}

	public void deleteUser(int id) {
		userRepository.delete(id);
	}

	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

    public void inActivateUser(User user){
        user.setActive(Boolean.FALSE);
        userRepository.save(user);
    }

    public User activateOrInactivate(Long userId){
        User user = userRepository.findOne(userId);
        //Once the user is deactivated then finalize the bill..
        billService.finalizeBillForUser(user, new Date());
        if(user.getActive() == Boolean.TRUE){
            user.setActive(Boolean.FALSE);
        }else {
            user.setActive(Boolean.TRUE);
        }
        userRepository.saveAndFlush(user);
        return user;
    }
	
}

