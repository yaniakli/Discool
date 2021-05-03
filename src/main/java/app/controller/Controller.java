package app.controller;


import app.jpa_repo.*;
import app.model.chat.PublicChatMessage;
import app.model.chat.TextChannel;
import app.model.courses.Course;
import app.model.courses.MoodlePage;
import app.model.users.Group;
import app.model.users.GroupMembers;
import app.model.users.Person;

import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {

    private final TextChannelRepository textChannelRepository;
    private final PublicChatMessageRepository publicChatMessageRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final MoodlePageRepository moodlePageRepository;
    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;

    public Controller(PersonRepository personRepository,
                      TextChannelRepository textChannelRepository,
                      PublicChatMessageRepository publicChatMessageRepository,
                      CourseRepository courseRepository,
                      MoodlePageRepository moodlePageRepository,
                      GroupRepository groupRepository,
                      GroupMembersRepository groupMembersRepository) {
        this.publicChatMessageRepository = publicChatMessageRepository;
        this.textChannelRepository = textChannelRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.moodlePageRepository = moodlePageRepository;
        this.groupRepository = groupRepository;
        this.groupMembersRepository = groupMembersRepository;
    }

    public String getTitleCourse(long id) {
        Optional<Course> c = courseRepository.findById(id);
        return c.map(Course::getName).orElse(null);
    }

    public PublicChatMessage saveMessage(String message, long channelId, long parentId, long userId) {
        PublicChatMessage messageToSave = PublicChatMessage.builder()
                .message(message)
                .channelid(channelId)
                .parentId(parentId)
                .sender(userId)
                .timeCreated(System.currentTimeMillis())
                .deleted(false)
                .build();
        publicChatMessageRepository.save(messageToSave);
        return messageToSave;
    }



    public MoodlePage getLastMoodlePage(long courseId){
        return moodlePageRepository.findFirstByCourseIdOrderByIdDesc(courseId);
    }

    public TextChannel getLastTextChannelRepository(long courseId){
        return textChannelRepository.findFirstByCourseIdOrderByIdDesc(courseId);
    }

    public TextChannel getTextChannel(long textChannelId){
        return textChannelRepository.findTextChannelById(textChannelId);
    }

    public MoodlePage getMoodlePage(long MoodlePageId){
        return moodlePageRepository.findMoodlePageById(MoodlePageId);
    }

    public void changeMessage(PublicChatMessage publicChatMessage, String messageText) {
        publicChatMessageRepository.updateMessageById(publicChatMessage.getId(), messageText);
    }

    public void saveMessage(PublicChatMessage message) {
        publicChatMessageRepository.save(message);
    }

    public ArrayList<TextChannel> getAllChannelsForCourse(long courseId) {
        return textChannelRepository.findAllByCourseId(courseId);
    }

    public void deleteMessage(PublicChatMessage message) {
        publicChatMessageRepository.updateDeletedById(message.getId());
    }

    public void deletePage(MoodlePage section) {
        moodlePageRepository.delete(section);
    }

    public String getUsernameOfSender(PublicChatMessage publicChatMessage) {
        return personRepository.findById(publicChatMessage.getSender()).getUsername();
    }

    public ArrayList<PublicChatMessage> getChatMessagesForChannel(long channelId) {
        return publicChatMessageRepository.findAllByChannelid(channelId);
    }

    public ArrayList<Person> getAllUser() {
        return personRepository.findAll();
    }


    public void updateSection(MoodlePage section, String title, String content) {
        section.setTitle(title);
        section.setContent(content);
        moodlePageRepository.save(section);
    }

    public void clearMessageChat() {
        publicChatMessageRepository.updateDeletedAll();
    }

    public void clearMessageChat(int value, long channelid) {
        publicChatMessageRepository.updateDeleted(channelid, value);
    }

    public PublicChatMessage getMessageById(long id) {
        return publicChatMessageRepository.findById(id);
    }

    public ArrayList<Person> getAllStudentsForCourse(long courseId) {
        ArrayList<Group> groups = groupRepository.findAllByCourseId(courseId);
        ArrayList<GroupMembers> members = new ArrayList<>();
        groups.forEach(group -> members.addAll(groupMembersRepository.findByGroupId(group.getId())));
        Set<Person> students = new LinkedHashSet<>(); // Set doesn't allow duplicates
        members.forEach(m -> {
            Person p = personRepository.findById(m.getUserId());
            if (Person.Role.STUDENT.equals(p.getRole())) students.add(p);
        });
        return new ArrayList<>(students);
    }

    public ArrayList<Person> getAllUsersForCourse(long courseId) {
        ArrayList<Group> groups = groupRepository.findAllByCourseId(courseId);
        ArrayList<GroupMembers> members = new ArrayList<>();
        groups.forEach(group -> members.addAll(groupMembersRepository.findByGroupId(group.getId())));
        Set<Person> users = new LinkedHashSet<>(); // Set doesn't allow duplicates
        members.forEach(m -> users.add(personRepository.findById(m.getUserId())));
        return new ArrayList<>(users);
    }

    public void createChannel(String name, long courseId) {
        TextChannel toSave = TextChannel.builder().name(name).courseId(courseId).build();
        textChannelRepository.save(toSave);
    }

    public void createMoodlePage(String title, long courseId) {
        MoodlePage toSave = MoodlePage.builder().courseId(courseId).title(title).content("").homePage(false).build();
        moodlePageRepository.save(toSave);
    }

    public String convertLongToDate(long dateLong) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat heure = new SimpleDateFormat("HH:mm");
        Date date = new Date(dateLong);
        if (formatter.format(date).equals(formatter.format(new Date(System.currentTimeMillis()))))
            return "Aujourd'hui à " + heure.format(date);
        return formatter.format(date) + " à " + heure.format(date);
    }

    /**
     * Returns the id of the moodle home page
     * @param courseId  the id of the course
     * @return the id of the homepage
     */
    public long findHomePageId(long courseId) {
        ArrayList<MoodlePage> pages = moodlePageRepository.findAllByCourseId(courseId);
        for (MoodlePage moodlePage : pages) {
            if (moodlePage.isHomePage()) {
                return moodlePage.getId();
            }
        }
        return 0; // no homepage for this course, should throw an error
    }

    public ArrayList<MoodlePage> getAllMoodlePagesForCourse(long courseId) {
        return moodlePageRepository.findAllByCourseId(courseId);
    }

    /**
     * Creates a server and the associated homepage
     * @param teacherId The id of the teacher
     * @param title     The name of the course
     * @param iconPath  The path to the course's picture
     */
    public void createServer(long teacherId, String title, String iconPath) {
        Course toSave = Course.builder()
                .teacherId(teacherId)
                .name(title)
                .pathIcon(iconPath).build();
        courseRepository.save(toSave);
        MoodlePage moodlePage = MoodlePage.builder()
                .homePage(true)
                .content("")
                .title("Homepage")
                .courseId(toSave.getId()).build();
        moodlePageRepository.save(moodlePage);
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public List<MoodlePage> getAllMoodlePageForCourse(long courseID) {
        return moodlePageRepository.findAllByCourseId(courseID);
    }

    public void deleteUser(Person person) {
        personRepository.deleteUserByIdGroup_members(person.getId());
        personRepository.deleteUserByIdIndirect_messages(person.getId());
        personRepository.updateUserByIdPosts(person.getId());
        personRepository.deleteUserById(person.getId());
    }
}
