package by.zapolski.controller;

import by.zapolski.database.model.Rule;
import by.zapolski.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RuleController {

    @Autowired
    RuleService ruleService;

    @GetMapping("/rules")
    public List<Rule> getAllWords() {
        return ruleService.getAll();
    }

    @GetMapping("/rules/{id}")
    public ResponseEntity<Rule> getRuleById(@PathVariable Integer id) {
        return new ResponseEntity<>(ruleService.getRuleById(id), HttpStatus.OK);
    }

    @PostMapping("/rules")
    public ResponseEntity<Rule> addRule(@RequestBody Rule rule) {
        ruleService.addRule(rule);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rules")
    public ResponseEntity<Rule> updateRule(@RequestBody Rule rule) {
        ruleService.updateRule(rule);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<Rule> deleteRule(@PathVariable Integer id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

}
