package by.zapolski.service;

import by.zapolski.database.dao.RuleDao;
import by.zapolski.database.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleDao ruleDao;

    @Override
    public Rule getRuleById(Integer id) {
        return ruleDao.getById(id);
    }

    @Override
    public List<Rule> getAll() {
        return ruleDao.getAll();
    }

    @Override
    public boolean addRule(Rule rule){
        return ruleDao.create(rule);
    }

    @Override
    public boolean updateRule(Rule rule) {
        return ruleDao.update(rule);
    }

    @Override
    public boolean deleteRule(Integer id) {
        return ruleDao.remove(id);
    }

}
