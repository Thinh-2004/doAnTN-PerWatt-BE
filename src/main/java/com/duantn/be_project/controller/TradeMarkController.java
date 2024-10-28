package com.duantn.be_project.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duantn.be_project.Repository.TradeMarkRepository;
import com.duantn.be_project.model.TradeMark;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@CrossOrigin("*")
@RestController
public class TradeMarkController {
    @Autowired
    TradeMarkRepository tradeMarkRepository;

    //GetAll
    @GetMapping("/brand")
    public ResponseEntity<List<TradeMark>> getAll(Model model) {
        List<TradeMark> tradeMarks = tradeMarkRepository.findAll();
        tradeMarks.sort(Comparator.comparing((TradeMark t) -> t.getName()));
        return ResponseEntity.ok(tradeMarks);
    }

    //GetById
    @GetMapping("/brand/{id}")
    public ResponseEntity<TradeMark> getById(@PathVariable("id") Integer id) {
        TradeMark tradeMark = tradeMarkRepository.findById(id).orElseThrow();
        if(tradeMark == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tradeMark);
    }

    //Post
    @PostMapping("/brand")
    public ResponseEntity<TradeMark> post(@RequestBody TradeMark tradeMark) {
        //TODO: process POST request
        if(tradeMarkRepository.existsById(tradeMark.getId())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tradeMarkRepository.save(tradeMark));
    }

    //Put
    @PutMapping("/brand/{id}")
    public ResponseEntity<TradeMark> put(@PathVariable("id") Integer id, @RequestBody TradeMark tradeMark) {
        //TODO: process PUT request
        if(!tradeMarkRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tradeMarkRepository.save(tradeMark));
    }
    
     @DeleteMapping("/brand/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        // TODO: process PUT request
        if (!tradeMarkRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        tradeMarkRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
}
