/**
 *
 */
package com.sivalabs.springapp;

import com.sivalabs.springapp.config.AppConfig;
import com.sivalabs.springapp.entities.User;
import com.sivalabs.springapp.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Siva
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

	/*@Test
    public void findAllUsers()  {
		List<User> users = userService.findAll();
		assertNotNull(users);
		assertTrue(!users.isEmpty());
	}
	
	@Test
	public void findUserById()  {
		User user = userService.findUserById(1);
		assertNotNull(user);
	}*/

    @Test
    @Transactional
    @Rollback(true)
    public void createUser() {
        String uuid = UUID.randomUUID().toString();
        User user = new User();
        user.setEmail(uuid + "@gmail.com");
        user.setName("Some name");
        user.setSector("Sector");
        user.setPhone("1112223333");
        user.setGivenSerialNumber(1);
        user.setOrderStartDate(new Date());
        User savedUser = userService.create(user);
        assertNotNull("User just got saved there should be ID associated", user.getId());
        User newUser = userService.findUserById(savedUser.getId());
        assertEquals("Some name", newUser.getName());
        assertEquals(uuid + "@gmail.com", newUser.getEmail());
    }

   // @Test
    //@Transactional
    //@Rollback(true)
    public void createBatchUser() {
        for (int i = 1; i < 11; i++) {
            User user = new User();
            user.setEmail("siva" + i + "@gmail.com");
            user.setName("Samba Siva " + i);
            user.setSector("Sector " + i);
            user.setPhone("1112223333");
            user.setCmPrice((double) 45);
            user.setBmPrice((double) 55);
            user.setDailyBmOrder((double) i);
            user.setDailyCmOrder((double) i + 1);
            user.setActive(Boolean.TRUE);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -3);
            user.setOrderStartDate(calendar.getTime());
            user.setPassword("samba123");
            User savedUser = userService.create(user);
            assertNotNull("User just got saved there should be ID associated", user.getId());
            User newUser = userService.findUserById(savedUser.getId());
            assertEquals("Samba Siva " + i, newUser.getName());
            assertEquals("siva" + i + "@gmail.com", newUser.getEmail());
        }
    }


}
