package cn.nero.commons.generator.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;

/**
 * @author Nero Claudius
 * @version 1.0.0
 * @since 2024/3/1
 */
@Getter
public enum JavaTypeEnums {

    /**
     * mysql type 2 java type
     */
    STRING("String", Set.of("char", "varchar", "text", "json")),
    INTEGER("Integer", Set.of("tinyint", "int")),
    LONG("Long", Set.of("bigint")),
    LOCAL_DATE_TIME("java.time.LocalDateTime", Set.of("datetime", "timestamp")),
    LOCAL_DATE("java.time.LocalDate", Set.of("date"))
    ;

    private String javaType;

    private Set<String> mysqlTypes;

    JavaTypeEnums(String javaType, Set<String> mysqlTypes) {
        this.javaType = javaType;
        this.mysqlTypes = mysqlTypes;
    }

    public static JavaTypeEnums fromMysqlType(String mysqlType) {
        return Arrays.stream(JavaTypeEnums.class.getEnumConstants())
                .filter(type -> type.getMysqlTypes().contains(mysqlType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid mysql type : " + mysqlType));
    }

}
