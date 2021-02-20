package app.web;

import app.jpa_repo.CourseSectionRepository;
import app.model.courses.CourseSection;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * test class to try to display a Moodle page
 */
@Route("")
public class MoodleView extends VerticalLayout {
    private final CourseSectionRepository repo;
    private final VerticalLayout sectionList = new VerticalLayout();

    public MoodleView(@Autowired CourseSectionRepository repo) {
        this.repo = repo;
        add(new H1("Test d'affichage de la page Moodle"));
        add(sectionList);
        refresh();
    }

    private void refresh() {
        ArrayList<CourseSection> list = repo.findAllSectionsByCourseId(1); // get all the sections from the table
        list.forEach(courseSection -> courseSection.addParent(repo)); // add the parent for each element
        LinkedList<CourseSection> sortedList = CourseSection.sort(list); // sort the sections in the right order
        sortedList.stream()
                .map(SectionLayout::new)    // create a new SectionLayout for each
                .forEach(sectionList::add); // add this SectionLayout into the sectionList layout to be displayed
    }

    class SectionLayout extends VerticalLayout {
        TextField title = new TextField(); // will be filled with the value of the title field in the CourseSection
        TextArea content = new TextArea(); // same with the content field

        public SectionLayout(CourseSection section) {
            add(title, content);

            // The UI fields on *this* will be bound to the fields of the same name in the CourseSection class,
            // taking the value from the *section* object
            Binder<CourseSection> binder = new Binder<>(CourseSection.class);
            binder.bindInstanceFields(this);
            binder.setBean(section);

            // for when the values of the fields change
            binder.addValueChangeListener(e -> {
                repo.save(binder.getBean()); // add the new one
                refresh();
            });
        }
    }
}
