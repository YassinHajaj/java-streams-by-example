package be.yassinhajaj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class GroupingByObjectPropertyAndAccumulateOtherObjectsProperty {

    public List<Parent> run(List<Parent> parents) {
        return parents.stream().collect(CustomCollector.getInstance());
    }

    private static class CustomCollector implements Collector<Parent, List<Parent>, List<Parent>> {

        private CustomCollector() {
            // Need Factory Method
        }

        public static CustomCollector getInstance() {
            return new CustomCollector();
        }

        @Override
        public Supplier<List<Parent>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<Parent>, Parent> accumulator() {
            return (list, otherParent) -> {
                for (Parent parent : list) {
                    if (parent.getPropertyToGroupBy().equals(otherParent.getPropertyToGroupBy())) {
                        if (otherParent.getChildren() != null) {
                            parent.getChildren().addAll(otherParent.getChildren());
                        }
                        return;
                    }
                }
                if (otherParent.getChildren() == null) {
                    otherParent.setChildren(Collections.emptyList());
                }
                list.add(otherParent);
            };
        }

        @Override
        public BinaryOperator<List<Parent>> combiner() {
            return (firstList, secondList) -> {
                firstList.addAll(secondList);
                return firstList;
            };
        }

        @Override
        public Function<List<Parent>, List<Parent>> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.UNORDERED);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Parent {
        String propertyToGroupBy;
        List<Child> children;
    }

    @Data
    @AllArgsConstructor
    public static class Child {
        String identifier;
    }

}
