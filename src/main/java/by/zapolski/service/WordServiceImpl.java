package by.zapolski.service;

import by.zapolski.database.dao.WordDao;
import by.zapolski.database.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordServiceImpl implements WordService {

    @Autowired
    WordDao wordDao;

    @Override
    public List<Word> getAllWords() {
        return wordDao.getAll();
    }
}
