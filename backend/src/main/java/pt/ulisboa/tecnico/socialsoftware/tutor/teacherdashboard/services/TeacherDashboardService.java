package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;

import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TeacherDashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @Autowired
    private QuestionStatsRepository questionStatsRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto getTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        Optional<TeacherDashboard> dashboardOptional = teacher.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        return dashboardOptional.
                map(TeacherDashboardDto::new).
                orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto createTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(TEACHER_ALREADY_HAS_DASHBOARD);

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        return createAndReturnTeacherDashboardDto(courseExecution, teacher);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution, Teacher teacher) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);

        // Get course execution ids from the current course
        // associated with the teacher and sort them by year
        Integer courseId = courseExecution.getCourse().getId();
        List<Integer> courseExecutionIds = new ArrayList<>(courseExecutionRepository.getCourseExecutionsIdByCourseId(courseId).stream().filter(id -> {
            // Filter out course executions that do not have a valid getYear()
            try {
                CourseExecution execution = courseExecutionRepository.getById(id);
                execution.getYear();
                return execution.getTeachers().stream().anyMatch(t -> t.getId() == teacher.getId());
            } catch (IllegalStateException e) {
                return false;
            }
        }).collect(Collectors.toList()));

        courseExecutionIds.sort((id1, id2) -> {
            CourseExecution ce1 = courseExecutionRepository.getById(id1);
            CourseExecution ce2 = courseExecutionRepository.getById(id2);
            return Integer.compare(ce2.getYear(), ce1.getYear());
        });

        // Create the stats for the latest 3 course executions and update them
        for (int i = 0; i < Math.min(courseExecutionIds.size(), 3); i++) {
            CourseExecution latestCourseExecution = courseExecutionRepository.findById(courseExecutionIds.get(i)).orElse(null);

            QuizStats quizStats = new QuizStats(latestCourseExecution, teacherDashboard);
            StudentStats studentStats = new StudentStats(latestCourseExecution, teacherDashboard);
            QuestionStats questionStats = new QuestionStats(latestCourseExecution, teacherDashboard);

            quizStats.update();
            studentStats.update();
            questionStats.update();

            quizStatsRepository.save(quizStats);
            studentStatsRepository.save(studentStats);
            questionStatsRepository.save(questionStats);
        }

        teacherDashboardRepository.save(teacherDashboard);

        return new TeacherDashboardDto(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.remove();
        teacherDashboardRepository.delete(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateTeacherDashboard(int dashboardId) {
        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.update();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAllTeacherDashboards() {
        List<TeacherDashboard> dashboards = teacherDashboardRepository.findAll();
        if(dashboards.size() == 0) {
            throw new TutorException(NO_TEACHER_DASHBOARDS);
        }
        for(TeacherDashboard dashboard : dashboards) {
            updateTeacherDashboard(dashboard.getId());
        }
    } 
}
