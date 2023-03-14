package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution.Status
import spock.lang.Unroll

import javax.transaction.Transactional

@DataJpaTest
@Transactional
class UpdateAllTeacherDashboardsTest extends SpockTest {
    def teacher

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        teacher.addCourse(externalCourseExecution)
    }

    def "updateTeacherDashboard"() {
        given: "a teacherDashboard"
        teacherDashboardService.getTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        def result = teacherDashboardRepository.findAll().get(0)
        externalCourseExecution.addQuiz(new Quiz())

        when: "update teacherDashboard"
        teacherDashboardService.updateTeacherDashboard(result.getId())

        then: "updateTeacherDashboard is called"
        result.getQuizStats().get(0).getNumQuizzes() == 1
    }

    def "updateAllTeacherDashboards"() {
        given: "a teacherDashboard"
        teacherDashboardService.getTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        Quiz quiz = new Quiz()
        quiz.setCourseExecution(externalCourseExecution)

        when: "update all teacherDashboards, just one"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "updateTeacherDashboard is called once"
        def result = teacherDashboardRepository.findAll().get(0)
        result.getQuizStats().get(0).getNumQuizzes() == 1
    }

    def "cannot update all teacher dashboards when there are no dashboards"() {
        when: "the course execution status is updated"
        externalCourseExecution.setStatus(Status.INACTIVE)
        courseExecutionRepository.save(externalCourseExecution)
        teacherDashboardService.updateAllTeacherDashboards()

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_TEACHER_DASHBOARDS
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
