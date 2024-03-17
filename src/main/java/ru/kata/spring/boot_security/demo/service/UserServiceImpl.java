package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;


import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final RoleService roleService;


    @Autowired
    public UserServiceImpl(UserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder, RoleService roleService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDao = userDao;
        this.roleService = roleService;
    }

//    @PostConstruct
//    public void initDB() {
//        Set<Role> roles = new HashSet<>(roleDao.findAll());
//        User user = new User("Rina",22,"Rina",
//                bCryptPasswordEncoder.encode("1234"));
//        user.setRoles(roles);
//        userDao.save(user);
//    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }


    @Transactional
    @Override
    public User getById(Long id) {
        return userDao.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    @Override
    public User getByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }


    @Transactional
    public void addNewUser(User user) {
        if (user.getRoles()==null) {
            user.setRoles(Set.of(roleService.getRoleUser()));
        }
        userDao.save(user);
    }


    @Transactional
    public void updateUser(User newUser) {
        User oldUser = userDao.getById(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setUsername(newUser.getUsername());
        oldUser.setAge(newUser.getAge());
        oldUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        oldUser.setRoles(newUser.getRoles());
    }


    @Transactional
    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }


}