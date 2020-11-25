package com.mail.smtp.repository;

import com.mail.smtp.entity.MailBoxEntity;
import com.mail.smtp.entity.MailListEntity;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/*@SpringBootTest(classes = {
        MailBoxRepository.class,
        MailListRepository.class
})*/
@SpringBootTest
class MailListRepositoryTest
{
    @Autowired
    private MailBoxRepository mailBoxRepository;

    @Autowired
    private MailListRepository mailListRepository;

    @BeforeEach
    public void setup()
    {
    }

    @Test
    @DisplayName("JSON 데이터 가져오기")
    @Transactional
    public void mailListTest()
    {
        Optional<MailBoxEntity> optBox = mailBoxRepository.findByAidxAndName(1, MailBoxEntity.MBOX_NAME_SENT);
        assertTrue(optBox.isPresent());

        Optional< List<MailListEntity> > optMailList = mailListRepository.findByMbox(optBox.get());
        assertTrue(optMailList.isPresent());

        List<MailListEntity> maillist = optMailList.get();
        maillist.stream().forEach(System.out::println);
    }
}