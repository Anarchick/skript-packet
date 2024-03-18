package fr.anarchick.skriptpacket.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

import java.util.List;

@Name("Is Java List")
@Description("Check if a given object is an instance of java.util.List")
@Examples("if {_something} is Java List:")
@Since("2.2.0")

public class IsJavaList extends PropertyCondition<Object> {

    static {
        register(IsJavaList.class, "Java List", "objects");
    }

    @Override
    public boolean check(Object value) {
        return value instanceof List<?>;
    }

    @Override
    protected String getPropertyName() {
        return "Java List";
    }

}
