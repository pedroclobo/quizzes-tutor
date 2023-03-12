package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.Status;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import javax.persistence.*;

@Entity
public class QuestionStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private int numAvailable;
    private int uniqueQuestionsAnswered;
    private float averageQuestionsAnswered;

    public QuestionStats() {
    }

    public QuestionStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
        numAvailable = 0;
        uniqueQuestionsAnswered = 0;
        averageQuestionsAnswered = 0.0f;
    }

    public void remove() {
        teacherDashboard.getQuestionStats().remove(this);
        teacherDashboard = null;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
        this.teacherDashboard.addQuestionStats(this);
    }

    public int getNumAvailable() {
        return numAvailable;
    }

    public void setNumAvailable(int numAvailable) {
        this.numAvailable = numAvailable;
    }

    public int getUniqueQuestionsAnswered() {
        return uniqueQuestionsAnswered;
    }

    public void setUniqueQuestionsAnswered(int uniqueQuestionsAnswered) {
        this.uniqueQuestionsAnswered = uniqueQuestionsAnswered;
    }

    public float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public void updateNumAvailable() {
        this.numAvailable = (int) courseExecution.getQuizzes().stream()
            .flatMap(q -> q.getQuizQuestions().stream())
            .map(QuizQuestion::getQuestion)
            .filter(q -> q.getStatus() == Status.AVAILABLE)
            .distinct()
            .count();
    }

    public void updateUniqueQuestionsAnswered() {
        this.uniqueQuestionsAnswered = (int) courseExecution.getQuizzes().stream()
                .flatMap(q -> q.getQuizAnswers() .stream()
                    .flatMap(qa -> qa.getQuestionAnswers().stream()
                        .map(QuestionAnswer::getQuestion)))
            .distinct()
            .count();
    }

    public void updateAverageQuestionsAnswered() {
        int students = courseExecution.getStudents().size();

        long uniqueAllStudents = courseExecution.getStudents().stream().mapToLong(student -> 
            student.getQuizAnswers().stream().flatMap(
                qa -> qa.getQuestionAnswers().stream().map(QuestionAnswer::getQuestion)
            ).distinct().count()).sum();

        this.averageQuestionsAnswered = students > 0 ? (float) uniqueAllStudents / students : 0.0f;
    }


    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void update() {
        updateNumAvailable();
        updateUniqueQuestionsAnswered();
        updateAverageQuestionsAnswered();
    }


    @Override
    public String toString() {
        return "QuestionStats{" +
            "id=" + id +
            ", courseExecution=" + courseExecution +
            ", teacherDashboard=" + teacherDashboard +
            ", numAvailable=" + numAvailable +
            ", uniqueQuestionsAnswered=" + uniqueQuestionsAnswered +
            ", averageQuestionsAnswered=" + averageQuestionsAnswered +
            '}';
      }

}
