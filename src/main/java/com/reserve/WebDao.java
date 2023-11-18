package com.reserve;

import com.reserve.WebController.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WebDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    WebDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //    予約をDBに登録
    public void add(Reserve reserve) {
        String sql = "INSERT INTO reserve(id, date, time, menu, name, email, tel) VALUES(?, ?, ?, ?, ?, ?, ?)";
        int number = jdbcTemplate.update(sql, reserve.id(), reserve.date(), reserve.time(), reserve.menu(), reserve.name(),
                reserve.email(), reserve.tel());
    }

    //        同じ日時で予約されているかを確認
    public List<ReserveDate> search(LocalDate date) {
        String sql = "SELECT * FROM RESERVE WHERE date = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, date);
        List<ReserveDate> reserveDates = result.stream()
                .map((Map<String, Object> row) -> new ReserveDate(
                        ((Date) row.get("date")).toLocalDate(),
                        ((Time) row.get("time")).toLocalTime()))
                .toList();
        return reserveDates;
    }

    //    IDで予約情報を確認
    public Reserve idFind(String id) {
        String sql = "SELECT * FROM reserve WHERE id = ?";
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
            Reserve reserve = new Reserve(
                    map.get("id").toString(),
                    ((Date) (map.get("date"))).toLocalDate(),
                    ((Time) map.get("time")).toLocalTime(),
                    map.get("menu").toString(),
                    map.get("name").toString(),
                    map.get("email").toString(),
                    map.get("tel").toString());
            return reserve;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }


    //DBから予約情報の削除
    public void cancel(String id) {
        String sql = "DELETE FROM reserve WHERE id = ?";
        int delete = jdbcTemplate.update(sql, id);
    }

    //    DBの情報を変更(id)以外
    public void reserveChange(Reserve reserve) {
        String sql = "UPDATE reserve SET (date,time,menu,name,email,tel) = (?,?,?,?,?,?) WHERE id = ?";
        int change = jdbcTemplate.update(sql, reserve.date(), reserve.time(), reserve.menu(), reserve.name(), reserve.email(), reserve.tel(), reserve.id());
    }
}
