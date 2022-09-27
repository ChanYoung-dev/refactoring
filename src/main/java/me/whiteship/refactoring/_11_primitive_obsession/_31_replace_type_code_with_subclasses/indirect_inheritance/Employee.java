package me.whiteship.refactoring._11_primitive_obsession._31_replace_type_code_with_subclasses.indirect_inheritance;


public class Employee {

    private String name;

//    private String typeValue;

    private EmployeeType type;

    public Employee(String name, String typeValue) {
        this.name = name;
        this.type = this.getEmployeeType(typeValue);
    }

    public static EmployeeType getEmployeeType(String typeValue){
        switch (typeValue) {
            case "engineer": return new Engineer();
            default: throw new IllegalArgumentException(typeValue);
        }
    }

    //EmployeeType로 올린다
    public String capitalizedType() {
        return this.type.capitalizedType();
//        return this.typeValue.substring(0, 1).toUpperCase() + this.typeValue.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + type.toString() + '\'' +
                '}';
    }
}
