package com.tts.TwitterT.controller;


import com.tts.TwitterT.model.User;
import com.tts.TwitterT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/signup")
    public String registration(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping(value = "signup")
    public String createNewUser(@Valid User user, BindingResult bindingResult,
                                Model model) {
        // Create variable to determine if user already exists
        User userExists = userService.findByUsername(user.getUsername());

        // if user already exists, reject the value and display error message
        if (userExists != null) {
            bindingResult.rejectValue("username", "error.user",
                    "Username already exists.");
        }
        // if there are no errors, add the new user
        if (!bindingResult.hasErrors()) {
            userService.saveNewUser(user);
            model.addAttribute("success", "Sign up successful");
            model.addAttribute("user", new User());
            return "registrationSuccess";
        }

        return "registration";

    }

}
