package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

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

    public void updateNumAvailable() {
       setNumAvailable(courseExecution.getNumberOfQuestions());
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void update() {
        updateNumAvailable();
    }

    @Override
    public String toString() {
        return "QuestionStats{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacherDashboard=" + teacherDashboard +
                ", numAvailable=" + numAvailable +
                '}';
    }

}
