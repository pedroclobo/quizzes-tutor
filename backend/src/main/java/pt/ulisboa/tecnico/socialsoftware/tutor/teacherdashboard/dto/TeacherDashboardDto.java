package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.*;
import java.util.List;


public class TeacherDashboardDto {

    private Integer id;
    private Integer numberOfStudents;
    private List<Integer> numOfStudents;
    private List<Integer> numMore75CorrectQuestions;
    private List<Integer> numAtLeast3Quizzes;
    
    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();

        this.numOfStudents = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumStudents)
        .collect(Collectors.toList());

        this.numMore75CorrectQuestions = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumMore75CorrectQuestions)
        .collect(Collectors.toList());

        this.numAtLeast3Quizzes = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumAtLeast3Quizzes)
        .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public List<Integer> getNumOfStudents() {
        return numOfStudents;
    }

    public void setNumOfStudents(List<Integer> numOfStudents) {
        this.numOfStudents = numOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public void setNumMore75CorrectQuestions(List<Integer> newNumMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = newNumMore75CorrectQuestions;
    }

    public List<Integer> getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumAtLeast3Quizzes(List<Integer> numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public List<Integer> getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                ", numOfStudents=" + this.getNumOfStudents() +
                ", numMore75CorrectQuestions=" + this.getNumMore75CorrectQuestions() +
                ", numAtLeast3Quizzes=" + this.getNumAtLeast3Quizzes() +
                "}";
    }
}
