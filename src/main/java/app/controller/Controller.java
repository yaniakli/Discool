package app.controller;


import app.jpa_repo.*;
import app.model.chat.PublicTextChannel;
import app.model.courses.Course;
import app.model.courses.MoodlePage;
import app.model.users.Group;
import app.model.users.GroupMembers;
import app.model.users.Person;

import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {

    private final PublicTextChannelRepository publicTextChannelRepository;
    private final PublicChatMessageRepository publicChatMessageRepository;
    private final PersonRepository personRepository;
    private final CourseRepository courseRepository;
    private final MoodlePageRepository moodlePageRepository;
    private final GroupRepository groupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final PrivateChatMessageRepository privateChatMessageRepository;

    public Controller(PersonRepository personRepository,
                      PublicTextChannelRepository publicTextChannelRepository,
                      PublicChatMessageRepository publicChatMessageRepository,
                      CourseRepository courseRepository,
                      MoodlePageRepository moodlePageRepository,
                      GroupRepository groupRepository,
                      GroupMembersRepository groupMembersRepository,
                      PrivateChatMessageRepository privateChatMessageRepository) {
        this.publicChatMessageRepository = publicChatMessageRepository;
        this.publicTextChannelRepository = publicTextChannelRepository;
        this.personRepository = personRepository;
        this.courseRepository = courseRepository;
        this.moodlePageRepository = moodlePageRepository;
        this.groupRepository = groupRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.privateChatMessageRepository = privateChatMessageRepository;
    }

    public String getTitleCourse(long id) {
        Optional<Course> c = courseRepository.findById(id);
        return c.map(Course::getName).orElse(null);
    }

    public void deleteNullUsers(){
        personRepository.deleteNullUsers();
    }

    public void updateUserById(long id, String email, String username , String firstname, String lastname, String description, Person.Role role, String website){
        personRepository.updateUserById(id, email,username, firstname, lastname ,description, role, website);
    }

    public void addUser( String username ,String password, Person.Role role, String firstname, String lastname, String email, String description, String website, long firstlogin, long lastlogin, long timecreated){
        personRepository.addUser(username, password, role, firstname, lastname, email, description, website, firstlogin, lastlogin, timecreated);
    }

    public Person findByUsername(String name){
        return personRepository.findByUsername(name);
    }

    public Person findUserById(long id) {
        return personRepository.findById(id);
    }

    public void saveUser(Person p) {
        personRepository.save(p);
    }

    public ArrayList<Person> findAllUsers(){
        return personRepository.findAll();
    }

    public List<Person> searchUser(String searchTerm) {
        return personRepository.search(searchTerm);
    }

    public List<Person> searchByUserName(String searchTerm){
        return personRepository.searchByUserName(searchTerm);
    }

    public List<Person> searchByEmail( String searchTerm){
        return personRepository.searchByEmail(searchTerm);
    }

    public Person getUsername(long id){
        return personRepository.findById(id);
    }

    public boolean userExist(String username) {
        return findByUsername(username) != null;
    }

    public MoodlePage getLastMoodlePage(long courseId){
        return moodlePageRepository.findFirstByCourseIdOrderByIdDesc(courseId);
    }

    public PublicTextChannel getLastTextChannelRepository(long courseId) {
        return publicTextChannelRepository.findFirstByCourseIdOrderByIdDesc(courseId);
    }

    public PublicTextChannel getTextChannel(long textChannelId) {
        return publicTextChannelRepository.findTextChannelById(textChannelId);
    }

    public MoodlePage getMoodlePage(long MoodlePageId) {
        return moodlePageRepository.findMoodlePageById(MoodlePageId);
    }

    public ArrayList<PublicTextChannel> getAllChannelsForCourse(long courseId) {
        return publicTextChannelRepository.findAllByCourseId(courseId);
    }

    public void deletePage(MoodlePage section) {
        moodlePageRepository.delete(section);
    }


    public ArrayList<Person> getAllUsers() {
        return personRepository.findAll();
    }

    public ArrayList<GroupMembers> findByUserId(long id){
        return groupMembersRepository.findByUserId(id);
    }

    public void updateSection(MoodlePage section, String title, String content) {
        section.setTitle(title);
        section.setContent(content);
        moodlePageRepository.save(section);
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
        PublicTextChannel toSave = PublicTextChannel.builder().name(name).courseId(courseId).build();
        publicTextChannelRepository.save(toSave);
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
        groupMembersRepository.deleteByUserId(person.getId());
        privateChatMessageRepository.updateDeletedById(person.getId());
        publicChatMessageRepository.deletePublicChatMessageById(person.getId());
        personRepository.deleteUserById(person.getId());
    }

    /**
     * @param users    the list of users to get the data from
     * @param getNames true to get the names (first+last), false to get the usernames
     * @return an ArrayList of names or usernames
     */
    public ArrayList<String> getAllNamesOrUsernames(ArrayList<Person> users, boolean getNames) {
        ArrayList<String> values = new ArrayList<>();
        if (getNames) {
            users.forEach(user -> values.add(user.getFirstName() + ", " + user.getLastName()));
        } else {
            users.forEach(user -> values.add(user.getUsername()));
        }
        return values;
    }
}
