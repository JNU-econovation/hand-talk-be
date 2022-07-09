package handtalkproject.service;

import handtalkproject.domain.entity.Day;
import handtalkproject.domain.entity.HandTalk;
import handtalkproject.repository.HandTalkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class LearningServiceTest {
    @Autowired
    HandTalkRepository handTalkRepository;

    @Autowired
    LearningService learningService;

    @Test
    @DisplayName("day에 해당하는 수어들을 조회했을 떄 잘 찾아와지는지 테스트")
    void getLearningData() {
        //given
        HandTalk handTalk = new HandTalk();
        Day day = new Day();
        day.setDay(1);
        handTalk.setDay(day);

        HandTalk handTalk2 = new HandTalk();
        Day day2 = new Day();
        day2.setDay(1);
        handTalk2.setDay(day2);

        handTalkRepository.save(handTalk);
        handTalkRepository.save(handTalk2);

        //when
        List<HandTalk> learningData = learningService.getLearningData(1);

        //then
        assertThat(learningData.size())
                  .isEqualTo(2);
    }
}