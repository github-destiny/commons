package ${package};

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* @author Nero Claudius
* @version 1.0.0
* @since 2024/3/1
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ${tableName} {

    <#list columns as column>

        /**
         * ${column.comment}
         */
        private ${column.javaType} ${column.columnName};

    </#list>

}