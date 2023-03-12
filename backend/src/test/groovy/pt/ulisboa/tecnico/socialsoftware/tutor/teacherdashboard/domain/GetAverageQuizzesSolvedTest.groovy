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
class GetAverageQuizzesSolvedTest extends SpockTest {
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
        expect: "the average number of unique quizzes solved by student is 0"
        quizStats.getAverageQuizzesSolved() == 0f
    }

    def "create an empty QuizStats and update it"() {
        when: "the quizStats is updated"
        quizStats.update()

        then: "the average number of unique quizzes solved by student is 0"
        quizStats.getAverageQuizzesSolved() == 0f
    }

    def "there are no students in the course execution"() {
        given: "a quiz in a course execution"
        def quiz = createQuiz(1, QUIZ_1_NAME)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        when: "a student not enrolled in the course execution answers the quiz"
        quiz.addQuizAnswer(new QuizAnswer(new Student(USER_2_NAME, false), quiz))
        quizStats.update()

        then: "the average number of unique quizzes solved by student is 0"
        quizStats.getAverageQuizzesSolved() == 0f
    }

    def "add quizzes answered by multiple students to a course execution"() {
        given: "two quizzes and two students"
        def quiz1 = createQuiz(1, QUIZ_1_NAME)
        quiz1.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz1)

        def quiz2 = createQuiz(2, QUIZ_2_NAME)
        quiz2.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz2)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "the quizzes are answered to"
        quiz1.addQuizAnswer(new QuizAnswer(student1, quiz1))
        quiz2.addQuizAnswer(new QuizAnswer(student1, quiz2))
        quiz2.addQuizAnswer(new QuizAnswer(student2, quiz2))

        quizStats.update()

        then: "the average number of unique quizzes solved by student is 1.5"
        quizStats.getAverageQuizzesSolved() == 1.5f
    }

    def "add quizzes answered by all students to a course execution"() {
        given: "two quizzes and two students in a course execution"
        def quiz1 = createQuiz(1, QUIZ_1_NAME)
        quiz1.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz1)

        def quiz2 = createQuiz(2, QUIZ_2_NAME)
        quiz2.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz2)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "each student answers all the quizzes"
        quiz1.addQuizAnswer(new QuizAnswer(student1, quiz1))
        quiz2.addQuizAnswer(new QuizAnswer(student1, quiz2))
        quiz1.addQuizAnswer(new QuizAnswer(student2, quiz1))
        quiz2.addQuizAnswer(new QuizAnswer(student2, quiz2))

        quizStats.update()

        then: "the average number of unique quizzes solved by student is 2"
        quizStats.getAverageQuizzesSolved() == 2f
    }

    def "add quizzes to a course execution where not all students have answered a quiz"() {
        given: "two quizzes and two students in a course execution"
        def quiz1 = createQuiz(1, QUIZ_1_NAME)
        quiz1.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz1)

        def quiz2 = createQuiz(2, QUIZ_2_NAME)
        quiz2.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz2)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "the first student answers both quizzes"
        quiz1.addQuizAnswer(new QuizAnswer(student1, quiz1))
        quiz2.addQuizAnswer(new QuizAnswer(student1, quiz2))

        quizStats.update()

        then: "the average number of unique quizzes solved by student is 1"
        quizStats.getAverageQuizzesSolved() == 1f
    }

    def "add quizzes to a course execution where a student has multiple answers to a quiz"() {
        given: "a quiz and two students in a course execution"
        def quiz = createQuiz(1, QUIZ_1_NAME)
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        def student1 = new Student(USER_2_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        def student2 = new Student(USER_3_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        when: "the first student answers the quiz and the second student answers it three times"
        quiz.addQuizAnswer(new QuizAnswer(student1, quiz))
        quiz.addQuizAnswer(new QuizAnswer(student2, quiz))
        quiz.addQuizAnswer(new QuizAnswer(student2, quiz))
        quiz.addQuizAnswer(new QuizAnswer(student2, quiz))

        quizStats.update()

        then: "the average number of unique quizzes solved by student is 1"
        quizStats.getAverageQuizzesSolved() == 1f
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
