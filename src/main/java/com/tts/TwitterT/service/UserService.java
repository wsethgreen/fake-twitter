package com.tts.TwitterT.service;


import com.tts.TwitterT.model.Role;
import com.tts.TwitterT.model.User;
import com.tts.TwitterT.repo.RoleRepo;
import com.tts.TwitterT.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private UserRepo userRepo;
    private RoleRepo roleRepo;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, RoleRepo roleRepo,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<User> findAll() {
        return (List<User>) userRepo.findAll();
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public User saveNewUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepo.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepo.save(user);
    }

    public User getLoggedInUser() {
        String loggedInUser = SecurityContextHolder.getContext()
                                .getAuthentication().getName();
        return findByUsername(loggedInUser);
    }

}
