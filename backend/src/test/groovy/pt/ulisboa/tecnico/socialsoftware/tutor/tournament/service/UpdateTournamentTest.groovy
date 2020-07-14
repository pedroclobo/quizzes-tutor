package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UpdateTournamentTest extends SpockTest {
    def topic1
    def topic2
    def topic3
    def tournamentDto
    def topics = new HashSet<Integer>()
    def user1
    def user2

    def setup() {
        user1 = new User(USER_1_NAME, USER_1_USERNAME, User.Role.STUDENT)
        user2 = new User(USER_2_NAME, USER_2_USERNAME, User.Role.STUDENT)

        user1.addCourse(courseExecution)
        userRepository.save(user1)
        user1.setKey(user1.getId())

        user2.addCourse(courseExecution)
        userRepository.save(user2)
        user2.setKey(user2.getId())

        def topicDto1 = new TopicDto()
        topicDto1.setName(TOPIC_1_NAME)
        topic1 = new Topic(course, topicDto1)
        topicRepository.save(topic1)

        def topicDto2 = new TopicDto()
        topicDto2.setName(TOPIC_2_NAME)
        topic2 = new Topic(course, topicDto2)
        topicRepository.save(topic2)

        def topicDto3 = new TopicDto()
        topicDto3.setName(TOPIC_3_NAME)
        topic3 = new Topic(course, topicDto3)
        topicRepository.save(topic3)

        topics.add(topic1.getId())
        topics.add(topic2.getId())

        tournamentDto = new TournamentDto()
        tournamentDto.setStartTime(STRING_DATE_TOMORROW)
        tournamentDto.setEndTime(STRING_DATE_LATER)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setState(Tournament.Status.NOT_CANCELED)
    }

    def "user that created tournament changes start time"() {
        given:
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "new startTime"
        def newStartTime = STRING_DATE_TOMORROW_PLUS_10_MINUTES
        tournamentDto.setStartTime(newStartTime)

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        DateHandler.toISOString(result.getStartTime()) == newStartTime
    }

    def "user that created tournament changes end time"() {
        given:
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "new endTime"
        def newEndTime = STRING_DATE_LATER_PLUS_10_MINUTES
        tournamentDto.setEndTime(newEndTime)

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        DateHandler.toISOString(result.getEndTime()) == newEndTime
    }

    def "user that created tournament changes number of questions"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "a new number of questions"
        def newNumberOfQuestions = 10
        tournamentDto.setNumberOfQuestions(newNumberOfQuestions)

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.numberOfQuestions == newNumberOfQuestions
    }

    def "user that created tournament adds topic of same course"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "a new topics list"
        topics.add(topic3.getId())

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopicConjunction().getTopics() == [topic2, topic3, topic1]  as Set
    }

    def "user that created tournament adds topic of different course"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "new course"
        def differentCourse = new Course(COURSE_2_NAME, Course.Type.TECNICO)
        courseRepository.save(differentCourse)

        and: "a new topics list"
        topic3.setCourse(differentCourse)
        topics.add(topic3.getId())

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_TOPIC_COURSE
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopicConjunction().getTopics() == [topic2, topic1]  as Set
    }

    def "user that created tournament removes existing topic from tournament that contains that topic"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "a new topics list"
        topics.remove(topic2.getId())

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopicConjunction().getTopics() == [topic1] as Set
    }

    def "user that created an open tournament tries to change it"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_IS_OPEN
        tournamentRepository.count() == 1L
    }

    def "user that created tournament tries to change it after has ended"() {
        given: "a tournament"
        tournamentDto.setStartTime(STRING_DATE_TODAY)
        tournamentDto.setEndTime(STRING_DATE_TODAY)
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        when:
        tournamentService.updateTournament(user1.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_ALREADY_CLOSED
        tournamentRepository.count() == 1L
    }

    def "user that not created tournament changes start time"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "new startTime"
        def oldStartTime = tournamentDto.getStartTime()
        def newStartTime = STRING_DATE_TOMORROW_PLUS_10_MINUTES
        tournamentDto.setStartTime(newStartTime)

        when:
        tournamentService.updateTournament(user2.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        DateHandler.toISOString(result.getStartTime()) == oldStartTime
    }

    def "user that not created tournament changes end time"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "new endTime"
        def oldEndTime = tournamentDto.getEndTime()
        def newEndTime = STRING_DATE_LATER_PLUS_10_MINUTES
        tournamentDto.setEndTime(newEndTime)

        when:
        tournamentService.updateTournament(user2.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        DateHandler.toISOString(result.getEndTime()) == oldEndTime
    }

    def "user that not created tournament changes number of questions"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "a new number of questions"
        def newNumberOfQuestions = 10
        tournamentDto.setNumberOfQuestions(newNumberOfQuestions)

        when:
        tournamentService.updateTournament(user2.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
    }

    def "user that not created tournament adds topic"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "a new topics list"
        topics.add(topic3.getId())

        when:
        tournamentService.updateTournament(user2.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopicConjunction().getTopics() == [topic2, topic1]  as Set
    }

    def "user that not created tournament removes topic"() {
        given: "a tournament"
        tournamentDto = tournamentService.createTournament(user1.getId(), topics, tournamentDto)

        and: "a new topics list"
        topics.remove(topic2.getId())

        when:
        tournamentService.updateTournament(user2.getId(), topics, tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_CREATOR
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTopicConjunction().getTopics() == [topic2, topic1] as Set
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
