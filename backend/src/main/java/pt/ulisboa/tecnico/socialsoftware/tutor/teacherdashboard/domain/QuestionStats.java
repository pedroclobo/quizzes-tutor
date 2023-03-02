package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import java.util.HashSet;
import java.util.Set;

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
    private float averageQuestionsAnswered;

    public QuestionStats() {
    }

    public QuestionStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
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

    public float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public void updateNumAvailable() {
       setNumAvailable(courseExecution.getNumberOfQuestions());
    }

    public void updateAverageQuestionsAnswered() {
        int numAnswers = 0;
        Set<Student> students = courseExecution.getStudents();
        Set<Question> uniqueQuestions = new HashSet<>();

        for (Student student : students) {
            Set<QuestionSubmission> questionSubmissions = student.getQuestionSubmissions();
            for (QuestionSubmission questionSubmission : questionSubmissions) {
                Question question = questionSubmission.getQuestion();
                if (!uniqueQuestions.contains(question)) {
                    uniqueQuestions.add(question);
                }
            }
            numAnswers += uniqueQuestions.size();
            uniqueQuestions.clear();
        }

        int numStudents = students.size();

        if (numStudents == 0) {
            setAverageQuestionsAnswered(0);
        } else {
            setAverageQuestionsAnswered((float) numAnswers / numStudents);
        }
    }


    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void update() {
        updateNumAvailable();
        updateAverageQuestionsAnswered();
    }

    @Override
    public String toString() {
        return "QuestionStats{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacherDashboard=" + teacherDashboard +
                ", numAvailable=" + numAvailable +
                ", averageQuestionsAnswered=" + averageQuestionsAnswered +
                '}';
    }

}
