package pers.cocoadel.user.platform.service;

import pers.cocoadel.user.platform.domain.User;
import pers.cocoadel.user.platform.exception.BusinessException;
import pers.cocoadel.user.platform.repository.UserRepository;
import javax.annotation.Resource;

public class UserServiceImpl implements UserService {

    @Resource(name = "bean/UserRepository")
    private  UserRepository userRepository;


    public UserServiceImpl() {

    }


    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User selectByName(String name, String password) {
        return userRepository.getByNameAndPassword(name, password);
    }

    @Override
    public boolean signIn(String name, String password) {
        User user = selectByName(name, password);
        if(user == null){
            throw new BusinessException("用户名或者密码错误！");
        }
        return  true;
    }

    public void signUp(User user){
        User oldUser = userRepository.getByName(user.getName());
        if(oldUser != null){
            throw new BusinessException(String.format("用户名称 %s 已经存在！",oldUser.getName()));
        }
        save(user);
    }
}
