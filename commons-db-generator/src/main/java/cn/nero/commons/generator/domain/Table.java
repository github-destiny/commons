package cn.nero.commons.generator.domain;

import cn.nero.commons.generator.constant.JavaTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
public class Table {

    private String schema;

    private String name;

    private String comment;

    private List<Column> columns;

    private Boolean hasLocalDateTime = false;

    private Boolean hasLocalDate = false;

    public void setColumns(List<Column> columns) {
        this.columns = columns;
        Set<String> dataTypes = this.columns.stream().map(Column::getDataType).collect(Collectors.toSet());
        dataTypes.forEach(type -> {
            if (JavaTypeEnums.LOCAL_DATE_TIME.getMysqlTypes().contains(type)) {
                this.hasLocalDateTime = true;
            }

            if (JavaTypeEnums.LOCAL_DATE.getMysqlTypes().contains(type)) {
                this.hasLocalDate = true;
            }
        });
    }

}
