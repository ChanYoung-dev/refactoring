package me.whiteship.refactoring._11_primitive_obsession._31_replace_type_code_with_subclasses.direct_inheritance;

public abstract class Employee {

    private String name;


    protected Employee(String name) {
        this.name = name;
    }

    public static Employee createEmployee(String name, String type){
        switch (type) {
            case "engineer": return new Engineer(name);
            default: throw new IllegalArgumentException(type);
        }
    }

//    private void validate(String type) {
//        List<String> legalTypes = List.of("engineer", "manager", "salesman");
//        if (!legalTypes.contains(type)) {
//            throw new IllegalArgumentException(type);
//        }
//    }

    public abstract String getType();

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }
}
