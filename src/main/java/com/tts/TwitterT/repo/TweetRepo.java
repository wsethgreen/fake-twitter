package com.tts.TwitterT.repo;

import com.tts.TwitterT.model.Tweet;
import com.tts.TwitterT.model.TweetDisplay;
import com.tts.TwitterT.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepo extends CrudRepository<Tweet, Long> {

    List<TweetDisplay> findAllByOrderByCreatedAtDesc();

    List<TweetDisplay> findAllByUserOrderByCreatedAtDesc(User user);

    List<TweetDisplay> findAllByUserInOrderByCreatedAtDesc(List<User> users);

    List<TweetDisplay> findByTags_PhraseOrderByCreatedAtDesc(String phrase);

}
