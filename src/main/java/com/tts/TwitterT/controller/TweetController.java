package com.tts.TwitterT.controller;


import com.tts.TwitterT.model.Tweet;
import com.tts.TwitterT.model.TweetDisplay;
import com.tts.TwitterT.model.User;
import com.tts.TwitterT.service.TweetService;
import com.tts.TwitterT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TweetController {

    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @GetMapping(value = {"/tweets", "/"})
    public String getFeed(@RequestParam(value = "filter", required = false) String filter,
                          Model model) {

        User loggedInUser = userService.getLoggedInUser();

        List<TweetDisplay> tweets = new ArrayList<>();

        if (filter == null) {
            filter = "all";
        }

        if (filter.equalsIgnoreCase("following")) {
            List<User> following = loggedInUser.getFollowing();
            tweets = tweetService.findAllByUsers(following);
            model.addAttribute("filter", following);
        } else {
            tweets = tweetService.findAll();
            model.addAttribute("filter", "all");
        }

        model.addAttribute("tweetList", tweets);

        return "feed";
    }

    @GetMapping(value = "/tweets/new")
    public String getTweetsFrom(Model model) {
        model.addAttribute("tweet", new Tweet());
        return "newTweet";
    }

    @PostMapping(value = "/tweets")
    public String submitTweetForm(@Valid Tweet tweet, BindingResult bindingResult,
                                  Model model) {
        User user = userService.getLoggedInUser();

        if (!bindingResult.hasErrors()) {
            tweet.setUser(user);
            tweetService.save(tweet);
            model.addAttribute("successMessage", "Tweet successfully posted.");
            model.addAttribute("tweet", new Tweet());
        }

        return "newTweet";
    }

    @GetMapping(value = "/tweets/{tag}")
    public String getTweetsByTag(@PathVariable(value = "tag") String tag,
                                 Model model) {

        List<TweetDisplay> tweets = tweetService.findAllWithTag(tag);

        model.addAttribute("tweetList", tweets);
        model.addAttribute("tag", tag);

        return "taggedTweets";
    }


}
