package ${package};

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

<#if hasLocalDateTime>
import java.time.LocalDateTime;
</#if>
<#if hasLocalDate>
import java.time.LocalDateTime;
</#if>


/**
* @author ${author}
* @version 1.0.0
* @since ${currentDate}
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ${tableName}PO {

    <#list columns as column>
    /**
     * ${column.columnComment}
     */
    private ${column.javaType} ${column.columnName};

    </#list>

}