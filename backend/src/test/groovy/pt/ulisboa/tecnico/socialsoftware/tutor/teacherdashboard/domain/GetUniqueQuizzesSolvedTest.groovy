package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer

@DataJpaTest
class GetUniqueQuizzesSolvedTest extends SpockTest {
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
        expect: "the number of unique quizzes solved is 0"
        quizStats.getUniqueQuizzesSolved() == 0
    }

    def "create an empty QuizStats and update it"() {
        when: "the quizStats is updated"
        quizStats.update()

        then: "the number of unique quizzes solved is 0"
        quizStats.getUniqueQuizzesSolved() == 0
    }

    def "add an answer to a quiz"() {
        given: "a quiz and a student in a course execution"
        def quiz = createQuiz(1, QUIZ_1_NAME)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        def student = new Student(USER_2_NAME, false)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        when: "a quiz with an answer is added to the course execution"
        def quizAnswer = new QuizAnswer(student, quiz)
        quizStats.update()

        then: "the number of unique quizzes solved is 1"
        quizStats.getUniqueQuizzesSolved() == 1
    }

    def "add two quizzes with answers to a course execution and remove the answers"() {
        given: "two quizzes and a student in a course execution"
        def quiz1 = createQuiz(1, QUIZ_1_NAME)
        quiz1.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz1)

        def quiz2 = createQuiz(2, QUIZ_2_NAME)
        quiz2.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz2)

        def student = new Student(USER_2_NAME, false)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        when: "the quizzes are added to the course execution"
        def quiz1Answer = new QuizAnswer(student, quiz1)
        def quiz2Answer = new QuizAnswer(student, quiz2)

        quizStats.update()

        then: "the number of unique quizzes solved is 2"
        quizStats.getUniqueQuizzesSolved() == 2

        when: "the first quiz's answer is removed"
        quiz1Answer.remove()
        quizStats.update()

        then: "the number of unique quizzes solved is 1"
        quizStats.getUniqueQuizzesSolved() == 1

        when: "the second quiz's answer is removed"
        quiz2Answer.remove()
        quizStats.update()

        then: "the number of unique quizzes solved is 0"
        quizStats.getUniqueQuizzesSolved() == 0
    }

    def "add a quiz with two answers from the same student to the course execution"() {
        given: "a quiz and a student in a course execution"
        def quiz = createQuiz(1, QUIZ_1_NAME)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        def student = new Student(USER_2_NAME, false)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        when: "the quizz is added to the course execution with duplicate answers from the student"
        def quizAnswer1 = new QuizAnswer(student, quiz)
        def quizAnswer2 = new QuizAnswer(student, quiz)
        quizStats.update()

        then: "the number of unique quizzes solved is 1"
        quizStats.getUniqueQuizzesSolved() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
