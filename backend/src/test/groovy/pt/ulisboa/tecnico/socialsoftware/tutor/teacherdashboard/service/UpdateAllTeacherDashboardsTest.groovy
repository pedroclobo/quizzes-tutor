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

    def "create an empty dashboard"() {
        given: "a teacherDashboard"
        teacherDashboardService.getTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        def result = teacherDashboardRepository.findAll().get(0)
        externalCourseExecution.addQuiz(new Quiz())

        when: "update teacherDashboard"
        teacherDashboardService.updateTeacherDashboard(result.getId())

        then: "updateTeacherDashboard is called"
        result.getQuizStats().get(0).getNumQuizzes() == 1
    }
    /*def "test updateAllTeacherDashboards method"() {
        given:
        student1 = new Student(USER_3_NAME, false)
        userRepository.save(student1)

        when:
        teacherDashboardService.updateAllTeacherDashboards()

        then:
        1 * teacherDashboardService.updateTeacherDashboard(td1.getId())
        1 * teacherDashboardService.updateTeacherDashboard(td2.getId())
    }*/


    def "cannot update all teacher dashboards when there are no dashboards"() {
        when: "the course execution status is updated"
        externalCourseExecution.setStatus(Status.INACTIVE)
        courseExecutionRepository.save(externalCourseExecution)
        teacherDashboardService.updateAllTeacherDashboards()

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_TEACHER_DASHBOARDS
    }

 /*   def "cannot update all teacher dashboards when there are dashboards without a teacher"() {
        given: "a dashboard for a course execution without a teacher"
        teacherDashboardService.createTeacherDashboard(courseExecution.getId(), student1.getId())

        when: "the course execution status is updated"
        courseExecution.setStatus(CourseExecutionStatus.INACTIVE)
        courseExecutionRepository.save(courseExecution)
        teacherDashboardService.updateAllTeacherDashboards()

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_DASHBOARD_NOT_FOUND
    
    }*/
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}