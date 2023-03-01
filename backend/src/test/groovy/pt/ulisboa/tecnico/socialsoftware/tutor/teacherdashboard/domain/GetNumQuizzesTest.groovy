package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto

@DataJpaTest
class GetNumQuizzesTest extends SpockTest {
    def teacherDashboard
    def quizStats

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)
    }

    def "create an empty QuizStats"() {
        expect: "the number of quizzes is 0"
        quizStats.getNumQuizzes() == 0
    }

    def "create an empty QuizStats and update it"() {
        when: "the quizStats is updated"
        quizStats.update()

        then: "the number of quizzes is 0"
        quizStats.getNumQuizzes() == 0
    }

    def "add a quiz to a course execution"() {
        given: "a quiz"
        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setTitle("Quiz 1")
        quizDto.setScramble(true)
        quizDto.setOneWay(true)
        quizDto.setQrCodeOnly(true)
        quizDto.setAvailableDate(STRING_DATE_TODAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto.setResultsDate(STRING_DATE_LATER)
        def quiz = new Quiz(quizDto)

        when: "the quiz is added to the course execution"
        externalCourseExecution.addQuiz(quiz)
        quizStats.update()

        then: "the number of quizzes is 1"
        quizStats.numQuizzes == 1
    }

    def "add two quizzes to a course execution and remove them"() {
        given: "two quizzes"
        def quizDto1 = new QuizDto()
        quizDto1.setKey(1)
        quizDto1.setTitle("Quiz 1")
        quizDto1.setScramble(true)
        quizDto1.setOneWay(true)
        quizDto1.setQrCodeOnly(true)
        quizDto1.setAvailableDate(STRING_DATE_TODAY)
        quizDto1.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto1.setResultsDate(STRING_DATE_LATER)
        def quiz1 = new Quiz(quizDto1)

        def quizDto2 = new QuizDto()
        quizDto2.setKey(1)
        quizDto2.setTitle("Quiz 1")
        quizDto2.setScramble(true)
        quizDto2.setOneWay(true)
        quizDto2.setQrCodeOnly(true)
        quizDto2.setAvailableDate(STRING_DATE_TODAY)
        quizDto2.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto2.setResultsDate(STRING_DATE_LATER)
        def quiz2 = new Quiz(quizDto2)

        when: "the two quizzes are added to the course execution"
        quiz1.setCourseExecution(externalCourseExecution)
        quiz2.setCourseExecution(externalCourseExecution)
        externalCourseExecution.addQuiz(quiz1)
        externalCourseExecution.addQuiz(quiz2)
        quizStats.update()

        then: "the number of quizzes is 2"
        quizStats.numQuizzes == 2

        when: "the first quiz is from the course execution"
        quiz1.remove()
        quizStats.update()

        then: "the number of quizzes is 1"
        quizStats.numQuizzes == 1

        when: "the other quiz is removed from the course execution"
        quiz2.remove()
        quizStats.update()

        then: "the number of quizzes is 0"
        quizStats.numQuizzes == 0
    }

    def "add the same quiz twice to a course execution"() {
        given: "a quiz"
        def quizDto = new QuizDto()
        quizDto.setKey(1)
        quizDto.setTitle("Quiz 1")
        quizDto.setScramble(true)
        quizDto.setOneWay(true)
        quizDto.setQrCodeOnly(true)
        quizDto.setAvailableDate(STRING_DATE_TODAY)
        quizDto.setConclusionDate(STRING_DATE_TOMORROW)
        quizDto.setResultsDate(STRING_DATE_LATER)
        def quiz = new Quiz(quizDto)

        when: "the quiz is added to the course execution"
        externalCourseExecution.addQuiz(quiz)
        quizStats.update()

        then: "the number of quizzes is 1"
        quizStats.numQuizzes == 1

        when: "the same quiz is added to the course execution"
        externalCourseExecution.addQuiz(quiz)
        quizStats.update()

        then: "the number of quizzes is still 1"
        quizStats.numQuizzes == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
