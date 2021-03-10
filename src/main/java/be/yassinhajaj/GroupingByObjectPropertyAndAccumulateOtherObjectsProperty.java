package be.yassinhajaj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class GroupingByObjectPropertyAndAccumulateOtherObjectsProperty {

    public List<Parent> run(List<Parent> parents) {
        return parents.stream().collect(new CustomCollector());
    }

    private static class CustomCollector implements Collector<Parent, List<Parent>, List<Parent>> {

        @Override
        public Supplier<List<Parent>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<Parent>, Parent> accumulator() {
            return (list, p) -> {
                for (Parent parent : list) {
                    if (parent.getPropertyToGroupBy().equals(p.getPropertyToGroupBy())) {
                        List<Child> children = parent.getChildren();
                        List<Child> childrenToAccumulate = p.getChildren();

                        List<Child> newChildList = new ArrayList<>();
                        newChildList.addAll(children);
                        newChildList.addAll(childrenToAccumulate);

                        parent.setChildren(newChildList);
                        return;
                    }
                }
                list.add(p);
            };
        }

        @Override
        public BinaryOperator<List<Parent>> combiner() {
            return (firstList, secondList) -> {
                List<Parent> parents = new ArrayList<>();
                parents.addAll(firstList);
                parents.addAll(secondList);
                return parents;
            };
        }

        @Override
        public Function<List<Parent>, List<Parent>> finisher() {
            return list -> list;
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
