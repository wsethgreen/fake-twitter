package com.tts.TwitterT.controller;

import com.tts.TwitterT.model.Tweet;
import com.tts.TwitterT.model.TweetDisplay;
import com.tts.TwitterT.model.User;
import com.tts.TwitterT.service.TweetService;
import com.tts.TwitterT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @GetMapping(value = "/users/{username}")
    public String getUser(@PathVariable(value = "username") String username,
                          Model model) {
        // Create an object for the logged in user
        User loggedInUser = userService.getLoggedInUser();
        // Create an object for the user
        User user = userService.findByUsername(username);
        // Create a list of all the tweets by the user
        List<TweetDisplay> tweets = tweetService.findAllByUser(user);
        // Create a list of all the people the user is following
        List<User> following = loggedInUser.getFollowing();

        boolean isFollowing = false;

        for (User followedUser : following) {
            if (followedUser.getUsername().equals(username)) {
                isFollowing = true;
            }
        }

        boolean isSelfPage = loggedInUser.getUsername().equals(username);


        model.addAttribute("isSelfPage", isSelfPage);
        model.addAttribute("following", isFollowing);
        model.addAttribute("tweets", tweets);
        model.addAttribute("user", user);

        return "user";
    }

    @GetMapping(value = "/users")
    public String getUsers(@RequestParam(value = "filter", required = false) String filter,
                           Model model) {

        List<User> users = new ArrayList<>();

        User loggedInUser = userService.getLoggedInUser();

        List<User> usersFollowing = loggedInUser.getFollowing();
        List<User> usersFollowers = loggedInUser.getFollowers();

        if (filter == null) {
            filter = "all";
        }

        if (filter.equalsIgnoreCase("followers")) {
            users = usersFollowers;
            model.addAttribute("filter", "followers");
        } else if (filter.equalsIgnoreCase("following")) {
            users = usersFollowing;
            model.addAttribute("filter", "following");
        } else {
            users = userService.findAll();
            model.addAttribute("filter", "all");
        }

        model.addAttribute("users", users);
        setFollowingStatus(users, usersFollowing, model);
        setTweetCounts(users, model);

        return "user";
    }

    public void setTweetCounts(List<User> users, Model model) {
        HashMap<String, Integer> tweetCounts = new HashMap<>();

        for (User user : users) {
            List<TweetDisplay> tweets = tweetService.findAllByUser(user);
            tweetCounts.put(user.getUsername(), tweets.size());
        }

        model.addAttribute("tweet_counts", tweetCounts);

    }

    private void setFollowingStatus(List<User> users, List<User> usersFollowing,
                                    Model model) {
        HashMap<String, Boolean> followingStatus = new HashMap<>();

        String username = userService.getLoggedInUser().getUsername();

        for (User user : users) {
            if (usersFollowing.contains(user)) {
                followingStatus.put(user.getUsername(), true);
            } else if (!user.getUsername().equals(username)) {
                followingStatus.put(user.getUsername(), false);
            }
        }

        model.addAttribute("followingStatus", followingStatus);
    }


}
