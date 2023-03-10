package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.*;
import java.util.List;


public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;
    private List<Integer> numOfStudents;
    
    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();

        this.numOfStudents = teacherDashboard.getStudentStats().stream()
        .map(StudentStats::getNumStudents)
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

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                ", numOfStudents=" + this.getNumOfStudents() +
                "}";
    }
}
