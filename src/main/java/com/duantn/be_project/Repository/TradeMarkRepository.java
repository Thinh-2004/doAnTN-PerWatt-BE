package com.duantn.be_project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duantn.be_project.model.TradeMark;

public interface TradeMarkRepository extends JpaRepository<TradeMark, Integer> {

    //Kiểm tra keyword
    @Query("select case when count(tm) > 0 then true else false end from TradeMark tm where tm.name like ?1")
    Boolean checkEmptyTradeMark(String keyWord);
}
