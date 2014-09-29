package com.gotcake.json.parser.schema;

import java.io.Reader;
import java.util.*;

/**
 * Created by aaron on 9/28/14.
 */
public class Schema<T extends SchemaDecoder> {

    public enum Type {
        String, Array, Object, Number, Boolean
    }

    private final HashMap<String, Property> properties;
    private final HashSet<String> requiredProps;
    private final Class<T> decoderClass;
    private final boolean validate;
    private final String name;

    public Schema(String name, Class<T> decoderClass, boolean validate) {
        properties = new HashMap<String, Property>();
        requiredProps = new HashSet<String>();
        this.decoderClass = decoderClass;
        this.validate = validate;
        this.name = name;
    }

    public Property defineProperty(String name) {
        Property p = new Property(name, 0);
        properties.put(name, p);
        return p;
    }

    public Property defineArrayProperty(String name, int dimensions) {
        Property p = new Property(name, dimensions);
        properties.put(name, p);
        return p;
    }

    public String getName() {
        return name;
    }

    protected Property getProperty(String name) {
        return properties.get(name);
    }

    public String validateRequiredProperties(Set<String> propertyNames) {
        for (String name: requiredProps) {
            if (!propertyNames.contains(name))
                return "Property "+name+" is required for schema " + this.name;
        }
        return null;
    }

    public String validateProperty(String name, Object val) {
        Property prop = properties.get(name);
        if (prop == null)
            return "Property "+name+" not defined for schema " + this.name;
        if (val == null && prop.required)
            return "Property "+name+" is required for schema " + this.name;
        Class<?> cls = val.getClass();
        if (!prop.isAllowedType(cls))
            return cls.getCanonicalName() + " is not a valid type of property "+name+" of schema " + this.name;
        return null;
    }

    protected T newDecoderInstance() {
        try {
            return decoderClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean shouldValidate() {
        return validate;
    }

    public class Property {

        protected final String name;
        protected final EnumSet<Type> allowedTypes;
        protected boolean required;
        protected final int arrayDimensions;
        protected Schema<?> schema;

        private Property(String name, int arrayDimensions) {
            allowedTypes = EnumSet.noneOf(Type.class);
            this.name = name;
            required = false;
            schema = null;
            this.arrayDimensions = arrayDimensions;
        }

        public Property allow(Type... types) {
            for (Type t: types)
                allowedTypes.add(t);
            return this;
        }

        public Property require() {
            if (!required) {
                required = true;
                requiredProps.add(this.name);
            }
            return this;
        }

        public Property setSchema(Schema<?> s) {
            schema = s;
            return this;
        }

        public boolean isAllowedType(Class<?> cls) {
            if (schema != null) {
                return schema.decoderClass == cls;
            } else if (Number.class.isAssignableFrom(cls)) {
                return allowedTypes.contains(Type.Number);
            } else if (String.class == cls) {
                return allowedTypes.contains(Type.String);
            } else if (Boolean.class == cls) {
                return allowedTypes.contains(Type.Boolean);
            } else if (List.class.isAssignableFrom(cls)) {
                return allowedTypes.contains(Type.Array);
            } else if (Map.class.isAssignableFrom(cls)) {
                return allowedTypes.contains(Type.Object);
            }
            return false;
        }

    }

}
