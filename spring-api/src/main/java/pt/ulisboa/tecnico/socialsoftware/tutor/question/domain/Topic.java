package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "topics")
public class Topic implements Serializable {
    @SuppressWarnings("unused")
    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany
    private Set<Question> questions = new HashSet<>();

    @ManyToOne
    private Topic parentTopic;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentTopic", fetch=FetchType.EAGER)
    private Set<Topic> childrenTopics = new HashSet<>();

    @ManyToMany
    private List<TopicConjuction> topicConjuctions = new ArrayList<>();

    public Topic() {
    }

    public Topic(TopicDto topicDto) {
        this.name = topicDto.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Topic getParentTopic() {
        return parentTopic;
    }

    public void setParentTopic(Topic parentTopic) {
        this.parentTopic = parentTopic;
    }

    public Set<Topic> getChildrenTopics() {
        return childrenTopics;
    }

    public void setChildrenTopics(Set<Topic> childrenTopics) {
        this.childrenTopics = childrenTopics;
    }

    public List<TopicConjuction> getTopicConjuctions() {
        return topicConjuctions;
    }

    public void setTopicConjuctions(List<TopicConjuction> topicConjuctions) {
        this.topicConjuctions = topicConjuctions;
    }

    public void addTopicConjuction(TopicConjuction topicConjuction) {
        this.topicConjuctions.add(topicConjuction);
    }

    public void remove() {
        getQuestions().forEach(question -> question.getTopics().remove(this));
        getQuestions().clear();

        if (this.parentTopic != null) {
            parentTopic.getChildrenTopics().remove(this);
            parentTopic.getChildrenTopics().addAll(this.getChildrenTopics());

        }

        this.childrenTopics.forEach(topic -> topic.parentTopic = this.parentTopic);

        this.parentTopic = null;
        this.childrenTopics.clear();
    }


}
