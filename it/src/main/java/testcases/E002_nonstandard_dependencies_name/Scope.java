package testcases.E002_nonstandard_dependencies_name;

@motif.Scope
public interface Scope {

    String string();

    @motif.Objects
    class Objects {}

    @motif.Dependencies
    interface MyDependencies {}
}