package com.jtausage.com.controllers;

import com.jtausage.com.configs.JMSDestinations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by danielnaves on 02/12/16.
 */
@RestController
public class XaAPIRestController {

    private final JmsTemplate jmsTemplate;
    private final JdbcTemplate jdbcTemplate;

    public XaAPIRestController(JmsTemplate jmsTemplate, JdbcTemplate jdbcTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Collection<Map<String, String>> read() {
        return this.jdbcTemplate.query("SELECT * FROM MESSAGE", new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
                Map<String, String> msg = new HashMap<String, String>();
                msg.put("id", resultSet.getString("ID"));
                msg.put("message", resultSet.getString("MESSAGE"));
                return msg;
            }
        });
    }

    @PostMapping
    @Transactional
    public void write(@RequestBody Map<String, String> payload, @RequestParam Optional<Boolean> rollback) {
        String id = UUID.randomUUID().toString();
        String name = payload.get("name");
        String msg = "Hello, " + name;
        this.jdbcTemplate.update("INSERT INTO MESSAGE (ID, MESSAGE) VALUES (?, ?)", id, msg);
        this.jmsTemplate.convertAndSend(JMSDestinations.queueDestinationPoc, msg);
        if (rollback.orElse(false)) {
            throw new RuntimeException("Não foi possível realizar a transação");
        }
    }

}
