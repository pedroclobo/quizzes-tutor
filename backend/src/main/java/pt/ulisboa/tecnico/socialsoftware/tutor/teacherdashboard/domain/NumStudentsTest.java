ackage pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz

@DataJpaTest
class NumStudentsTest extends SpockTest {
    def "numStudents"() {
        given:
        def courseExecution = new CourseExecution()
        def teacherDashboard = new TeacherDashboard()
        def studentStats = new StudentStats(teacherDashboard, courseExecution)

        when:
        courseExecution.addQuiz(new Quiz())
        courseExecution.addQuiz(new Quiz())
        studentStats.update()

        then:
        quizStats.numQuizzes == 2
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
