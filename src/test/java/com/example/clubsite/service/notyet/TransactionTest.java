package com.example.clubsite.service.notyet;


import com.example.clubsite.service.chatroom.ChatRoomService;
import com.example.clubsite.service.chat.ChatService;
import com.example.clubsite.service.memberchatroom.MemberChatRoomService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@Log4j2
@ActiveProfiles(profiles = "dv")
class TransactionTest {

    @Autowired
    private MemberChatRoomService memberChatRoomService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }


    @Test
    public void printConnectionInfo() {
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); // true
        chatService.addChat(159L,10L,"transactionTest1!");
        TransactionStatus outer2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer2.isNewTransaction()={}", outer2.isNewTransaction()); // true
    }
}



