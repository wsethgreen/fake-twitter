package com.tts.TwitterT.service;


import com.tts.TwitterT.model.Tag;
import com.tts.TwitterT.model.Tweet;
import com.tts.TwitterT.model.TweetDisplay;
import com.tts.TwitterT.model.User;
import com.tts.TwitterT.repo.TagRepo;
import com.tts.TwitterT.repo.TweetRepo;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TweetService {

    @Autowired
    private TweetRepo tweetRepo;

    @Autowired
    private TagRepo tagRepo;

    public List<TweetDisplay> findAll() {
        List<TweetDisplay> tweets = tweetRepo.findAllByOrderByCreatedAtDesc();
        return tweets;
    }

    public List<TweetDisplay> findAllByUser(User user) {
        List<TweetDisplay> tweets = tweetRepo.findAllByUserOrderByCreatedAtDesc(user);
        return tweets;
    }

    public List<TweetDisplay> findAllByUsers(List<User> users) {
        List<TweetDisplay> tweets = tweetRepo.findAllByUserInOrderByCreatedAtDesc(users);
        return tweets;
    }

    public List<TweetDisplay> findAllWithTag(String tag) {
        List<TweetDisplay> tweets = tweetRepo.findByTags_PhraseOrderByCreatedAtDesc(tag);
        return tweets;
    }

    public void save(Tweet tweet) {
        handleTags(tweet);
        tweetRepo.save(tweet);
    }


    private void handleTags(Tweet tweet) {
        List<Tag> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(tweet.getMessage());

        while (matcher.find()) {
            String phrase = matcher.group().substring(1).toLowerCase();
            Tag tag = tagRepo.findByPhrase(phrase);

            if (tag == null) {
                tag = new Tag();
                tag.setPhrase(phrase);
                tagRepo.save(tag);
            }
            // add each tag to the list of tags
            tags.add(tag);
        }
        // set the tags for the tweet
        tweet.setTags(tags);
    }

    private List<TweetDisplay> formatTweets(List<Tweet> tweets) {
        addTagLinks(tweets);
        shortenLinks(tweets);
        List<TweetDisplay> tweetDisplay = formatTimestamps(tweets);
        return tweetDisplay;
    }

    private void addTagLinks(List<Tweet> tweets) {
        Pattern pattern = Pattern.compile("#\\w+");

        for (Tweet tweet : tweets) {

            String message = tweet.getMessage();
            Matcher matcher = pattern.matcher(message);
            Set<String> tags = new HashSet<>();

            while(matcher.find()) {
                tags.add(matcher.group());
            }

            for (String tag : tags) {
                message = message.replaceAll(tag,
                        "<a class=\"tag\" href=\"/tweets/" + tag.substring(1).toLowerCase() + "\">" + tag + "</a>");
            }

            tweet.setMessage(message);
        }
    }

    private void shortenLinks(List<Tweet> tweets) {

        Pattern pattern = Pattern.compile("https?[^ ]+");

        for (Tweet tweet : tweets) {

            String message = tweet.getMessage();
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String link = matcher.group();
                String shortenedLink = link;

                if (link.length() > 23) {
                    shortenedLink = link.substring(0, 20) + "...";
                    message = message.replace(link,
                            "<a class=\"tag\" href=\"" + link + "\" target=\"_blank\">" + shortenedLink + "</a>");
                }

                tweet.setMessage(message);
            }
        }
    }

    private List<TweetDisplay> formatTimestamps(List<Tweet> tweets) {

        List<TweetDisplay> response = new ArrayList<>();
        PrettyTime prettyTime = new PrettyTime();
        SimpleDateFormat simpleDate = new SimpleDateFormat("M/d/yy");
        Date now = new Date();

        for (Tweet tweet : tweets) {
            TweetDisplay tweetDisplay = new TweetDisplay();
            tweetDisplay.setUser(tweet.getUser());
            tweetDisplay.setMessage(tweet.getMessage());
            tweetDisplay.setTags(tweet.getTags());
            long diffInMillies = Math.abs(now.getTime() - tweet.getCreatedAt().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diff > 3) {
                tweetDisplay.setDate(simpleDate.format(tweet.getCreatedAt()));
            } else {
                tweetDisplay.setDate(prettyTime.format(tweet.getCreatedAt()));
            }
            response.add(tweetDisplay);
        }

        return response;
    }

}
