package com.tts.TwitterT.repo;


import com.tts.TwitterT.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepo extends CrudRepository<Tag, Long> {

    Tag findByPhrase(String phrase);

}
