package be.yassinhajaj;

import be.yassinhajaj.GroupingByObjectPropertyAndAccumulateOtherObjectsProperty.Child;
import be.yassinhajaj.GroupingByObjectPropertyAndAccumulateOtherObjectsProperty.Parent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class GroupingByObjectPropertyAndAccumulateOtherObjectsPropertyTest {

    private static final GroupingByObjectPropertyAndAccumulateOtherObjectsProperty BEAN = new GroupingByObjectPropertyAndAccumulateOtherObjectsProperty();

    @Test
    @DisplayName("Given a list of parents with same property to group by, when merged, only a parent of each property is present and it contains all the children")
    public void parentToChildrenTest() {
        Parent firstParent = new Parent("property-one", mutableListOf(
                new Child("first"),
                new Child("second"),
                new Child("third"),
                new Child("fourth"),
                new Child("fifth")
        ));

        Parent secondParent = new Parent("property-two", mutableListOf(
                new Child("sixth"),
                new Child("seventh"),
                new Child("eighth")
        ));

        Parent thirdParent = new Parent("property-one", mutableListOf(
                new Child("ninth"),
                new Child("tenth")
        ));

        Parent fourthParent = new Parent("property-three", Collections.emptyList());

        Parent fifthParent = new Parent("property-four", null);

        Parent sixthParent = new Parent("property-four", null);

        Parent seventhParent = new Parent("property-five", null);

        List<Parent> parents = mutableListOf(firstParent, secondParent, thirdParent, fourthParent, fifthParent, sixthParent, seventhParent);

        List<Parent> result = BEAN.run(parents);

        assertThat(result).isNotNull().hasSize(5);
        List<String> propertiesToGroupBy = result.stream().map(Parent::getPropertyToGroupBy).collect(Collectors.toList());
        assertThat(propertiesToGroupBy).containsExactly("property-one", "property-two", "property-three", "property-four", "property-five");

        Parent propertyOneParent = result.stream().filter(parent -> parent.getPropertyToGroupBy().equals("property-one")).findFirst().get();
        assertThat(propertyOneParent.getChildren()).hasSize(7);
        assertThat(propertyOneParent.getChildren().stream().map(Child::getIdentifier).collect(Collectors.toList())).containsExactly("first", "second", "third", "fourth", "fifth", "ninth", "tenth");

        Parent propertyTwoParent = result.stream().filter(parent -> parent.getPropertyToGroupBy().equals("property-two")).findFirst().get();
        assertThat(propertyTwoParent.getChildren()).hasSize(3);
        assertThat(propertyTwoParent.getChildren().stream().map(Child::getIdentifier).collect(Collectors.toList())).containsExactly("sixth", "seventh", "eighth");

        Parent propertyThreeParent = result.stream().filter(parent -> parent.getPropertyToGroupBy().equals("property-three")).findFirst().get();
        assertThat(propertyThreeParent.getChildren()).isEmpty();

        Parent propertyFourParent = result.stream().filter(parent -> parent.getPropertyToGroupBy().equals("property-four")).findFirst().get();
        assertThat(propertyFourParent.getChildren()).isEmpty();

        Parent propertyFiveParent = result.stream().filter(parent -> parent.getPropertyToGroupBy().equals("property-five")).findFirst().get();
        assertThat(propertyFiveParent.getChildren()).isEmpty();
    }

    private static <T> List<T> mutableListOf(T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }
}
