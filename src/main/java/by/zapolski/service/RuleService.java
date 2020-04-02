package by.zapolski.service;

import by.zapolski.database.model.Rule;

import java.util.List;

public interface RuleService {

    Rule getRuleById(Integer id);

    List<Rule> getAll();

    boolean addRule(Rule rule);

    boolean updateRule(Rule rule);

    boolean deleteRule(Integer id);
}
