package by.zapolski.controller;

import by.zapolski.database.model.Word;
import by.zapolski.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WordController {

    @Autowired
    WordService wordService;

    @GetMapping("/words")
    public List<Word> getAllWords() {
        return wordService.getAllWords();
    }
}
