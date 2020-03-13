package by.zapolski.database;

import by.zapolski.database.dao.ExampleDao;
import by.zapolski.database.dao.RuleDao;
import by.zapolski.database.dao.WordDao;
import by.zapolski.database.model.Example;
import by.zapolski.database.model.Rule;
import by.zapolski.database.model.Word;

public class Main {
    public static void main(String[] args) {

        RuleDao ruleDao = new RuleDao();
        ruleDao.clearTable();
        Rule rule = new Rule();
        rule.setValue("a very important rule");
        ruleDao.create(rule);
        System.out.println("Expected one record in RULE: " + ruleDao.getAll());
        rule.setValue("a very important rule (changed)");
        ruleDao.update(rule);
        System.out.println("Expected one changed record in RULE: " + ruleDao.getAll());
        rule.setValue("");
        rule = ruleDao.getById(rule.getId());
        System.out.println("Expected got by Id object in RULE: " + rule);
        ruleDao.remove(rule.getId());
        System.out.println("Expected empty table in RULE: " + ruleDao.getAll());
        ruleDao.create(rule);

        System.out.println();

        WordDao wordDao = new WordDao();
        wordDao.clearTable();
        System.out.println("Expected empty table in WORD: " + wordDao.getAll());
        Word word = new Word();
        word.setValue("begin");
        wordDao.create(word);
        System.out.println("Expected 1 record in WORD: " + wordDao.getAll());
        word.setValue("begin (changed)");
        wordDao.update(word);
        System.out.println("Expected one changed record in WORD: " + wordDao.getAll());
        word.setValue("");
        word = wordDao.getById(word.getId());
        System.out.println("Expected got by Id object in WROD: " + word);
        wordDao.remove(word.getId());
        System.out.println("Expected empty table in WORD: " + wordDao.getAll());
        wordDao.create(word);

        System.out.println();

        ExampleDao exampleDao = new ExampleDao();
        System.out.println("Expected empty table in EXAMPLES: " + exampleDao.getAll());

        Example example = new Example();
        example.setWordId(word.getId());
        example.setRussian("русский");
        example.setEnglish("english");
        example.setSound("sound");
        example.setRuleId(rule.getId());

        exampleDao.create(example);
        System.out.println("Expected 1 record in EXAMPLES: " + exampleDao.getAll());
        example.setRussian("русский2");
        example.setEnglish("english2");
        exampleDao.update(example);
        System.out.println("Expected one changed record in EXAMPLES: " + exampleDao.getAll());
        exampleDao.remove(example.getId());
        System.out.println("Expected empty table in EXAMPLES: " + exampleDao.getAll());
        exampleDao.create(example);
        ruleDao.remove(rule.getId());
        System.out.println("Expected one record in table EXAMPLES with NULL rule_id: " + exampleDao.getAll());
        wordDao.remove(word.getId());
        System.out.println("Expected two empty tables in WORD and EXAMPLES: " + wordDao.getAll() + " " + exampleDao.getAll());

        wordDao.clearTable();
        wordDao.close();
        exampleDao.clearTable();
        exampleDao.close();
        ruleDao.clearTable();
        ruleDao.close();
    }
}
